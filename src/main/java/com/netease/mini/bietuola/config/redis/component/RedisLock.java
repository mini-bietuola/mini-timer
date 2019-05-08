package com.netease.mini.bietuola.config.redis.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created On 10/24 2017
 * Redis实现的分布式锁
 * 此对象非线程安全，使用时务必注意
 *
 */
public class RedisLock {

    private static final Logger logger = LoggerFactory.getLogger(RedisLock.class);

    /**
     * 利用ThreadLocal实现可重入
     */
    public static ThreadLocal<Map<String, RedisLock>> locks = new ThreadLocal<Map<String, RedisLock>>() {
        @Override
        protected Map<String, RedisLock> initialValue() {
            return new HashMap<>();
        }
    };

    private final StringRedisTemplate stringRedisTemplate;

    private final String lockKey;

    private final String lockValue;

    private boolean locked = false;

    private int enterTime = 0;

    private int tryIntervalMillis = 50;

    public int getTryIntervalMillis() {
        return tryIntervalMillis;
    }

    public void setTryIntervalMillis(int tryIntervalMillis) {
        this.tryIntervalMillis = tryIntervalMillis;
    }

    public int getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(int enterTime) {
        this.enterTime = enterTime;
    }

    /**
     * 使用脚本在redis服务器执行这个逻辑可以在一定程度上保证此操作的原子性
     * （即不会发生客户端在执行setNX和expire命令之间，发生崩溃或失去与服务器的连接导致expire没有得到执行，发生永久死锁）
     * <p>
     * 除非脚本在redis服务器执行时redis服务器发生崩溃，不过此种情况锁也会失效
     */
    private static final RedisScript<Boolean> SETNX_AND_EXPIRE_SCRIPT;

    static {
        String sb = "if (redis.call('setnx', KEYS[1], ARGV[1]) == 1) then\n" +
                "\tredis.call('expire', KEYS[1], tonumber(ARGV[2]))\n" +
                "\treturn true\n" +
                "else\n" +
                "\treturn false\n" +
                "end";
        SETNX_AND_EXPIRE_SCRIPT = RedisScriptHelper.createScript(sb, Boolean.class);
    }

    private static final RedisScript<Boolean> DEL_IF_GET_EQUALS;

    static {
        String sb = "if (redis.call('get', KEYS[1]) == ARGV[1]) then\n" +
                "\tredis.call('del', KEYS[1])\n" +
                "\treturn true\n" +
                "else\n" +
                "\treturn false\n" +
                "end";
        DEL_IF_GET_EQUALS = RedisScriptHelper.createScript(sb, Boolean.class);
    }

    public RedisLock(StringRedisTemplate stringRedisTemplate, String lockKey) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.lockKey = lockKey;
        this.lockValue = UUID.randomUUID().toString() + "." + System.currentTimeMillis();
    }

    private boolean doTryLock(int lockSeconds) throws Exception {
        if (locked) {
            throw new IllegalStateException("already locked!");
        }
        locked = stringRedisTemplate.execute(SETNX_AND_EXPIRE_SCRIPT, Collections.singletonList(lockKey), lockValue,
                                             String.valueOf(lockSeconds));
        return locked;
    }

    /**
     * 尝试加锁，成功返回true，如果失败立即返回false
     * 该方法非阻塞
     *
     * @param lockSeconds 锁允许持有的最长时间，超过这个时间后锁会自动释放
     */
    public boolean tryLock(int lockSeconds) {
        RedisLock lock = locks.get().get(lockKey);
        if (lock == this && locked) {
            // 实现可重入
            setEnterTime(getEnterTime() + 1);
            return true;
        }
        try {
            boolean isLocked = doTryLock(lockSeconds);
            if (isLocked) {
                setEnterTime(1);
                locks.get().put(lockKey, this);
            }
            return isLocked;
        } catch (Exception e) {
            logger.error("tryLock Error", e);
            return false;
        }
    }

    /**
     * 轮询的方式去获得锁，成功返回true，超过轮询次数或异常返回false
     *
     * @param lockSeconds       锁允许持有的最长时间，超过这个时间后锁会自动释放
     * @param tryIntervalMillis 轮询的时间间隔(毫秒)
     * @param maxTryCount       最大的轮询次数
     */
    private boolean tryLock(int lockSeconds, long tryIntervalMillis, int maxTryCount) {
        if (logger.isDebugEnabled()) {
            logger.debug("try lock of key:{}, thread:{}", lockKey, Thread.currentThread());
        }
        int tryCount = 0;
        while (true) {
            if (++tryCount > maxTryCount) {
                // 获取锁超时
                return false;
            }
            try {
                if (doTryLock(lockSeconds)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("locked of key:{}, thread:{}", lockKey, Thread.currentThread());
                    }
                    return true;
                }
            } catch (Exception e) {
                logger.error("tryLock Error", e);
                return false;
            }
            try {
                Thread.sleep(tryIntervalMillis);
            } catch (InterruptedException e) {
                logger.error("tryLock interrupted", e);
                return false;
            }
        }
    }

    /**
     * 解锁操作
     */
    public void unlock() {
        RedisLock lock = locks.get().get(lockKey);
        if (lock != this || !locked) {
            throw new IllegalStateException("not locked yet!");
        }
        setEnterTime(getEnterTime() - 1);
        if (getEnterTime() == 0) {
            locked = false;
            // 忽略结果
            stringRedisTemplate.execute(DEL_IF_GET_EQUALS, Collections.singletonList(lockKey), lockValue);
            locks.get().remove(lockKey);
        }
    }

    /**
     * 尝试加锁，成功返回true，在 timeoutSeconds 时间内仍未获得锁或者出现异常则返回false
     * 该方法阻塞，最长阻塞时间为 timeoutSeconds
     *
     * @param lockSeconds       锁允许持有的最长时间，超过这个时间后锁会自动释放
     * @param timeoutSeconds    尝试获取锁超时时间
     */
    public boolean tryLock(int lockSeconds, int timeoutSeconds) {
        RedisLock lock = locks.get().get(lockKey);
        if (lock == this && locked) {
            // 实现可重入
            setEnterTime(getEnterTime() + 1);
            return true;
        }
        int maxTryCount = timeoutSeconds * 1000 / tryIntervalMillis + 1;
        boolean isLocked = tryLock(lockSeconds, tryIntervalMillis, maxTryCount);
        if (isLocked) {
            setEnterTime(1);
            locks.get().put(lockKey, this);
        }
        return isLocked;
    }

    public static RedisLock createLock(StringRedisTemplate stringRedisTemplate, String lockKey) {
        RedisLock lock = locks.get().get(lockKey);
        if (lock == null) {
            lock = new RedisLock(stringRedisTemplate, lockKey);
        }
        return lock;
    }
}

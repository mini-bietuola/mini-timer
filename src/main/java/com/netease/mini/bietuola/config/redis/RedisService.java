package com.netease.mini.bietuola.config.redis;

import com.netease.mini.bietuola.config.redis.component.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Auther ctl
 * @Date 2019/4/29
 */
@Service
public class RedisService {

    private static final Logger LOG = LoggerFactory.getLogger(RedisService.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;



    public RedisService(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value, long expireSecond) {
        redisTemplate.opsForValue().set(key, value, expireSecond, TimeUnit.SECONDS);
    }

    /**
     * 不存在才添加
     */
    public boolean add(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public boolean setExpire(String key, long expireSecond) {
        return redisTemplate.expire(key, expireSecond, TimeUnit.SECONDS);
    }

    public RedisLock getLock(String lockKey) {
        return RedisLock.createLock(stringRedisTemplate, lockKey);
    }

}

package com.netease.mini.bietuola.config.redis.component;

import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.data.redis.core.script.RedisScript;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created On 10/24 2017
 *
 */
public class RedisScriptHelper {

    private static final Map<URL, RedisScript<?>> SCRIPT_CACHE = new HashMap<URL, RedisScript<?>>();

    @SuppressWarnings("unchecked")
    public static <T> RedisScript<T> loadScript(URL url, Class<T> resultType) {
        RedisScript<T> script = (RedisScript<T>) SCRIPT_CACHE.get(url);
        if (script == null) {
            try {
                script = new RedisScriptImpl<T>(IOUtils.toString(url, "UTF-8"), resultType);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SCRIPT_CACHE.put(url, script);
        }
        return script;
    }

    public static <T> RedisScript<T> loadScript(String resourceName, Class<T> resultType) {
        return loadScript(Resources.getResource(resourceName), resultType);
    }

    public static <T> RedisScript<T> createScript(String scriptAsString, Class<T> resultType) {
        return new RedisScriptImpl<T>(scriptAsString, resultType);
    }

    private static class RedisScriptImpl<T> implements RedisScript<T> {

        private final String script;

        private final String sha1;

        private final Class<T> resultType;

        RedisScriptImpl(String script, Class<T> resultType) {
            this.script = script;
            this.sha1 = DigestUtils.sha1DigestAsHex(script);
            this.resultType = resultType;
        }

        @Override
        public String getSha1() {
            return sha1;
        }

        @Override
        public Class<T> getResultType() {
            return resultType;
        }

        @Override
        public String getScriptAsString() {
            return script;
        }
    }
}

package com.netease.mini.bietuola.config.mybatis;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Auther ctl
 * @Date 2019/4/28
 */
public class IntEnumCache {
    private static final ConcurrentHashMap<Class<?>, HashMap<Integer, Enum<?>>> cache = new ConcurrentHashMap<>();

    public static <E extends Enum<E> & IntEnum> E getEnumValue(Class<E> enumClass, Integer value) {
        HashMap<Integer, Enum<?>> lookup = cache.get(enumClass);
        if (lookup == null) {
            lookup = new HashMap<>();
            EnumSet<E> enumValues = EnumSet.allOf(enumClass);
            for (E enumValue : enumValues) {
                lookup.put(enumValue.getIntValue(), enumValue);
            }
            HashMap<Integer, Enum<?>> oldLookup = cache.putIfAbsent(enumClass, lookup);
            if (oldLookup != null) {
                lookup = oldLookup;
            }
        }

        @SuppressWarnings("unchecked")
        E enumValue = (E) lookup.get(value);
        return enumValue;
    }
}

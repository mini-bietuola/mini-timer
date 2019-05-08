/**
 * @(#)Gender.java, 2019-04-29.
 * <p/>
 * Copyright 2019 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.netease.mini.bietuola.constant;

import com.netease.mini.bietuola.config.mybatis.IntEnum;

/**
 * @author hanbing(hanbing5 @ corp.netease.com)
 */
public enum Gender implements IntEnum {

    MALE(1, "男"),
    FEMALE(2, "女");

    private final int intValue;
    private final String displayName;

    Gender(int intValue, String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    @Override
    public int getIntValue() {
        return intValue;
    }

    public boolean is(int value) {
        return this.intValue == value;
    }

    public static Gender parse(int intValue) {
        for (Gender gender : values()) {
            if (gender.is(intValue))
                return gender;
        }
        return null;
    }
}

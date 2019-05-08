/**
 * @(#)StartType.java, 2019-04-29.
 * <p/>
 * Copyright 2019 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.netease.mini.bietuola.constant;

import com.netease.mini.bietuola.config.mybatis.IntEnum;

/**
 * @author hanbing(hanbing5 @ corp.netease.com)
 */
public enum StartType implements IntEnum {
    FULL_PEOPLE(1, "人满即开"),
    SCHEDULE(2, "预设时间");

    private final int intValue;
    private final String desc;

    StartType(int intValue, String desc) {
        this.intValue = intValue;
        this.desc = desc;
    }

    @Override
    public int getIntValue() {
        return intValue;
    }

    public String getDesc() {
        return desc;
    }

    public boolean is(int intValue) {
        return this.intValue == intValue;
    }

    public static StartType parse(int intValue) {
        for (StartType startType : values())
            if (startType.is(intValue))
                return startType;
        return null;
    }

}

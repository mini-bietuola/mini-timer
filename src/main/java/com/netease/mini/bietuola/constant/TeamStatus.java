/**
 * @(#)TeamStatus.java, 2019-04-29.
 * <p/>
 * Copyright 2019 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.netease.mini.bietuola.constant;

import com.netease.mini.bietuola.config.mybatis.IntEnum;

/**
 * @author hanbing(hanbing5 @ corp.netease.com)
 */
public enum TeamStatus implements IntEnum {

    RECUIT(1, "招募中"),
    RECUIT_FAILED(2, "招募失败"),
    PROCCESSING(3, "进行中"),
    FINISHED(4, "结束"),
    WAITING_START(5,"招募完成待开始");

    private final int intValue;
    private final String desc;

    TeamStatus(int intValue, String desc) {
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

    public static TeamStatus parse(int intValue) {
        for (TeamStatus teamStatus : values())
            if (teamStatus.is(intValue))
                return teamStatus;
        return null;
    }

}

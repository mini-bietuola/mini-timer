package com.netease.mini.bietuola.constant;

import com.netease.mini.bietuola.config.mybatis.IntEnum;

/**
 * @Description
 * @Auther ctl
 * @Date 2019/4/28
 */
public enum Role implements IntEnum {

    ADMIN(1, "管理员"),
    USER(2, "普通用户");

    private final int intValue;
    private final String displayName;

    Role(int intValue, String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    @Override
    public int getIntValue() {
        return intValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean is(int intValue) {
        return this.intValue == intValue;
    }

    public static Role parse(int intValue) {
        for (Role role : values())
            if (role.is(intValue))
                return role;
        return null;
    }

}

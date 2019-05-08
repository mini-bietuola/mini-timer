/**
 * @(#)RecomTeamInfo.java, 2019-04-30.
 * <p/>
 * Copyright 2019 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.netease.mini.bietuola.entity;

import java.io.Serializable;

/**
 * @author hanbing(hanbing5 @ corp.netease.com)
 */
public class RecomTeamInfo extends Team implements Serializable {

    private static final long serialVersionUID = 5795470019654849878L;
    /**
     * 当前招募人数
     */
    private Integer currentNum;

    public Integer getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(Integer currentNum) {
        this.currentNum = currentNum;
    }
}
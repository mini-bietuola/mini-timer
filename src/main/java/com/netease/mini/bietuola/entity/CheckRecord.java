package com.netease.mini.bietuola.entity;

import java.io.Serializable;

/**
 * 打卡记录表
 */
public class CheckRecord implements Serializable {

    private static final long serialVersionUID = 5870116840329602L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 用户加入小组表记录id
     */
    private Long userTeamId;
    /**
     * 打卡时间
     */
    private Long checkTime;

    /**
     * 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 用户加入小组表记录id
     */
    public void setUserTeamId(Long userTeamId) {
        this.userTeamId = userTeamId;
    }

    /**
     * 用户加入小组表记录id
     */
    public Long getUserTeamId() {
        return userTeamId;
    }

    /**
     * 打卡时间
     */
    public void setCheckTime(Long checkTime) {
        this.checkTime = checkTime;
    }

    /**
     * 打卡时间
     */
    public Long getCheckTime() {
        return checkTime;
    }

    @Override
    public String toString() {
        return "CheckRecord{" +
                "id=" + id +
                ", userTeamId=" + userTeamId +
                ", checkTime=" + checkTime +
                '}';
    }
}

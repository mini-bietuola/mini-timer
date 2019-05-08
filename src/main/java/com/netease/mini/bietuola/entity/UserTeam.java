package com.netease.mini.bietuola.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户加入小组表
 */
public class UserTeam implements Serializable {

    private static final long serialVersionUID = -822302007494565440L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 小组id
     */
    private Long teamId;
    /**
     * 用户加入小组的时间
     */
    private Long createTime;
    /**
     * 小组结束后用户获得的钱
     */
    private BigDecimal awardAmount;

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
     * 用户id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 用户id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 小组id
     */
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    /**
     * 小组id
     */
    public Long getTeamId() {
        return teamId;
    }

    /**
     * 用户加入小组的时间
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * 用户加入小组的时间
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * 小组结束后用户获得的钱
     */
    public void setAwardAmount(BigDecimal awardAmount) {
        this.awardAmount = awardAmount;
    }

    /**
     * 小组结束后用户获得的钱
     */
    public BigDecimal getAwardAmount() {
        return awardAmount;
    }
}

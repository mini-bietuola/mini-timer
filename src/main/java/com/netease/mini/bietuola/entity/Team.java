package com.netease.mini.bietuola.entity;

import com.netease.mini.bietuola.constant.StartType;
import com.netease.mini.bietuola.constant.TeamStatus;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 小组表
 */
public class Team implements Serializable {

    private static final long serialVersionUID = 1425399896900791161L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 小组名称
     */
    private String name;
    /**
     * 小组头像url
     */
    private String avatarUrl;
    /**
     * 小组图文描述图片url
     */
    private String imgUrl;
    /**
     * 押金
     */
    private BigDecimal fee;
    /**
     * 打卡开始日期，13位时间戳，清空时分秒，可空
     */
    private Long startDate;
    /**
     * 应打卡天数
     */
    private Integer duration;
    /**
     * 打卡开始时间，x点x分，存储总分钟数
     */
    private Integer startTime;
    /**
     * 打卡结束时间，x点x分，存储总分钟数
     */
    private Integer endTime;
    /**
     * 小组总人数
     */
    private Integer memberNum;
    /**
     * 小组描述、简介
     */
    private String desc;
    /**
     * 小组活动状态：招募中，招募失败，进行中，已结束
     */
    private TeamStatus activityStatus;
    /**
     * 小组类别id
     */
    private Long categoryId;
    /**
     * 开始类型，人满即开、预设时间
     */
    private StartType startType;
    /**
     * 人满即开情况下的最长招募天数，过期还没招满即招募失败
     */
    private Long maxRecuitDate;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 创建用户id
     */
    private Long createUserId;

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
     * 小组名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 小组名称
     */
    public String getName() {
        return name;
    }

    /**
     * 小组头像url
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * 小组头像url
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * 小组图文描述图片url
     */
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    /**
     * 小组图文描述图片url
     */
    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * 押金
     */
    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    /**
     * 押金
     */
    public BigDecimal getFee() {
        return fee;
    }

    /**
     * 打卡开始日期，13位时间戳，清空时分秒，可空
     */
    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    /**
     * 打卡开始日期，13位时间戳，清空时分秒，可空
     */
    public Long getStartDate() {
        return startDate;
    }

    /**
     * 应打卡天数
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * 应打卡天数
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * 打卡开始时间，x点x分，存储总分钟数
     */
    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    /**
     * 打卡开始时间，x点x分，存储总分钟数
     */
    public Integer getStartTime() {
        return startTime;
    }

    /**
     * 打卡结束时间，x点x分，存储总分钟数
     */
    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    /**
     * 打卡结束时间，x点x分，存储总分钟数
     */
    public Integer getEndTime() {
        return endTime;
    }

    /**
     * 小组总人数
     */
    public void setMemberNum(Integer memberNum) {
        this.memberNum = memberNum;
    }

    /**
     * 小组总人数
     */
    public Integer getMemberNum() {
        return memberNum;
    }

    /**
     * 小组描述、简介
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 小组描述、简介
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 小组活动状态：招募中，招募失败，进行中，已结束
     */
    public void setActivityStatus(TeamStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    /**
     * 小组活动状态：招募中，招募失败，进行中，已结束
     */
    public TeamStatus getActivityStatus() {
        return activityStatus;
    }

    /**
     * 小组类别id
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * 小组类别id
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * 开始类型，人满即开、预设时间
     */
    public void setStartType(StartType startType) {
        this.startType = startType;
    }

    /**
     * 开始类型，人满即开、预设时间
     */
    public StartType getStartType() {
        return startType;
    }

    /**
     * 人满即开情况下的最长招募天数，过期还没招满即招募失败
     */
    public void setMaxRecuitDate(Long maxRecuitDate) {
        this.maxRecuitDate = maxRecuitDate;
    }

    /**
     * 人满即开情况下的最长招募天数，过期还没招满即招募失败
     */
    public Long getMaxRecuitDate() {
        return maxRecuitDate;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * 创建时间
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * 更新时间
     */
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 更新时间
     */
    public Long getUpdateTime() {
        return updateTime;
    }

    /**
     * 创建用户id
     */
    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    /**
     * 创建用户id
     */
    public Long getCreateUserId() {
        return createUserId;
    }
}

package com.netease.mini.bietuola.entity;

import com.netease.mini.bietuola.constant.Gender;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户表
 */
public class User implements Serializable {

    private static final long serialVersionUID = -134914335904879745L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * md5加密后的密码
     */
    private String passwordMd5;
    /**
     * 手机号码
     */
    private String phone;
    /**
     * 头像图片url
     */
    private String avatarUrl;
    /**
     * 性别
     */
    private Gender gender;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 个人简介
     */
    private String desc;
    /**
     * 个人账户余额
     */
    private BigDecimal amount;
    /**
     * 个人积分
     */
    private Long score;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 创建时间
     */
    private Long updateTime;
    /**
     * 用户状态，0：正常，-1：删除
     */
    private Integer status;

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
     * 昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * md5加密后的密码
     */
    public void setPasswordMd5(String passwordMd5) {
        this.passwordMd5 = passwordMd5;
    }

    /**
     * md5加密后的密码
     */
    public String getPasswordMd5() {
        return passwordMd5;
    }

    /**
     * 手机号码
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 手机号码
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 头像图片url
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * 头像图片url
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * 性别
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * 性别
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * 年龄
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * 年龄
     */
    public Integer getAge() {
        return age;
    }

    /**
     * 个人简介
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 个人简介
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 个人账户余额
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 个人账户余额
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 个人积分
     */
    public void setScore(Long score) {
        this.score = score;
    }

    /**
     * 个人积分
     */
    public Long getScore() {
        return score;
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
     * 创建时间
     */
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 创建时间
     */
    public Long getUpdateTime() {
        return updateTime;
    }

    /**
     * 用户状态，0：正常，-1：删除
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 用户状态，0：正常，-1：删除
     */
    public Integer getStatus() {
        return status;
    }
}

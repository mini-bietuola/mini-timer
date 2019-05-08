package com.netease.mini.bietuola.entity;

import java.io.Serializable;

/**
 * 小组类别表
 */
public class Category implements Serializable {

    private static final long serialVersionUID = 7037523817865410403L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 小组类型名称
     */
    private String name;
    /**
     * 小组类型图片url
     */
    private String imgUrl;
    /**
     * 该类别对应每次打卡可获得积分
     */
    private Integer score;

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
     * 小组类型名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 小组类型名称
     */
    public String getName() {
        return name;
    }

    /**
     * 小组类型图片url
     */
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    /**
     * 小组类型图片url
     */
    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * 该类别对应每次打卡可获得积分
     */
    public void setScore(Integer score) {
        this.score = score;
    }

    /**
     * 该类别对应每次打卡可获得积分
     */
    public Integer getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", score=" + score +
                '}';
    }
}

package com.netease.mini.bietuola.entity;

import com.netease.mini.bietuola.constant.Role;

import java.io.Serializable;

/**
 * @Description
 * @Auther ctl
 * @Date 2019/4/28
 */
public class Hello implements Serializable {
    private static final long serialVersionUID = -2597549125451757061L;

    private long id;
    private String name;
    private int age;
    private double price;
    private long createTime; // 13位时间戳
    private int day; // 如20190405
    private Role role; // 枚举示例

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Hello{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", price=" + price +
                ", createTime=" + createTime +
                ", day=" + day +
                ", role=" + role +
                '}';
    }
}

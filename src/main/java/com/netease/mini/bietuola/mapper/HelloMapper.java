package com.netease.mini.bietuola.mapper;

import com.netease.mini.bietuola.entity.Hello;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description
 * @Auther ctl
 * @Date 2019/4/28
 */
public interface HelloMapper {

    // 可以自己写sql在注解中，不需要在xml中写
    @Select("SELECT id, name, age, price, create_time AS createTime, day,role FROM hello WHERE name = #{name}")
    List<Hello> getHelloByName(String name);

    // 可以在xml中写好对应名字的sql操作
    List<Hello> listHello();

    int save(Hello hello);

    int update(@Param("name") String name, @Param("id") long id);

}

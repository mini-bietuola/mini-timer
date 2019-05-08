package com.netease.mini.bietuola.mapper;

import com.netease.mini.bietuola.entity.Category;

import java.util.List;

/**
 * @Description
 * @Auther ctl
 * @Date 2019/4/28
 */
public interface CategoryMapper {

//    // 可以自己写sql在注解中，不需要在xml中写
//    @Select("SELECT id, name, age, price, create_time AS createTime, day,role FROM hello WHERE name = #{name}")
//    List<Hello> getHelloByName(String name);

    // 可以在xml中写好对应名字的sql操作
    List<Category> listcategory();

//    int save(Hello hello);
//
//    int update(@Param("name") String name, @Param("id") long id);


    /**
     * 查询该类别对应积分
     * @param id
     * @return
     */
    int selectScoreById(Long id);
}

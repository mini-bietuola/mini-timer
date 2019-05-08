package com.netease.mini.bietuola.mapper;

import java.math.BigDecimal;

import com.netease.mini.bietuola.entity.User;
import org.apache.ibatis.annotations.Param;

import com.netease.mini.bietuola.entity.User;

import java.util.List;

/**
 * @Description
 * @Auther ctl
 * @Date 2019/4/30
 */
public interface UserMapper {

    /**
     * 根据用户id查找用户
     * @param userId 用户id
     * @return
     */
    User findUserByUserId(int userId);


    /**
     * @param user 用户
     */
    int save(User user);

    /**
     * @param user 用户
     */
    void updateByUserId(User user);

    /**
     *
     * @param phone
     * @return
     */
    User getByPhone(@Param("phone") String phone);

    int countByPhone(@Param("phone") String phone);

    int updatePassword(@Param("phone") String phone, @Param("passwordMd5") String passwordMd5);

    User getBaseInfoById(@Param("id") Long id);

    int updateBaseInfoById(User user);

    BigDecimal getAmount(long userId);

    int updateAmount(@Param("amount") BigDecimal amount, @Param("userId") long userId);


    /**
     * 查询小组所有用户个人信息
     * @param teamId
     * @return
     */
    List<User> getAllUserByTeamId(Long teamId);

    /**
     * 更新用户的金额
     * @param amount
     * @return
     */
    int updateUserAmount(@Param("amount") BigDecimal amount,@Param("userId") Long id);

}

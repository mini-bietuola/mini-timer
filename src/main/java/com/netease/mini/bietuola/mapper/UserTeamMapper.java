package com.netease.mini.bietuola.mapper;

import com.netease.mini.bietuola.entity.UserTeam;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zhang on 2019/5/1.
 */
public interface UserTeamMapper {
    /**
     * 查询用户的所有小组
     * @param userId 用户id
     * @return
     */
    List<UserTeam> findUserTeamByUserId(Long userId);

    /**
     * 查询当前小组参与人数
     * @param teamId
     * @return
     */
    long findTeamJoinNum(Long teamId);

    /**
     * 查询用户参与的小组的信息
     * @param userId
     * @param teamId
     * @return
     */
    UserTeam findUserTeamByUserIdAndTeamId(@Param("userId") Long userId, @Param("teamId") Long teamId);

    /**
     * 插入加入记录
     * @param userTeam
     * @return
     */
    int  insert(UserTeam userTeam);

    /**
     * 查询个人最后获得金钱
     * @param userId，teamId
     * @return
     */
    BigDecimal selectAwardAmountByUserIdTeamId(@Param("userId") Long userId, @Param("teamId") Long teamId);

    /**
     * 更新个人在一个小组中最后获得的钱
     * @param userId
     * @param teamId
     * @param fee
     */
    int updateAwardAmount(@Param("userId") Long userId, @Param("teamId") Long teamId, @Param("fee") BigDecimal fee);
}

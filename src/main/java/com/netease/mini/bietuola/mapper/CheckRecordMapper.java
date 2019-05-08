package com.netease.mini.bietuola.mapper;

import com.netease.mini.bietuola.entity.CheckRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2019/5/1.
 */
public interface CheckRecordMapper {
    /**
     * 通过userTeamId查找对应得CheckRecord列表
     * @param userTeamId
     * @return
     */
    List<CheckRecord> findCheckRecordByUserTeamId(Long userTeamId);

    /**
     * 保存打开信息
     * @param userTeamId
     * @param checkTime
     */
    void save(@Param("userTeamId") Long userTeamId,@Param("checkTime") Long checkTime);

    /**
     * 指定日期段打卡记录
     * @param userTeamId
     * @param startTime
     * @param endTime
     * @return
     */
    List<CheckRecord> CheckStatus(@Param("userTeamId") Long userTeamId,@Param("startTime") Long startTime,@Param("endTime") Long endTime);


    /**
     * 计算用户打卡次数
     * @param userId
     * @param teamId
     * @return
     */
    int CountCheckTimeByUserId(@Param("userId") Long userId, @Param("teamId") Long teamId);

    /**
     * 统计小组内用户的打卡记录
     * @param teamId
     * @return
     */
    List<Map<String, Long>> queryCheckTimeByTeamId(@Param("teamId") Long teamId);
}

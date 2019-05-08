package com.netease.mini.bietuola.mapper;

import com.netease.mini.bietuola.constant.TeamStatus;
import com.netease.mini.bietuola.entity.Team;
import org.apache.ibatis.annotations.Param;

import java.util.List;


import com.netease.mini.bietuola.entity.Team;

/**
 * @Description
 * @Auther ctl
 * @Date 2019/4/30
 */
public interface TeamMapper {

    /**
     * 根据小组的状态查询小组详情列表
     *
     * @param teamStatus 队伍状态信息
     * @return
     */
    List<Team> findTeamByActivityStatus(@Param("teamStatus") TeamStatus teamStatus);

    /**
     * 根据小组id查询小组详情
     *
     * @param teamId
     * @return
     */
    Team findTeamByTeamId(Long teamId);

    /**
     * 新建小组
     *
     * @param team
     * @return
     */
    int save(Team team);

    /**
     * 查询所有小组
     *
     * @return
     */
    List<Team> listTeam();

    /**
     * 根据类别查询小组
     *
     * @param categoryId 类别ID
     * @return
     */
    List<Team> getTeamByCategory(Long categoryId);

    /**
     * 根据ID查询小组
     *
     * @param teamId
     * @return
     */
    Team getTeamById(long teamId);

    /**
     * 统计小组当前人数
     *
     * @param teamId 小组ID
     * @return
     */
    int countMember(long teamId);

    /**
     * 更新小组状态
     *
     * @param startDate  开始日期
     * @param teamStatus 小组状态
     * @param teamId     小组ID
     * @return
     */
    int updateStatus(@Param("startDate") long startDate, @Param("teamStatus") TeamStatus teamStatus, @Param("teamId") long teamId);

    /**
     * 通过Id查询小组基本信息
     *
     * @param teamId
     * @return
     */
    Team selectTeamInfoById(Long teamId);

    /**
     * 计算小组当前人数
     *
     * @param teamId
     * @return
     */
    int countCurrentMemberNum(Long teamId);

    /**
     * 定时任务，每日凌晨状态检查和改变：招募完成待开始-->进行中
     * @param current
     */
    void changeWaitingToProcessing(@Param("current") Long current);

    /**
     * 定时任务，每日凌晨状态检查和改变：招募中-->招募失败，针对预设时间类型
     * @param current
     */
    void changeRecuitToFailForSchedule(@Param("current") Long current);

    /**
     * 定时任务，每日凌晨状态检查和改变：招募中-->招募失败，针对人满即开类型
     * @param current
     */
    void changeRecuitToFailForFullPeople(@Param("current") Long current);
}

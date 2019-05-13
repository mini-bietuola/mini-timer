package com.netease.mini.bietuola.schedule;

import com.netease.mini.bietuola.config.redis.RedisService;
import com.netease.mini.bietuola.config.redis.component.RedisLock;
import com.netease.mini.bietuola.constant.Constants;
import com.netease.mini.bietuola.constant.TeamStatus;
import com.netease.mini.bietuola.entity.Team;
import com.netease.mini.bietuola.entity.UserTeam;
import com.netease.mini.bietuola.mapper.CheckRecordMapper;
import com.netease.mini.bietuola.mapper.TeamMapper;
import com.netease.mini.bietuola.mapper.UserMapper;
import com.netease.mini.bietuola.mapper.UserTeamMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by zhang on 2019/5/6.
 */
@Component
public class ScheduleTask {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduleTask.class);

    private final TeamMapper teamMapper;
    private final CheckRecordMapper checkRecordMapper;
    private final UserMapper userMapper;
    private final UserTeamMapper userTeamMapper;
    private final RedisService redisService;

    @Autowired
    public ScheduleTask(TeamMapper teamMapper, CheckRecordMapper checkRecordMapper, UserMapper userMapper, UserTeamMapper userTeamMapper, RedisService redisService) {
        this.teamMapper = teamMapper;
        this.checkRecordMapper = checkRecordMapper;
        this.userMapper = userMapper;
        this.userTeamMapper = userTeamMapper;
        this.redisService = redisService;
    }

    @Scheduled(cron = "5 0 0 * * ?")
    public void task() {
        String key = Constants.REDIS_LOCK_PREFIX + "teamStateChangeTask:finish";
        RedisLock lock = redisService.getLock(key);
        boolean isLocked = lock.tryLock(30);
        if (isLocked) {
            LOG.info("定时任务，小组状态转换：进行中-->已结束");
            teamStatusChange();
        }
    }

    /**
     * 小组状态由进行变化为结束
     */
    public void teamStatusChange() {
        // todo 记录数过多时的优化处理
        List<Team> teamList = teamMapper.findTeamByActivityStatus(TeamStatus.PROCCESSING);
        for (Team team : teamList) {
            //打卡开始时间
            Long timeCheck = team.getStartDate();
            //打卡天数
            Integer day = team.getDuration();
            //当前时间
            Long current = System.currentTimeMillis();
            if (current >= timeCheck + day * 24 * 60 * 60 * 1000) {
                //小组状态由进行转换为已结束
                Long teamId = team.getId();
                teamMapper.updateStatus(team.getStartDate(), TeamStatus.FINISHED, teamId);
                //资金的计算工作
                List<Map<String, Long>> mapList = checkRecordMapper.queryCheckTimeByTeamId(teamId);

                //小组总的打卡数
                int sum = 0;
                for (Map<String, Long> map : mapList) {
                    sum += map.get("times");
                }
                for (Map<String, Long> map : mapList) {
                    BigDecimal fee = team.getFee().multiply(new BigDecimal(team.getMemberNum()))
                            .multiply(new BigDecimal(map.get("times"))).divide(new BigDecimal(sum), 2, RoundingMode.DOWN);
                    Long userId = map.get("userId");
                    userMapper.updateUserAmount(fee, userId);
                    userTeamMapper.updateAwardAmount(userId, teamId, fee);
                }
            }
        }
    }

    @Scheduled(cron = "10 0 0 * * ?")
    public void task2() {
        String key = Constants.REDIS_LOCK_PREFIX + "teamStateChangeTask:fail";
        RedisLock lock = redisService.getLock(key);
        boolean isLocked = lock.tryLock(30);
        if (isLocked) {
            changeWaitingToProcessing();
            changeRecuitToFailForSchedule();
            changeRecuitToFailForFullPeople();
        }
    }

    /**
     * 招募完成待开始-->进行中
     */
    public void changeWaitingToProcessing() {
        // 招募完成待开始-->进行中
        LOG.info("定时任务，小组状态转换：招募完成待开始-->进行中");
        teamMapper.changeWaitingToProcessing(System.currentTimeMillis());
//        List<Team> waitingTeams = teamMapper.findTeamByActivityStatus(TeamStatus.WAITING_START);
//        for (Team t: waitingTeams) {
//            Long startDate = t.getStartDate();
//            long current = System.currentTimeMillis();
//            if (current > startDate) {
//                teamMapper.updateStatus(startDate, TeamStatus.PROCCESSING, t.getId());
//            }
//        }
    }

    /**
     * 招募中-->招募失败，针对预设时间类型，注意需要退钱
     */
    @Transactional
    public void changeRecuitToFailForSchedule() {
        LOG.info("定时任务，小组状态转换：招募中-->招募失败，预设时间类型");
        List<Team> teams = teamMapper.listRecuitToFailTeamForSchedule(System.currentTimeMillis());
        doChangeRecuitToFailAndRefund(teams);
    }

    /**
     * 招募中-->招募失败，针对人满即开类型，注意需要退钱
     */
    @Transactional
    public void changeRecuitToFailForFullPeople() {
        LOG.info("定时任务，小组状态转换：招募中-->招募失败，预设人满即开");
        List<Team> teams = teamMapper.listRecuitToFailTeamForFullPeople(System.currentTimeMillis());
        doChangeRecuitToFailAndRefund(teams);
    }

    public void doChangeRecuitToFailAndRefund(List<Team> teams) {
        if (teams != null && !teams.isEmpty()) {
            Map<Long, BigDecimal> teamIdFeeMap = new HashMap<>();
            for (Team t: teams) {
                teamIdFeeMap.put(t.getId(), t.getFee());
            }
            Set<Long> teamIds = teamIdFeeMap.keySet();
            teamMapper.updateStatusByTeamIds(teamIds, TeamStatus.RECUIT_FAILED);
            List<UserTeam> uts = userTeamMapper.listTeamIdAndUserIdByTeamIds(teamIds);
            Map<Long, Set<Long>> teamIdMapUserIds = new HashMap<>();
            for (UserTeam ut: uts) {
                Long teamId = ut.getTeamId();
                Set<Long> userIds = teamIdMapUserIds.get(teamId);
                if (userIds == null) {
                    teamIdMapUserIds.put(teamId, new HashSet<>(Collections.singletonList(ut.getUserId())));
                } else {
                    userIds.add(ut.getUserId());
                }
            }
            for (Map.Entry<Long, Set<Long>> tmu: teamIdMapUserIds.entrySet()) {
                Long teamId = tmu.getKey();
                Set<Long> userIds = tmu.getValue();
                BigDecimal fee = teamIdFeeMap.get(teamId);
                userMapper.addAmountbyUserIds(userIds, fee);
            }
        }
    }

}

package com.netease.mini.bietuola.schedule;

import com.netease.mini.bietuola.config.jpush.JPushService;
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
import com.netease.mini.bietuola.web.util.DateUtil;
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
    private final JPushService jPushService;

    @Autowired
    public ScheduleTask(TeamMapper teamMapper, CheckRecordMapper checkRecordMapper, UserMapper userMapper, UserTeamMapper userTeamMapper, RedisService redisService, JPushService jPushService) {
        this.teamMapper = teamMapper;
        this.checkRecordMapper = checkRecordMapper;
        this.userMapper = userMapper;
        this.userTeamMapper = userTeamMapper;
        this.redisService = redisService;
        this.jPushService = jPushService;
    }

    @Scheduled(cron = "5 0/5 * * * ?")
    public void task() {
        String key = Constants.REDIS_LOCK_PREFIX + "teamStateChangeTask:finish";
        RedisLock lock = redisService.getLock(key);
        boolean isLocked = lock.tryLock(10);
        if (isLocked) {
            LOG.info("定时任务，小组状态转换：进行中-->已结束");
            teamStatusChange();
        }
    }

    /**
     * 小组状态由进行变化为结束
     */
    @Transactional
    public void teamStatusChange() {
        // todo 记录数过多时的优化处理
        List<Team> teamList = teamMapper.findTeamByActivityStatus(TeamStatus.PROCCESSING);
        //当前时间
        Long current = System.currentTimeMillis();
        for (Team team : teamList) {
            //打卡开始时间
            Long timeCheck = team.getStartDate();
            //打卡天数
            Integer day = team.getDuration();
            if (current >= timeCheck + day * 24 * 60 * 60 * 1000) {
                Long teamId = team.getId();
                // 向小组内所有成员发送结束消息
                String alert = "您的小组\"" + team.getName() + "\"打卡活动已结束，请及时查看";
                String title = "小组结束通知";
                // 推送消息必须在当前时间上延迟若干秒
                String sendTime = DateUtil.format(System.currentTimeMillis() + 10000, "yyyy-MM-dd HH:mm:ss");
                String name = "小组结束通知";
                Map<String, String> infoMap = new HashMap<>();
                infoMap.put("route", "Details");
                infoMap.put("teamId", teamId.toString());
                String[] tags = {"team_" + teamId};
                jPushService.sendSingleNotification(alert, title, sendTime, name, infoMap, tags);
                //小组状态由进行转换为已结束
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

    @Scheduled(cron = "5 0/5 * * * ?")
    public void task2() {
        String key = Constants.REDIS_LOCK_PREFIX + "teamStateChangeTask:fail";
        RedisLock lock = redisService.getLock(key);
        boolean isLocked = lock.tryLock(10);
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
//        teamMapper.changeWaitingToProcessing(System.currentTimeMillis());
        List<Team> waitingTeams = teamMapper.findTeamByActivityStatus(TeamStatus.WAITING_START);
        long current = System.currentTimeMillis();
        for (Team team: waitingTeams) {
            Long startDate = team.getStartDate();
            if (current >= startDate) {
                Long teamId = team.getId();
                // 向小组内所有成员发送开始消息，只发一次
                String alert = "您的小组\"" + team.getName() + "\"今天就要开始打卡啦~请做好准备";
                String title = "小组开始通知";
                String sendTime = DateUtil.format(System.currentTimeMillis() + 10000, "yyyy-MM-dd HH:mm:ss");
                String name = "小组开始通知";
                Map<String, String> infoMap = new HashMap<>();
                infoMap.put("route", "Details");
                infoMap.put("teamId", teamId.toString());
                String[] tags = {"team_" + teamId};
                jPushService.sendSingleNotification(alert, title, sendTime, name, infoMap, tags);
                // 发送每天一次的打卡通知消息
                Integer minutes = team.getStartTime();
                String sendTime2 = DateUtil.format(DateUtil.getTodayStart() + minutes * 60 * 1000, "HH:mm:ss");
                String alert2 = "您的小组\"" + team.getName() + "\"打卡开始时间为：" + sendTime2.substring(0, 5) + "，不要忘记打卡噢";
                String title2 = "打卡时间马上就要到啦~";
                long nowDelay = System.currentTimeMillis() + 10000;
                String startTime2 = DateUtil.format(nowDelay, "yyyy-MM-dd HH:mm:ss");
                String endTime2 = DateUtil.format(nowDelay + team.getDuration() * 24 * 60 * 60 * 1000, "yyyy-MM-dd HH:mm:ss");
                String name2 = "小组每日打卡通知";
                Map<String, String> infoMap2 = new HashMap<>();
                infoMap2.put("route", "TeamIM");
                infoMap2.put("teamId", teamId.toString());
                String[] tags2 = {"team_" + teamId};
                jPushService.sendDailyNotification(alert2, title2, sendTime2, startTime2, endTime2, name2, 1, infoMap2, tags2);
                // 更改状态
                teamMapper.updateStatus(startDate, TeamStatus.PROCCESSING, teamId);
            }
        }
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

package com.netease.mini.bietuola.schedule;

import com.netease.mini.bietuola.constant.TeamStatus;
import com.netease.mini.bietuola.entity.Team;
import com.netease.mini.bietuola.mapper.CheckRecordMapper;
import com.netease.mini.bietuola.mapper.TeamMapper;
import com.netease.mini.bietuola.mapper.UserMapper;
import com.netease.mini.bietuola.mapper.UserTeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2019/5/6.
 */
@Component
public class ScheduleTask {

    private final TeamMapper teamMapper;
    private final CheckRecordMapper checkRecordMapper;
    private final UserMapper userMapper;
    private final UserTeamMapper userTeamMapper;

    @Autowired
    public ScheduleTask(TeamMapper teamMapper, CheckRecordMapper checkRecordMapper, UserMapper userMapper, UserTeamMapper userTeamMapper) {
        this.teamMapper = teamMapper;
        this.checkRecordMapper = checkRecordMapper;
        this.userMapper = userMapper;
        this.userTeamMapper = userTeamMapper;
    }

    @Scheduled(cron = "30 0 0 * * ?")
    public void task() {
        teamStatusChange();
        System.out.println("进行中-->已结束，执行定时任务");
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
        changeRecuit();
        System.out.println("招募中-->招募失败；招募完成待开始-->进行中，执行定时任务");
    }

    /**
     * 招募完成待开始-->进行中；招募中-->招募失败
     */
    public void changeRecuit() {
        long current = System.currentTimeMillis();
        // 招募完成待开始-->进行中
        teamMapper.changeWaitingToProcessing(current);
//        List<Team> waitingTeams = teamMapper.findTeamByActivityStatus(TeamStatus.WAITING_START);
//        for (Team t: waitingTeams) {
//            Long startDate = t.getStartDate();
//            long current = System.currentTimeMillis();
//            if (current > startDate) { // todo 可在sql中直接加入该过滤条件以及修改状态操作，整过程只要一条sql即可
//                teamMapper.updateStatus(startDate, TeamStatus.PROCCESSING, t.getId());
//            }
//        }
        // 招募中-->招募失败
        teamMapper.changeRecuitToFailForSchedule(current); // 针对预设时间类型
        teamMapper.changeRecuitToFailForFullPeople(current); // 针对人满即开类型
//        List<Team> recuitTeams = teamMapper.findTeamByActivityStatus(TeamStatus.RECUIT);
//        for (Team t: recuitTeams) {
//            Long startDate = t.getStartDate();
//            long current = System.currentTimeMillis();
//            if (current > startDate) {
//                teamMapper.updateStatus(startDate, TeamStatus.RECUIT_FAILED, t.getId());
//            }
//        }
    }

}

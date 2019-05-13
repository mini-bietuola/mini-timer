package com.netease.mini.bietuola.schedule;

import com.netease.mini.bietuola.constant.TeamStatus;
import com.netease.mini.bietuola.entity.Team;
import com.netease.mini.bietuola.entity.User;
import com.netease.mini.bietuola.mapper.TeamMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zhang on 2019/5/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
//@Transactional
public class ScheduleTaskTest {
    @Autowired
    private TeamMapper teamMapper;
    @Autowired
    private ScheduleTask scheduleTask;

    @Before
    public void before(){
        User user1 = new User();
    }

    @Test
    public void teamStatusChangeTest() throws InterruptedException {
        TeamStatus teamStatus = teamMapper.getTeamById(1l).getActivityStatus();
        scheduleTask.teamStatusChange();
        Thread.sleep(1000*10l);
        TeamStatus teamStatus1 =teamMapper.getTeamById(1l).getActivityStatus();
    }

    public static void main(String[] args) {
        Date date= new Date(1557037942100l);
        System.out.println(date);
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.getTime().getTime());
        calendar.set(2019,3,28);
        System.out.println(calendar.getTime().getTime());
        calendar.set(2019,4,2);
        System.out.println(calendar.getTime().getTime());
        calendar.set(2019,4,5);
        System.out.println(calendar.getTime().getTime());
    }

    @Test
    public void testChangeRecuit() {
        teamMapper.changeWaitingToProcessing(System.currentTimeMillis());
    }

    @Test
    public void testChangeTeamStatus() {
//        List<Team> teams = teamMapper.listRecuitToFailTeamForFullPeople(System.currentTimeMillis());
//        for (Team t: teams) {
//            System.out.println(t.getId());
//        }
        List<Team> teams = new ArrayList<>();
        Team t = new Team();
        t.setId(1L);
        t.setFee(new BigDecimal(10));
        teams.add(t);
        t = new Team();
        t.setId(5L);
        t.setFee(new BigDecimal(20));
        teams.add(t);
        t = new Team();
        t.setId(6L);
        t.setFee(new BigDecimal(30));
        teams.add(t);
        scheduleTask.doChangeRecuitToFailAndRefund(teams);
//        System.out.println((int) (Double.parseDouble("68.6") * 100));
//        System.out.println(new BigDecimal("68.6").multiply(new BigDecimal(100)).setScale(0, RoundingMode.DOWN).intValue());

    }

}

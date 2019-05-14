package com.netease.mini.bietuola.config.jpush;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import cn.jpush.api.schedule.ScheduleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Description 极光推送服务
 * @Author ttw
 * @Date 2019/5/8
 **/
@Service
public class JPushService {
    private static final Logger LOG = LoggerFactory.getLogger(JPushService.class);

    private static final String appKey = "8e0890d0c7eea09febe12876";
    private static final String masterSecret = "d96c79555db5ae0f9a603ad5";
    ClientConfig clientConfig = ClientConfig.getInstance();
    final JPushClient jpushClient = new JPushClient(masterSecret, appKey, null, clientConfig);

    public static PushPayload buildPushObject_android_tag_alertWithTitle(String alert, String title, Map map, String... tags) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.tag(tags))
                .setNotification(Notification.android(alert, title, map))
                .build();
    }

    //发送一次消息
    public void sendSingleNotification(String alert, String title, String sendTime, String name, Map map, String... tags){
        final PushPayload payload = buildPushObject_android_tag_alertWithTitle(alert, title, map ,tags);

        try {
            ScheduleResult result = jpushClient.createSingleSchedule(name, sendTime, payload);
            LOG.info("Got result - " + result);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            LOG.error("Sendno: " + payload.getSendno());

        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
            LOG.info("Msg ID: " + e.getMsgId());
            LOG.error("Sendno: " + payload.getSendno());
        }
    }

    //发送每天一次的消息，频率可以改变。
    //startTime、endTime format 为 'yyyy-MM-dd HH:mm:ss'
    //sendTime format为 'HH:mm:ss'
    public void sendDailyNotification(String alert, String title, String sendTime, String startTime, String endTime, String name, Integer frequency,  Map map, String... tags) {
        final PushPayload payload = buildPushObject_android_tag_alertWithTitle(alert, title, map ,tags);

        try {
            ScheduleResult result = jpushClient.createDailySchedule(name, startTime, endTime, sendTime, frequency, payload);
            LOG.info("Got result - " + result);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            LOG.error("Sendno: " + payload.getSendno());

        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
            LOG.info("Msg ID: " + e.getMsgId());
            LOG.error("Sendno: " + payload.getSendno());
        }
    }

    /*public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JPushService jPushService = new JPushService();
        jPushService.SendTeamStartMessage("梦之队", sdf.format(new Date(System.currentTimeMillis() + 5 * 1000)));
    }*/

}

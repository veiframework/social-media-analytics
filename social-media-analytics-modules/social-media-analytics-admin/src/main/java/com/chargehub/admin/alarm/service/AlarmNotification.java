package com.chargehub.admin.alarm.service;

/**
 * @author zhanghaowei
 * @since 1.0
 */
public interface AlarmNotification {

    String type();


    void send(AlarmNotificationConfig alarmNotificationConfig);


}

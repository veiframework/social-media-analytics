package com.chargehub.admin.alarm.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.chargehub.admin.alarm.domain.AlarmWebhook;
import com.chargehub.admin.groupuser.domain.GroupUser;
import com.chargehub.admin.groupuser.service.GroupUserService;
import com.chargehub.common.core.utils.ThreadHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Component
public class AlarmNotificationManager {

    private final Map<String, AlarmNotification> alarmNotificationMap = new ConcurrentHashMap<>();


    private final GroupUserService groupUserService;


    private final AlarmWebhookService alarmWebhookService;

    public AlarmNotificationManager(List<AlarmNotification> alarmNotifications,
                                    GroupUserService groupUserService,
                                    AlarmWebhookService alarmWebhookService) {
        this.groupUserService = groupUserService;
        this.alarmWebhookService = alarmWebhookService;
        for (AlarmNotification alarmNotification : alarmNotifications) {
            alarmNotificationMap.put(alarmNotification.type(), alarmNotification);
        }
    }


    public List<AlarmNotificationConfig> getLeadershipWebhook(String userId) {
        GroupUser groupUser = this.groupUserService.getByUserId(userId);
        if (groupUser == null) {
            return new ArrayList<>();
        }
        String idPath = groupUser.getIdPath();
        if (StringUtils.isBlank(idPath)) {
            return new ArrayList<>();
        }
        String[] split = idPath.split(StringPool.COMMA);
        // 添加长度检查避免索引越界
        if (split.length <= 1) {
            return new ArrayList<>();
        }
        Set<String> ids = new HashSet<>(Arrays.asList(split).subList(0, split.length - 1));
        List<AlarmWebhook> list = alarmWebhookService.getByUserIds(ids);
        Map<String, AlarmWebhook> map = new HashMap<>();
        for (AlarmWebhook alarmWebhook : list) {
            map.put(alarmWebhook.getType(), alarmWebhook);
        }
        return BeanUtil.copyToList(map.values(), AlarmNotificationConfig.class);
    }

    public void send(List<AlarmNotificationConfig> alarmNotificationConfigs) {
        for (AlarmNotificationConfig alarmNotificationConfig : alarmNotificationConfigs) {
            AlarmNotification alarmNotification = alarmNotificationMap.get(alarmNotificationConfig.getType());
            if (alarmNotification == null) {
                continue;
            }
            ThreadHelper.execute(() -> alarmNotification.send(alarmNotificationConfig));
        }
    }

    public void send(AlarmNotificationConfig alarmNotificationConfig) {
        AlarmNotification alarmNotification = alarmNotificationMap.get(alarmNotificationConfig.getType());
        if (alarmNotification == null) {
            return;
        }
        ThreadHelper.execute(() -> alarmNotification.send(alarmNotificationConfig));
    }

}

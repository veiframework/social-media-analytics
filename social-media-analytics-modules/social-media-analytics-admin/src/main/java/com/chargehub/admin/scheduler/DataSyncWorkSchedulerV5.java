package com.chargehub.admin.scheduler;

import cn.hutool.core.thread.ThreadUtil;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.service.SocialMediaWorkTaskService;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@Component
public class DataSyncWorkSchedulerV5 {


    @Autowired
    private SocialMediaWorkTaskService socialMediaWorkTaskService;

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Autowired
    private HubProperties hubProperties;

    @Autowired
    private RedisService redisService;


    public void execute() {
        for (int i = 0; i < 60; i++) {
            boolean existKey = redisService.existKey(SocialMediaWorkTaskService.BATCH_ADD_TASK_LOCK + "*");
            if (existKey) {
                ThreadUtil.safeSleep(1000);
            } else {
                List<SocialMediaWork> latestWork = socialMediaWorkService.getLatestWork(null);
                if (CollectionUtils.isNotEmpty(latestWork)) {
                    Date now = new Date();
                    Integer updateMinutes = hubProperties.getUpdateMinutes();
                    latestWork.removeIf(e -> !e.computeSyncDuration(now, updateMinutes));
                }
                socialMediaWorkTaskService.batchAddTask(latestWork, true);
                return;
            }
        }
        log.error("自动同步作品时锁被占用");
    }


}

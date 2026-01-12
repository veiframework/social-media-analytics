package com.chargehub.admin.scheduler;

import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.service.SocialMediaWorkTaskService;
import com.chargehub.common.core.properties.HubProperties;
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


    public void execute() {
        List<SocialMediaWork> latestWork = socialMediaWorkService.getLatestWork(null, false);
        if (CollectionUtils.isNotEmpty(latestWork)) {
            Date now = new Date();
            Integer updateMinutes = hubProperties.getUpdateMinutes();
            //TODO 将来微信视频号也要纳入这个定时任务
            latestWork.removeIf(e -> !e.computeSyncDuration(now, updateMinutes) || e.getPlatformId().equals(SocialMediaPlatformEnum.WECHAT_VIDEO.getDomain()));
        }
        socialMediaWorkTaskService.batchAddTask(latestWork, false);
    }


}

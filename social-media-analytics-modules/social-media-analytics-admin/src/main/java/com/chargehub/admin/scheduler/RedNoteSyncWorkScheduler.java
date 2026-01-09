package com.chargehub.admin.scheduler;

import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.work.service.SocialMediaWorkCreateService;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.service.SocialMediaWorkTaskService;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.redis.service.RedisService;
import org.springframework.stereotype.Component;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Component
public class RedNoteSyncWorkScheduler extends AbstractSyncWorkScheduler {

    protected RedNoteSyncWorkScheduler(SocialMediaWorkTaskService socialMediaWorkTaskService, RedisService redisService, DataSyncManager dataSyncManager, SocialMediaWorkService socialMediaWorkService, SocialMediaWorkCreateService socialMediaWorkCreateService, HubProperties hubProperties) {
        super(socialMediaWorkTaskService, redisService, dataSyncManager, socialMediaWorkService, socialMediaWorkCreateService, hubProperties, 2);
        this.setTaskName(SocialMediaPlatformEnum.RED_NOTE.getDomain());
    }

}

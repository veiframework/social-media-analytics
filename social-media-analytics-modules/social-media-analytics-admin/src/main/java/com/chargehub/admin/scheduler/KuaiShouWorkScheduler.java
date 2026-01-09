package com.chargehub.admin.scheduler;

import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.service.SocialMediaAccountTaskService;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.work.service.SocialMediaWorkCreateService;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.redis.service.RedisService;
import org.springframework.stereotype.Component;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Deprecated
@Component
public class KuaiShouWorkScheduler extends AbstractWorkScheduler {


    protected KuaiShouWorkScheduler(SocialMediaAccountTaskService socialMediaAccountTaskService, RedisService redisService, DataSyncManager dataSyncManager, SocialMediaAccountService socialMediaAccountService, SocialMediaWorkService socialMediaWorkService, SocialMediaWorkCreateService socialMediaWorkCreateService, HubProperties hubProperties) {
        super(socialMediaAccountTaskService, redisService, dataSyncManager, socialMediaAccountService, socialMediaWorkService, socialMediaWorkCreateService, hubProperties, null);
        this.setTaskName(SocialMediaPlatformEnum.KUAI_SHOU.getDomain());
    }

}

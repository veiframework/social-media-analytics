package com.chargehub.admin.scheduler;

import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.service.SocialMediaAccountTaskService;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.DataSyncWorksParams;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.SyncWorkStatusEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.service.SocialMediaWorkCreateService;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.net.Proxy;
import java.util.*;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@Component
@Deprecated
public class WechatVideoWorkScheduler extends AbstractWorkScheduler {

    protected WechatVideoWorkScheduler(SocialMediaAccountTaskService socialMediaAccountTaskService, RedisService redisService, DataSyncManager dataSyncManager, SocialMediaAccountService socialMediaAccountService, SocialMediaWorkService socialMediaWorkService, SocialMediaWorkCreateService socialMediaWorkCreateService, HubProperties hubProperties) {
        super(socialMediaAccountTaskService, redisService, dataSyncManager, socialMediaAccountService, socialMediaWorkService, socialMediaWorkCreateService, hubProperties, 2);
        this.setTaskName(SocialMediaPlatformEnum.WECHAT_VIDEO.getDomain());
    }


    @Override
    public void fetchWorks(SocialMediaAccount socialMediaAccountVo, Proxy proxy, com.microsoft.playwright.options.Proxy browserProxy) {
        String accountId = socialMediaAccountVo.getId();
        String platformId = socialMediaAccountVo.getPlatformId();
        String secUid = socialMediaAccountVo.getSecUid();
        String accountType = socialMediaAccountVo.getType();
        String userId = socialMediaAccountVo.getUserId();
        String tenantId = socialMediaAccountVo.getTenantId();
        List<SocialMediaWork> latestWork = this.socialMediaWorkService.getLatestWork0(accountId, false);
        this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.SYNCING);
        Map<String, SocialMediaWork> workMap = new HashMap<>();
        for (SocialMediaWork socialMediaWork : latestWork) {
            workMap.put(socialMediaWork.getWorkUid(), socialMediaWork);
        }
        DataSyncWorksParams dataSyncWorksParams = new DataSyncWorksParams();
        dataSyncWorksParams.setSecUid(secUid);
        dataSyncWorksParams.setProxy(proxy);
        dataSyncWorksParams.setWorkMap(workMap);
        SocialMediaWorkResult<SocialMediaWork> result = this.dataSyncManager.getWorks(platformId, dataSyncWorksParams);
        List<SocialMediaWork> newWorks = result.getWorks();
        if (CollectionUtils.isEmpty(newWorks)) {
            return;
        }
        List<SocialMediaWork> updateList = new ArrayList<>();
        Set<String> newWorkUids = new HashSet<>();
        for (SocialMediaWork newWork : newWorks) {
            String workUid = newWork.getWorkUid();
            newWorkUids.add(workUid);
            SocialMediaWork existWork = workMap.get(workUid);
            if (existWork == null) {
                newWork.setUserId(userId);
                newWork.setAccountId(accountId);
                newWork.setTenantId(tenantId);
                newWork.setAccountType(accountType);
                newWork.setPlatformId(platformId);
                updateList.add(newWork);
            } else {
                SocialMediaWork updateWork = existWork.computeMd5(newWork);
                if (updateWork != null) {
                    updateList.add(updateWork);
                }
            }
        }
        workMap.forEach((workUid, existWork) -> {
            if (!newWorkUids.contains(workUid)) {
                this.socialMediaWorkService.deletedWechatVideo(existWork.getId());
            }
        });
        this.socialMediaWorkService.saveOrUpdateBatch(updateList);
    }
}

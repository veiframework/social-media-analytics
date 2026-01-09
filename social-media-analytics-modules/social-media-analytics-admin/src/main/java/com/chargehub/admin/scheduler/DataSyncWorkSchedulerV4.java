package com.chargehub.admin.scheduler;

import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.service.SocialMediaAccountTaskService;
import com.chargehub.common.core.properties.HubProperties;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Deprecated
@Component
public class DataSyncWorkSchedulerV4 {

    @Autowired
    private SocialMediaAccountTaskService socialMediaAccountTaskService;

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    @Autowired
    private HubProperties hubProperties;

    public void execute() {
        List<SocialMediaAccount> accounts = socialMediaAccountService.getAccountIdsByUserIds(null, null);
        if (CollectionUtils.isNotEmpty(accounts)) {
            Date now = new Date();
            Integer updateMinutes = hubProperties.getUpdateMinutes();
            accounts.removeIf(i -> !i.computeSyncDuration(now, updateMinutes));
        }
        socialMediaAccountTaskService.batchAddTask(accounts, true);
    }


}

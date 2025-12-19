package com.chargehub.admin.scheduler;

import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.service.SocialMediaAccountTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Component
public class DataSyncWorkSchedulerV4 {

    @Autowired
    private SocialMediaAccountTaskService socialMediaAccountTaskService;

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    public void execute() {
        List<SocialMediaAccount> accounts = socialMediaAccountService.getAccountIdsByUserIds(null);
        socialMediaAccountTaskService.batchAddTask(accounts);
    }


}

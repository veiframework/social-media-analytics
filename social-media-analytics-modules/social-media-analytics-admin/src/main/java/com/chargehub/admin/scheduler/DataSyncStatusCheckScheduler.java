package com.chargehub.admin.scheduler;

import com.chargehub.admin.account.service.SocialMediaAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@Component("dataSyncStatusCheckScheduler")
public class DataSyncStatusCheckScheduler {


    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    public void execute(Integer minute) {
        socialMediaAccountService.updateSyncWorkError(minute);
    }

}

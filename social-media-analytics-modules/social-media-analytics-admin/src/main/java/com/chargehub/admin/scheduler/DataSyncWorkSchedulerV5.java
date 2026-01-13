package com.chargehub.admin.scheduler;

import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.service.SocialMediaWorkTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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


    public void execute() {
        List<SocialMediaWork> latestWork = socialMediaWorkService.getLatestWork(null, false);
        socialMediaWorkTaskService.batchAddTask(latestWork, false);
    }


}

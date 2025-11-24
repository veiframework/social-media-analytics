package com.chargehub.admin.account.controller;

import com.chargehub.admin.scheduler.DataSyncWorkScheduler;
import com.chargehub.common.security.annotation.InnerAuth;
import com.chargehub.common.security.annotation.UnifyResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@UnifyResult
@RestController
public class SocialMediaAccountApi {


    @Autowired
    private DataSyncWorkScheduler dataSyncWorkScheduler;

    @InnerAuth
    @GetMapping("/api/social-media/sync/work")
    @ApiOperation("api同步全部作品")
    public void syncWorkDataApi() {
        dataSyncWorkScheduler.execute();
    }

}

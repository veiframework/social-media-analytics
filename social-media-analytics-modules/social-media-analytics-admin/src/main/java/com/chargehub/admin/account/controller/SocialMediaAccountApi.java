package com.chargehub.admin.account.controller;

import com.chargehub.common.security.annotation.InnerAuth;
import com.chargehub.common.security.annotation.UnifyResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@UnifyResult
@RestController
public class SocialMediaAccountApi {


    @InnerAuth
    @GetMapping("/api/social-media/sync/work")
    @ApiOperation("api同步全部作品")
    public void syncWorkDataApi() {
    }

}

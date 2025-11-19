package com.chargehub.admin.work.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.admin.work.dto.SocialMediaWorkQueryDto;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@UnifyResult
@RestController
public class SocialMediaWorkController {

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;


    @RequiresPermissions("work:page")
    @ApiOperation("分页")
    @Operation(summary = "分页")
    @GetMapping
    public IPage<SocialMediaWorkVo> getPage(SocialMediaWorkQueryDto queryDto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return this.socialMediaWorkService.getPurviewPage(queryDto, loginUser);
    }




}

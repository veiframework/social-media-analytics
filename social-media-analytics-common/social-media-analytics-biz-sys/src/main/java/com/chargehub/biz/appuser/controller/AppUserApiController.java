package com.chargehub.biz.appuser.controller;


import com.chargehub.admin.api.domain.dto.WxLoginDto;
import com.chargehub.biz.appuser.dto.AppUserInfoDto;
import com.chargehub.biz.appuser.service.UserInfoExtension;
import com.chargehub.biz.appuser.service.AppUserService;
import com.chargehub.biz.appuser.vo.AppUserVo;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.utils.SecurityUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Zhanghaowei
 * @date 2025/08/02 17:58
 */
@Api(tags = "API - 用户管理")
@RestController
@RequestMapping("/app-user/api")
@UnifyResult
public class AppUserApiController {

    @Autowired
    private AppUserService appUserService;

    @Autowired(required = false)
    private UserInfoExtension userInfoExtension;

    @ApiOperation("根据openId查询用户")
    @Operation(summary = "根据openId查询用户")
    @PostMapping("/wechat")
    public AppUserVo loginByOpenId(@RequestBody @Validated WxLoginDto dto) {
        return appUserService.loginByOpenId(dto);
    }


    @RequiresLogin(doIntercept = false)
    @ApiOperation("获取用户信息")
    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public AppUserVo getByOpenId() {
        String userId = SecurityUtils.getUserId() + "";
        AppUserVo appUserVo = (AppUserVo) appUserService.getDetailById(userId);
        if (userInfoExtension != null) {
            ObjectNode extendParams = userInfoExtension.getExtendParams(userId, appUserVo);
            appUserVo.setExtendParams(extendParams);
        }
        return appUserVo;
    }

    @RequiresLogin(doIntercept = false)
    @ApiOperation("更新用户信息")
    @Operation(summary = "更新用户信息")
    @PutMapping("/info")
    public void updateAppInfo(@RequestBody @Validated AppUserInfoDto dto) {
        Long userId = SecurityUtils.getUserId();
        dto.setId(String.valueOf(userId));
        appUserService.updateAppInfo(dto);
        if (userInfoExtension != null) {
            userInfoExtension.updateUserInfo(dto);
        }
    }

}

package com.chargehub.biz.appuser.controller;


import com.chargehub.biz.appuser.dto.AppUserDto;
import com.chargehub.biz.appuser.dto.AppUserQueryDto;
import com.chargehub.biz.appuser.service.AppUserService;
import com.chargehub.biz.appuser.vo.AppUserVo;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.template.controller.AbstractZ9Controller;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制器
 *
 * @author system
 * @since 2024-03-21
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/app-user")
@UnifyResult
public class AppUserAdminController extends AbstractZ9Controller<AppUserDto, AppUserQueryDto, AppUserVo, AppUserService> {

    public AppUserAdminController(AppUserService appUserService) {
        super(appUserService);
    }

}
package com.chargehub.auth.controller;

import com.chargehub.auth.domain.dto.WxMaLoginDto;
import com.chargehub.auth.service.AppTokenService;
import com.chargehub.common.core.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2025/08/02 17:41
 */
@RestController
@Api(tags = "APP端 - 授权相关接口")
public class AppTokenController {

    @Autowired
    private AppTokenService appTokenService;

    @ApiOperation("openId登录")
    @PostMapping("/login/wechat")
    public R<Map<String, Object>> loginByOpenId(@RequestBody @Validated WxMaLoginDto wxMaLoginDto) {
        Map<String, Object> map = appTokenService.loginByOpenId(wxMaLoginDto);
        return map == null ? R.fail("用户尚未注册") : R.ok(map);
    }

}

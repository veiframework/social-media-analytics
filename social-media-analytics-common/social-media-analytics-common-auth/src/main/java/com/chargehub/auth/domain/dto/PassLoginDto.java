package com.chargehub.auth.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/15 14:00
 * @Project：chargehub
 * @Package：com.chargehub.auth.form
 * @Filename：PassLoginDto
 */
@Data
public class PassLoginDto {

    @ApiModelProperty(value = "登录手机号",required = true)
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phone;

    @ApiModelProperty(value = "登录密码",required = true)
    @NotEmpty(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "微信小程序openId",notes = " 当前端请求微信小程序登录接口返回code=2001时,后台返回的有wxMaOpenId字段，前端再次请求密码登录时需要把本字段带上",required = false)
    private String wxMaOpenId;

    @ApiModelProperty(value = "unionId",notes = " 当前端请求微信小程序登录接口返回code=2001或2002时,后台返回的有unionId字段，前端再次请求密码登录时需要把本字段带上",required = false)
    private String unionId;

    @ApiModelProperty(value = "支付宝用户id",notes = " 当前端请求微信小程序登录接口返回code=2002时,后台返回的有aliUserId字段，前端再次请求密码登录时需要把本字段带上",required = false)
    private String aliUserId;

    @ApiModelProperty(value = "支付宝用户aliOpenid",notes = " 当前端请求微信小程序登录接口返回code=2002时,后台返回的有aliOpenid字段，前端再次请求密码登录时需要把本字段带上",required = false)
    private String aliOpenid;

    @ApiModelProperty(value = "百度Openid",notes = " 当前端请求百度小程序登录接口返回code=2003时,后台返回的有baiduOpenid字段，前端再次请求密码登录时需要把本字段带上",required = false)
    private String baiduOpenId;
}

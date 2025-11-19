package com.chargehub.auth.domain.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class AppUserLoginVO {

    @ApiModelProperty("密码")
    @NotNull(message = "密码不能为空")
    private String password;
    @NotNull(message = "手机号不能为空")
    @ApiModelProperty("手机号")
    private String mobilePhone;
}

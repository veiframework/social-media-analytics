package com.chargehub.auth.domain.vo;

import com.chargehub.auth.common.ClientTypeEnum;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class AppTempRegisterVO {

    @ApiModelProperty("客户端类型")
    @NotNull(message = "客户端不能为空")
    private ClientTypeEnum clientType;
    @NotNull(message = "手机号不能为空")
    @ApiModelProperty("手机号")
    private String mobilePhone;

}

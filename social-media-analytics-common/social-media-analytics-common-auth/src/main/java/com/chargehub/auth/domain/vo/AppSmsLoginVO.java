package com.chargehub.auth.domain.vo;

import com.chargehub.auth.common.ClientTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
public class AppSmsLoginVO {

    @ApiModelProperty("客户端类型")
    @NotNull(message = "客户端不能为空")
    private ClientTypeEnum clientType;
    @NotNull(message = "手机号不能为空")
    @ApiModelProperty("手机号")
    private String mobilePhone;
    @NotNull(message = "验证码不能为空")
    @ApiModelProperty("验证码")
    private String code;

}

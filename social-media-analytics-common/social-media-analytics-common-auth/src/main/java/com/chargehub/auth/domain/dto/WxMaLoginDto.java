package com.chargehub.auth.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/15 14:08
 * @Project：chargehub
 * @Package：com.chargehub.auth.form
 * @Filename：WxMaLoginDto
 */
@Data
public class WxMaLoginDto {


    @ApiModelProperty(value = "微信小程序appId",required = true)
    @NotEmpty(message = "微信小程序appId不能为空")
    private String wxMaAppId;

    @ApiModelProperty(value = "微信小程序code",required = true)
    @NotEmpty(message = "微信小程序code不能为空")
    private String wxMaCode;
}

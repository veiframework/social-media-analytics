package com.chargehub.auth.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/10/11 13:35
 * @Project：chargehub
 * @Package：com.chargehub.auth.domain.dto
 * @Filename：WxMaAuthLoginDto
 */
@Data
public class WxMaAuthLoginDto {

    @ApiModelProperty(value = "微信小程序appId",required = true)
    @NotEmpty(message = "appId不能为空")
    private String appId;

    @ApiModelProperty(value = "微信小程序code",required = true)
    @NotEmpty(message = "微信小程序code不能为空")
    private String code;

    @ApiModelProperty(value = "微信encryptedData",required = true)
    @NotEmpty(message = "encryptedData不能为空")
    private String encryptedData;

    @ApiModelProperty(value = "微信ivStr",required = true)
    @NotEmpty(message = "ivStr不能为空")
    private String ivStr;
}

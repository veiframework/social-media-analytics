package com.chargehub.auth.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class BaiduMaAuthLoginDto {

    @ApiModelProperty(value = "百度小程序appId",required = true)
    @NotEmpty(message = "appId不能为空")
    private String appId;

    @ApiModelProperty(value = "百度小程序code",required = true)
    @NotEmpty(message = "百度小程序code不能为空")
    private String code;

    @ApiModelProperty(value = "百度encryptedData",required = true)
    @NotEmpty(message = "encryptedData不能为空")
    private String encryptedData;

    @ApiModelProperty(value = "百度ivStr",required = true)
    @NotEmpty(message = "ivStr不能为空")
    private String ivStr;
}

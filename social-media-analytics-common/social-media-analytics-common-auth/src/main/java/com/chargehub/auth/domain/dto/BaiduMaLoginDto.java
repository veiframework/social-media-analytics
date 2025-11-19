package com.chargehub.auth.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class BaiduMaLoginDto {

    @ApiModelProperty(value = "百度小程序code",required = true)
    @NotEmpty(message = "百度小程序code不能为空")
    private String baiduMaCode;
}

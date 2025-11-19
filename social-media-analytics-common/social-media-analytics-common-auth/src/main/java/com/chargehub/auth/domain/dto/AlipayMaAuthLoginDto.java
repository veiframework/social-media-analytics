package com.chargehub.auth.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/10/11 13:33
 * @Project：chargehub
 * @Package：com.chargehub.auth.domain.dto
 * @Filename：AlipayMaAuthLoginDto
 */
@Data
public class AlipayMaAuthLoginDto {


    @ApiModelProperty(value = "支付宝code",required = true)
    @NotEmpty(message = "支付宝小程序code不能为空")
    private String aliMaCode;

    @ApiModelProperty(value = "支付宝返回加密信息 response",required = true)
    @NotEmpty(message = "支付宝小程序返回加密信息不能为空")
    private String response;

//    @ApiModelProperty(value = "签名",required = true)
//    @NotEmpty(message = "签名不能为空")
//    private String sign;
}

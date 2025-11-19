package com.chargehub.auth.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/15 14:12
 * @Project：chargehub
 * @Package：com.chargehub.auth.form
 * @Filename：AlipayMaLoginDto
 */
@Data
public class AlipayMaLoginDto {

    @ApiModelProperty(value = "支付宝code",required = true)
    @NotEmpty(message = "支付宝小程序code不能为空")
    private String aliMaCode;
}

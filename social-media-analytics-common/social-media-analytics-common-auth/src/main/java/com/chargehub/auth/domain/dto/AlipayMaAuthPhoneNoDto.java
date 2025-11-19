package com.chargehub.auth.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 15:19
 * @Project：chargehub
 * @Package：com.chargehub.auth.form
 * @Filename：AppWxBody
 */
@Data
public class AlipayMaAuthPhoneNoDto {


    @ApiModelProperty(value = "支付宝返回加密信息 response",required = true)
    @NotEmpty(message = "response不能为空")
    private String response;

//    @ApiModelProperty(value = "支付宝返回签名",required = false)
//    private String sign;
}

package com.chargehub.auth.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/15 14:40
 * @Project：chargehub
 * @Package：com.chargehub.auth.form
 * @Filename：AuthPhoneNoRegisterDto
 */
@Data
public class AuthPhoneNoRegisterDto {

    @ApiModelProperty(value = "手机号",required = true)
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$",message = "手机号格式不正确")
    private String phone;

    @ApiModelProperty(value = "密码",required = true)
    @NotEmpty(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "授权手机号记录id",required = true)
    @NotEmpty(message = "授权手机号记录id不能为空")
    private String authId;

    @ApiModelProperty(value = "推荐人id",required = false)
    private Integer inviterId;
}

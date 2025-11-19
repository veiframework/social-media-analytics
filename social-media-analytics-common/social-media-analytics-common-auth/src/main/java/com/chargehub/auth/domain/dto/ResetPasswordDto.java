package com.chargehub.auth.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/10 19:02
 * @Project：chargehub
 * @Package：com.chargehub.auth.form
 * @Filename：AppUserBody
 */
@Data
public class ResetPasswordDto {

    @ApiModelProperty(value = "手机号",required = true)
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phone;

    @ApiModelProperty(value = "验证码",required = true)
    @NotEmpty(message = "验证码不能为空")
    private String verifCode;

    @ApiModelProperty(value = "新密码",notes = "新密码非必填 不填默认手机号后六位",required = false)
    private String password;
}

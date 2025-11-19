package com.chargehub.auth.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/5/9 13:43
 * @Project：chargehub
 * @Package：com.chargehub.auth.domain.dto
 * @Filename：UpdateUserPasswordDto
 */
@Data
public class UpdateUserPasswordDto {

    @ApiModelProperty(value = "新密码", required = true)
    private String newPassword;
}

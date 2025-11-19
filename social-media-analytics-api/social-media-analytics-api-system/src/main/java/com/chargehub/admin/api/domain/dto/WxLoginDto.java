package com.chargehub.admin.api.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Zhanghaowei
 * @date 2025/08/04 17:34
 */
@Data
public class WxLoginDto {

    @NotBlank
    private String openId;

    private String unionId;

}

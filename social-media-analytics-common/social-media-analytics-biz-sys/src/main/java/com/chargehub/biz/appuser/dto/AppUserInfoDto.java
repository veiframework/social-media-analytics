package com.chargehub.biz.appuser.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Zhanghaowei
 * @date 2025/08/02 19:34
 */
@Data
public class AppUserInfoDto {

    @NotBlank
    private String id;

    @NotBlank
    private String nickname;

    @NotBlank
    private String avatar;


    private String phone;


}

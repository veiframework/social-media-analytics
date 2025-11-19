package com.chargehub.auth.domain.dto;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/9/12 10:45
 * @Project：chargehub
 * @Package：com.chargehub.auth.domain.dto
 * @Filename：EmbedLoginDto
 */
@Data
public class EmbedLoginDto {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 第三方token
     */
    private String thirdPartyToken;
}

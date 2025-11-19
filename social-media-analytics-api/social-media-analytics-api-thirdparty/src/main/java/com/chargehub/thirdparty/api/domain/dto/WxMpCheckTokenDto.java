package com.chargehub.thirdparty.api.domain.dto;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 14:35
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.dto
 * @Filename：WxMpCheckTokenDto
 */
@Data
public class WxMpCheckTokenDto {

    /**
     * 签名
     */
    private String signature;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 随机字符串
     */
    private String nonce;
}

package com.chargehub.thirdparty.config.wx.mp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 14:01
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.config.wx.mp
 * @Filename：WechatMpProperties
 */
@Data
@ConfigurationProperties(prefix = "wechat.mp")
public class WechatMpProperties {
    private List<MpConfig> configs;

    @Data
    public static class MpConfig {
        /**
         * 设置微信公众号的appid
         */
        private String appId;

        /**
         * 设置微信公众号的app secret
         */
        private String secret;

        /**
         * 设置微信公众号的token
         */
        private String token;

        /**
         * 设置微信公众号的EncodingAESKey
         */
        private String aesKey;
    }
}

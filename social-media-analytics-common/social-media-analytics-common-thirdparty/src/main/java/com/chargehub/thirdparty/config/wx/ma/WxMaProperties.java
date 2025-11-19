package com.chargehub.thirdparty.config.wx.ma;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 13:59
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.config.wx.ma
 * @Filename：WxMaProperties
 */
@Data
@ConfigurationProperties(prefix = "wechat.ma")
public class WxMaProperties {

    private List<Config> configs;

    @Data
    public static class Config {
        /**
         * 设置微信小程序的appid
         */
        private String appId;

        /**
         * 设置微信小程序的Secret
         */
        private String secret;

        /**
         * 设置微信小程序消息服务器配置的token
         */
        private String token;

        /**
         * 设置微信小程序消息服务器配置的EncodingAESKey
         */
        private String aesKey;

        /**
         * 消息格式，XML或者JSON
         */
        private String msgDataFormat;
    }

}

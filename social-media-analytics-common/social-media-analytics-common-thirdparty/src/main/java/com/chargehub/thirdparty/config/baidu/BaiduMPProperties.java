package com.chargehub.thirdparty.config.baidu;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "baidu")
public class BaiduMPProperties {

    private String mpAppKey;

    private String mpAppSecret;

    private String mpUrl;

    /**
     * accessToken在redis过期时间
     */
    private Long accessTokenExpire;

}
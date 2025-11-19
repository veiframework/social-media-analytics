package com.chargehub.thirdparty.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhanghaowei
 * @date 2024/07/30 15:22
 */
@Data
@Configuration
@ConfigurationProperties("chargehub.amap")
public class ChargehubAmapProperties {

    private String url;

    private String appId;

    private String merchantId;

    private String merchantKey;

    private String merchantPrivateKey;

    private String merchantPublicKey;

    private String amapPublicKey;

    public String getUrl() {
        return url + "?key=" + this.getMerchantKey();
    }

}

package com.chargehub.common.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 运营商授权配置
 * Date: 2023/10
 *
 * @author TiAmo@13721682347@163.com
 */
@Configuration
@ConfigurationProperties(prefix = "chargehub.icn")
public class IcnOperatorEndpointProperties {

    /**
     * token 过期时间
     */
    private Duration tokenExpireTime = Duration.ofSeconds(7200);

    /**
     * token密钥
     */
    private String jwtSecretKey = "1234567890qwertyu";

    /**
     * 归属运营商ID（当前服务运营商ID）
     */
    private String ownerOperatorId;

    /**
     * 监管机构Id
     */
    private String regulatorOperatorId;

    /**
     * 端点配置信息
     */
    private Map<String, IcnOperatorEndpointConfig> endpoints = new HashMap<>(16);

    /**
     * 是否是接入方
     */
    private boolean accessor;


    public Duration getTokenExpireTime() {
        return tokenExpireTime;
    }

    public void setTokenExpireTime(Duration tokenExpireTime) {
        this.tokenExpireTime = tokenExpireTime;
    }

    public String getJwtSecretKey() {
        return jwtSecretKey;
    }

    public void setJwtSecretKey(String jwtSecretKey) {
        this.jwtSecretKey = jwtSecretKey;
    }

    public String getOwnerOperatorId() {
        return ownerOperatorId;
    }

    public void setOwnerOperatorId(String ownerOperatorId) {
        this.ownerOperatorId = ownerOperatorId;
    }

    public String getRegulatorOperatorId() {
        return regulatorOperatorId;
    }

    public void setRegulatorOperatorId(String regulatorOperatorId) {
        this.regulatorOperatorId = regulatorOperatorId;
    }

    public Map<String, IcnOperatorEndpointConfig> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<String, IcnOperatorEndpointConfig> endpoints) {
        this.endpoints = endpoints;
    }

    public boolean isAccessor() {
        return accessor;
    }

    public void setAccessor(boolean accessor) {
        this.accessor = accessor;
    }


}

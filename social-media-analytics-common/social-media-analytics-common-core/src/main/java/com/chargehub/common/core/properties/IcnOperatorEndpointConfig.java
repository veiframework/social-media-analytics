package com.chargehub.common.core.properties;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 运营商授权配置
 * Date: 2023/10
 *
 * @author TiAmo@13721682347@163.com
 */
public class IcnOperatorEndpointConfig {

    /**
     * name
     */
    @JsonProperty("name")
    private String name;

    /**
     * 运营商ID
     */
    @JsonProperty("OperatorID")
    private String operatorID;

    /**
     * 密钥
     */
    @JsonProperty("secret")
    private String secret;

    /**
     * 加密Key
     */
    @JsonProperty("aesKey")
    private String aesKey;

    /**
     * keyIv
     */
    @JsonProperty("keyIv")
    private String keyIv;

    /**
     * 签名key
     */
    @JsonProperty("secretSign")
    private String secretSign;

    /**
     * apiUri
     */
    @JsonProperty("apiUri")
    private String apiUri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(String operatorID) {
        this.operatorID = operatorID;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getKeyIv() {
        return keyIv;
    }

    public void setKeyIv(String keyIv) {
        this.keyIv = keyIv;
    }

    public String getSecretSign() {
        return secretSign;
    }

    public void setSecretSign(String secretSign) {
        this.secretSign = secretSign;
    }

    public String getApiUri() {
        return apiUri;
    }

    public void setApiUri(String apiUri) {
        this.apiUri = apiUri;
    }
}

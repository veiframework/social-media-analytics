package com.chargehub.common.core.properties;

/**
 * @author Zhanghaowei
 * @date 2024/04/12 12:28
 */
public class HuaweiObs {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String staticDomain;

    private String privateBucketName;
    private String privateStaticDomain;


    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getStaticDomain() {
        return staticDomain;
    }

    public void setStaticDomain(String staticDomain) {
        this.staticDomain = staticDomain;
    }

    public String getPrivateBucketName() {
        return privateBucketName;
    }

    public void setPrivateBucketName(String privateBucketName) {
        this.privateBucketName = privateBucketName;
    }

    public String getPrivateStaticDomain() {
        return privateStaticDomain;
    }

    public void setPrivateStaticDomain(String privateStaticDomain) {
        this.privateStaticDomain = privateStaticDomain;
    }
}

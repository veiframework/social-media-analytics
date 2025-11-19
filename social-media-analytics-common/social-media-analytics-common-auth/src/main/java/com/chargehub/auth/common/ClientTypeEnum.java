package com.chargehub.auth.common;

public enum ClientTypeEnum {

    /**
     * "移动端"
     */
    H5("移动端"),
    /**
     * "小程序端"
     */
    WECHAT_MP("微信小程序端"),
    /**
     * "小程序端"
     */
    ALIYUN_MP("支付宝小程序端"),
    /**
     * 安卓移动应用端"
     */
    APP("移动应用端"),
    WX_APP("移动应用端微信支付"),

    ALIYUN_APP("移动应用端阿里云支付"),
    /**
     * "未知"
     */
    UNKNOWN("未知");

    private String clientName;

    ClientTypeEnum(String des) {
        this.clientName = des;
    }

    public String clientName() {
        return this.clientName;
    }

    public String value() {
        return this.name();
    }

}

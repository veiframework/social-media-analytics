package com.chargehub.common.core.enums;

/**
 * @author Zhanghaowei
 * @date 2024/05/06 19:03
 */
public enum PaySource {

    /**
     * 支付来源 0-PC  1-APP  3-微信小程序  4-支付宝小程序 5-WEB端 6-百度小程序 7-通联支付
     */

    PC("0"),
    APP("1"),
    WX_MA("3"),
    ALI_MA("4"),
    WEB("5"),
    BAIDU_MA("6"),
    ALLIN("7"),

    ;

    String code;

    PaySource(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

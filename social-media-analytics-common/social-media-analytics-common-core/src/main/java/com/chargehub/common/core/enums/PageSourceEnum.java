package com.chargehub.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PageSourceEnum {

    MA("1", "小程序"),

    ALIPAY_APP("2", "支付宝APP"),
    ;

    private final String code;

    private final String desc;


}

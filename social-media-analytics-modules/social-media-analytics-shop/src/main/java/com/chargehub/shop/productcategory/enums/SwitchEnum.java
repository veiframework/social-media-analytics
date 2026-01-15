package com.chargehub.shop.productcategory.enums;

import lombok.Getter;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Getter
public enum SwitchEnum {

    ENABLE("enable"),

    DISABLE("disable");

    private final String desc;

    SwitchEnum(String desc) {
        this.desc = desc;
    }


}

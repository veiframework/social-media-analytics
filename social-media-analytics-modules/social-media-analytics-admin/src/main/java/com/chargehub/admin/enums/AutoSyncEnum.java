package com.chargehub.admin.enums;

import lombok.Getter;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Getter
public enum AutoSyncEnum {

    ENABLE("enable"),
    DISABLE("disable");

    private final String desc;

    AutoSyncEnum(String desc) {
        this.desc = desc;
    }
}

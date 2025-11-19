package com.chargehub.common.core.enums;

import java.util.Objects;

/**
 * icn 启动充电充电接口枚举
 * 2023/10
 *
 * @author TiAmo(13721682347@163.com)
 */
public enum StartChargeStatEnum {

    START("1", "启动中"),

    CHARGE("2", "充电中"),

    STOPPING("3", "停止中"),

    END("4", "已结束"),

    UNKNOWN("5", "未知"),

    ;
    private final String code;

    private final String desc;

    StartChargeStatEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static StartChargeStatEnum get(String code) {
        for (StartChargeStatEnum e : values()) {
            if (Objects.equals(code, e.getCode())) {
                return e;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

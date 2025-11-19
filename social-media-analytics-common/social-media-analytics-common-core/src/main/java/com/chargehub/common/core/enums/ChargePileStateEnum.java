package com.chargehub.common.core.enums;

/**
 * @author Zhanghaowei
 * @date 2024/04/23 20:41
 */
public enum ChargePileStateEnum {

    NETWORK_ERR(2, "离网"),

    FAULT(3, "故障"),

    OFFLINE(4, "离线"),

    ONLINE(5, "在线");

    private final int code;

    private final String desc;

    ChargePileStateEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

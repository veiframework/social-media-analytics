package com.chargehub.common.core.enums;

@SuppressWarnings("LombokGetterMayBeUsed")
public enum ConnectorRealtimeAlarmCodeEnum {

    REAL_TIME_OFFLINE("1", "设备离线异常");

    private final String code;

    private final String desc;

    ConnectorRealtimeAlarmCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

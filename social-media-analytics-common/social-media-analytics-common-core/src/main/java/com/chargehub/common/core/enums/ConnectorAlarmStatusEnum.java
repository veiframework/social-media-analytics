package com.chargehub.common.core.enums;

public enum ConnectorAlarmStatusEnum {

    ALARM("0", "告警"),

    ALARM_END("10", "恢复");


    private final String code;

    private final String desc;

    ConnectorAlarmStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getCodeValue() {
        return Integer.valueOf(this.code);
    }
}

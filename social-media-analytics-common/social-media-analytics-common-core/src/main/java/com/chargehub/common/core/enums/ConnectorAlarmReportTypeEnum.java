package com.chargehub.common.core.enums;

@SuppressWarnings("LombokGetterMayBeUsed")
public enum ConnectorAlarmReportTypeEnum {

    CONNECTOR_ALARM("connector_alarm", "connector_alarm"),

    PLAT_ALARM("plat_alarm", "plat_alarm"),

    USER_ALARM("user_alarm", "user_alarm");


    private final String code;

    private final String desc;

    ConnectorAlarmReportTypeEnum(String code, String desc) {
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

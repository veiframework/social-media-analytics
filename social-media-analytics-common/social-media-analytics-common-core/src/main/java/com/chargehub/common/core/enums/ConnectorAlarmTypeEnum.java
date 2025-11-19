package com.chargehub.common.core.enums;

@SuppressWarnings("LombokGetterMayBeUsed")
public enum ConnectorAlarmTypeEnum {

    REAL_TIME("real_time", "real_time"),

    CHARGE_END_REASON("charge_end_reason", "charge_end_reason"),

    CHARGE_END_DATA_FAIL_REASON("charge_end_data_fail_reason", "charge_end_data_fail_reason"),

    CHARGE_START_FAIL_ERROR("charge_start_fail_alarm", "charge_start_fail_alarm"),

    CHARGE_REALTIME_INTERRUPT_ALARM("charge_realtime_interrupt_alarm", "charge_realtime_interrupt_alarm");

    private final String code;

    private final String desc;

    ConnectorAlarmTypeEnum(String code, String desc) {
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

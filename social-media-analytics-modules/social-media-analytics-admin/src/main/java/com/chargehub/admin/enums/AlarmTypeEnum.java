package com.chargehub.admin.enums;

import lombok.Getter;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Getter
public enum AlarmTypeEnum {

    INTERVAL("interval", "workAlarmIntervalScheduler");

    private final String desc;

    private final String beanName;

    AlarmTypeEnum(String desc, String beanName) {
        this.desc = desc;
        this.beanName = beanName;
    }

    public static AlarmTypeEnum getByDesc(String desc) {
        for (AlarmTypeEnum value : AlarmTypeEnum.values()) {
            if (value.getDesc().equals(desc)) {
                return value;
            }
        }
        throw new IllegalArgumentException("不支持的告警类型");
    }

}

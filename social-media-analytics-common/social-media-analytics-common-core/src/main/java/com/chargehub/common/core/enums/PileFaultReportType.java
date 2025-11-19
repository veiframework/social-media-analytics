package com.chargehub.common.core.enums;

import java.time.Duration;
import java.util.Objects;

/**
 * PileFaultReportType 设备异常上报
 *
 * 2023/07/24
 *
 * @author TiAmo(13721682347@163.com)
 */
public enum PileFaultReportType {

	PILE("1", "设备上报", Duration.ofMinutes(0)),
	;

	private final String code;


	private final String desc;


    /**
     * 延迟时间
     */
    private final Duration delay;

    PileFaultReportType(String code, String desc, Duration delay) {
        this.code = code;
        this.desc = desc;
        this.delay = delay;
    }

    public static PileFaultReportType getByCode(String code) {
        for (PileFaultReportType e : values()) {
            if (Objects.equals(code, e.getCode())) {
                return e;
            }
        }
        throw new RuntimeException("enum not exists.");
    }


    /**
     * 拼接缓存名字
     * @param text text
     * @return String
     */
    public String joinKey(String text){
        return this.getCode() + ":" + text;
    }



    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public Duration getDelay() {
        return delay;
    }
}

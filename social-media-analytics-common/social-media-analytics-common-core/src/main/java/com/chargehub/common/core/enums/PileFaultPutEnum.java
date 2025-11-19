package com.chargehub.common.core.enums;

import java.util.Objects;

/**
 * 事件汇报枚举
 * 2023/07/24
 *
 * @author TiAmo(13721682347@163.com)
 */
public enum PileFaultPutEnum {

    CONFIG_EVENT("1", "电表故障"),

    SCREEN_FAULT_EVENT("2", "显示器故障"),

    CHARGE_MODULE("3", "充电模块故障"),

	INSULATION_DETECTION("4", "绝缘检测模块故障"),

	SWIPING_CARD("5", "刷卡器故障"),

	PILE_OFF_LINE("6", "设备离线"),

	UPDATER_LOAD("7", "升级文件下载失败"),

	FTP_SERVER_ERROR("8", "FTP服务异常"),

	COMMUNICATION_PART("9", "通讯报文故障"),

	EMERGENCY_STOP("10", "急停故障"),

	street_lamp("11", "路灯故障"),

    ;
    private final String code;

    private final String desc;

    PileFaultPutEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PileFaultPutEnum getByCode(String code) {
        for (PileFaultPutEnum e : values()) {
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

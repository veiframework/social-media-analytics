package com.chargehub.common.core.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备控制枚举
 * 2023/08/09
 *
 * @author TiAmo(13721682347 @ 163.com)
 */

public enum MessageCodeEnums {


    // >>>>>>>>>>>>>>>> 远程控制 >>>>>>>>>>>>>>>>

    PILE_START_CHARGE("PILE_START_CHARGE", "设备开启充电"),

    PILE_STOP_CHARGE("PILE_STOP_CHARGE", "设备关闭充电"),

    ELECTRICITY_PRICE("PILE_ELECTRICITY_PRICE_SEND", "电价下发"),

    PILE_PARAMETER_UPDATE("PILE_PARAMETER_UPDATE", "设备远程参数下发"),

    PILE_PARAMETER_READ("PILE_PARAMETER_READ", "设备远程参数读取"),

    PILE_PARAMETER_READ_SEND("PILE_PARAMETER_READ_SEND", "设备远程参数读取请求下发"),

    CHARGE_CONTROL_INFORM_RESP("CHARGE_CONTROL_INFORM_RESP", "启动充电结果通知"),


    // <<<<<<<<<<<<<<<<< 远程控制 <<<<<<<<<<<<<<<<<


    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 远程控制应答 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    PILE_MESSAGE_COMMON_RESP("PILE_MESSAGE_COMMON_RESP", "远程控制请求应答"),
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 远程控制应答 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 事件汇报 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    EVENT_CONFIG_EVENT("EVENT_CONFIG_ARG", "充电参数配置信息"),

    EVENT_HAND_SHAKE("EVENT_HAND_SHAKE", "充电握手事件"),

    EVENT_CHARGE_FINISH("EVENT_CHARGE_FINISH", "充电完成事件"),

    SWIPE_CARD_QUERY("SWIPE_CARD_QUERY", "刷卡查询事件"),

    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 事件汇报 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    // >>>>>>>>>>>>>>>>>>>>>>> 数据汇报 >>>>>>>>>>>>>>>>>>>>>>>

    PILE_HEART_BEAT("PILE_HEART_BEAT", "设备心跳"),

    PILE_ONLINE("PILE_ONLINE", "充电桩实时状态"),

    PILE_CHARGE_STAGE("PILE_CHARGE_STAGE", "数据汇报-充电桩电阶信息"),

    // <<<<<<<<<<<<<<<<<<<<<<<< 数据汇报 <<<<<<<<<<<<<<<<<<<<<<<<


    // >>>>>>>>>>>>>>>>>>>>>>> 业务端 >>>>>>>>>>>>>>>>>>>>>>>

    PILE_UPDATE("PILE_UPDATE", "设备更新"),

    PILE_AUTH("PILE_AUTH", "设备认证"),

    PILE_OFFLINE("PILE_OFFLINE", "设备下线"),

    // <<<<<<<<<<<<<<<<<<<<<<<< 业务端 <<<<<<<<<<<<<<<<<<<<<<<<

    APP_CHARGE_ORDER_STATEMENT("APP_CHARGE_ORDER_STATEMENT", "APP 订单结算通知"),


    // >>>>>>>>>>>>>>>>>>>>>>> 设备异常上报 >>>>>>>>>>>>>>>>>>>>>>>

    PILE_FAULT("PILE_FAULT", "设备异常上报"),

    // <<<<<<<<<<<<<<<<<<<<<<<< 设备异常上报 <<<<<<<<<<<<<<<<<<<<<<<<


    WARNING("WARNING", "压制警告");

    private final String code;

    private final String desc;

    MessageCodeEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Map<String, MessageCodeEnums> map = Arrays.stream(values()).collect(Collectors.toMap(MessageCodeEnums::getCode, v -> v));

    public static MessageCodeEnums getByCode(String code) {
        return map.get(code);
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

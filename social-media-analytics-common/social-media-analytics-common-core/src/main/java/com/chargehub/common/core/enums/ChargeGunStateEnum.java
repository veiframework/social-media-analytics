package com.chargehub.common.core.enums;

/**
 * 充电枪状态
 * 0.脱机(枪未接车)
 * 1.空闲(枪接了车未开充充电)
 * 2.握手
 * 3.配置
 * 4.充电中
 * 5.结束
 * 6.故障
 *
 * @author PhilShen
 */

@SuppressWarnings("unused")
public enum ChargeGunStateEnum {


    UN_KNOW(-1, "离线","充电枪已离线，请更换其他桩充电"),

    /**
     * 0.脱机(枪未接车)
     */
    OFFLINE(0, "空闲","充电枪未连接，请检查充电枪"),

    /**
     * 1.空闲(枪接了车未开充充电)
     */
    FREE(1, "已连接",null),

    /**
     * 握手
     */
    SHAKE_HAND(2, "充电中","当前枪有未结束订单，请勿多次开启充电"),

    /**
     * 参数配置
     */
    CONFIG(3, "充电中","当前枪有未结束订单，请勿多次开启充电"),

    /**
     * 充电中
     */
    CHARGING(4, "充电中","当前枪有未结束订单，请勿多次开启充电"),

    /**
     * 5.结束
     */
    CHARGE_END(5, "结束","充电枪未连接，请检查充电枪"),

    /**
     * 急停故障
     */
    FAULT(6, "急停故障","设备处于急停状态，无法充电，请旋转充电桩上急停按钮恢复"),

    /**
     * 设备锁定
     */
    GUN_LOCK(7, "设备锁定","充电枪设备锁定"),

    /**
     * 网络异常
     */
    NET_EXCEPTION(8, "网络异常","充电枪网络异常"),

    /**
     * 待检修
     */
    WAIT_REPAIR(9, "故障","充电枪设备故障，请更换其他设备"),

    /**
     * 100.连接中断
     */
    NOT_CONNECT(100, "连接中断","充电枪连接中断"),

    /**
     * 101.等待开始充电
     */
    WAITE_CHARGE(101, "等待开始充电","充电枪等待开始充电");


    ChargeGunStateEnum(int code, String desc,String errMsg) {
        this.code = code;
        this.desc = desc;
        this.errMsg = errMsg;
    }

    private final int code;

    private final String desc;

    private final String errMsg;

    public static ChargeGunStateEnum getType(int code) {
        for (ChargeGunStateEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }


    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getErrMsg() {
        return errMsg;
    }
}

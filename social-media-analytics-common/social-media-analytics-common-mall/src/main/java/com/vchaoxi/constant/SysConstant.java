package com.vchaoxi.constant;

import java.math.BigDecimal;

public class SysConstant {


    /**
     *
     */
    public static final String TOKEN_KEY = "X-Wine-Mall-Token";

    public static final String LOCKER_NO_KEY = "X-Vc-Locker-No";
    public static final String APP_VERSION_KEY = "X-Vc-App-Version";
    public static final String LOCKER_SCREEN_SIZE = "X-Vc-Locker-Screen-Size";
    public static final String LOCK_APP_VERSION_KEY = "X-Vc-Locker-Version";


    /**
     * 代理商id
     */
    public static final String AGENT_ID = "X-Agent-Id";


    /**
     * token有效期(小时)
     */
    public static final Integer TOKEN_VALIDITY = 120;




    public static final String PLATFORM_USER = "user";
    public static final String PLATFORM_SHOP = "shop";
    public static final String PLATFORM_DISPATCHER = "dispatcher";
    public static final String PLATFORM_ADMIN = "admin";
    public static final String PLATFORM_LOCKER = "locker";


    /**
     * 系统占位符
     */
    public static final String SYS_OCCUPY = "%code";


    /**
     * 系统加密方式
     */
    public static final String ENCRYPT_TYPE = "MD5";


    /**
     * 系统提现手续费
     */
    public static final BigDecimal WITHDRAWAL_SERVICE_RATE = new BigDecimal("0.006");


    /**
     * 系统内用户账号基数
     */
    public static final Integer USER_ACCOUNT_BASE = 100000;

    /**
     * 未知的用户id  当管理员直接创建订单时 无法直接关联用户id 则使用该值代替
     *
     * -1 未知的(无法关联用户的)
     *  0 未绑定的（还未绑定的）
     */
    public static final Integer UNKNOWN_USERID  = -1;
    public static final Integer UNBOUND_USERID  = 0;


    public static final String COMMISSION_RECORD_COMMISSION = "commission";
    public static final String COMMISSION_RECORD_WITHDRAW = "withdraw";

    public static final String COMMISSION_RECORD_RECEIVED = "received";
    public static final String COMMISSION_RECORD_WAITING = "waiting";


}

package com.chargehub.common.core.constant;

/**
 * 缓存常量信息
 *
 * @author ruoyi
 */
public class CacheConstants {
    /**
     * 缓存有效期，默认720（分钟）
     */
    public final static long EXPIRATION = 720;

    /**
     * 缓存有效期
     */
    public final static long APP_EXPIRATION = 60 * 24 * 7;

    /**
     * 缓存刷新时间，默认120（分钟）
     */
    public final static long REFRESH_TIME = 120;


    /**
     * 缓存刷新时间，默认1440（分钟）
     */
    public final static long APP_REFRESH_TIME = 1440;


    /**
     * 密码最大错误次数
     */
    public final static int PASSWORD_MAX_RETRY_COUNT = 5;

    /**
     * 密码锁定时间，默认10（分钟）
     */
    public final static long PASSWORD_LOCK_TIME = 10;

    /**
     * 权限缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * APP权限缓存前缀
     */
    public final static String APP_LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";


    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String USER_PWD_ERR_CNT_KEY = "user_pwd_err_cnt:";


    /**
     * 授权手机号记录 redis key
     */
    public static final String AUTH_PHONE_KEY = "auth_phone_key:";

    /**
     * 登录IP黑名单 cache key
     */
    public static final String SYS_LOGIN_BLACKIPLIST = SYS_CONFIG_KEY + "sys.login.blackIPList";

    /**
     * 计费下发任务锁
     */
    public static final String CHARGE_BILLING_TASK_LOCK_KEY = "charge_billing_task:lock:";

    /**
     * 用户id缓存
     */
    public static final String SYS_USER_ID_KEY = "sys:userid:key:";

    /**
     * 充电桩缓存
     */
    public static final String CHARGE_PILE_KEY = "charge:pile:key:";

    /**
     * 枪缓存
     */
    public static final String CHARGE_GUN_KEY = "charge:gun:key:";

    /**
     * 区域列表缓存
     */
    public static final String REGION_KEY = "charge:region:key:";

    /**
     * 思极星能订单结算开始时间
     */
    public static final String SIJINENG_ORDER_SETTLEMENT_BEGIN_TIME = "SIJIORDERSENDJOB:BEGINTIME";

    /**
     * 支付回调宿主地址
     */
    public static final String NOTIFY_HOST = "notifyHost";

    /**
     * 充值回调锁
     */
    public static final String RECHARGE_LOCK_NOTIFY_KEY = "recharge:lock:notify:key:";

    /**
     * 退款锁
     */
    public static final String WALLET_REFUND_KEY = "recharge:lock:refund:key:";

    /**
     * 枪占用时长
     */
    public static final String PILE_ONLINE_GUN_USE_TIME_KEY = "charge:pile_gun_user_time:";

    /**
     * 状态锁
     */
    public static final String PILE_STATE_LOCK = "charge:state:lock:";

    /**
     * 心跳锁
     */
    public static final String PILE_HEARTBEAT_LOCK = "charge:heart:lock:";


    public static final String PILE_ONLINE_LOCK = "charge:online:lock:";

    public static final String PILE_METRICS_LOCK = "charge:metrics:lock:";

    public static final String PILE_MQ_GUN_CACHE = "PILE_MQ_GUN_CACHE";

    public static final String PILE_MQ_CACHE = "PILE_MQ_CACHE";

    public static final String STATION_PRICE = "charge:station-price:key:";

    public static final String CHARGE_STAGE_MONITOR = "charge:stage:monitor:";

    public static final String REALTIME_CHARGE_MONITOR = "charge:realtime:monitor:";

    public static final String BAIDU_ACCESS_TOKEN = "BAIDU_ACCESS_TOKEN";

    public static final String ICN_STATION_PRICE_SYNC_LOCK = "icn_station_price_sync_lock:";

    public static final String ICN_STATION_PRICE_SYNC_LOCK0 = "icn_station_price_operator_sync_lock:";

    /**
     * 支付宝退款查询次数
     */
    //public static final String ALIPAY_REFUND_QUERY_NUM = "charge:pay:alipay:refund:query_num:";

    public static final String SYNCING_WORK_LOCK = "SYNCING_WORK_LOCK";

    public static final String WORK_NEXT_CRAWL_TIME = "WORK_NEXT_CRAWL_TIMESTAMP";
}

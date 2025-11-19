package com.chargehub.payment;

import cn.hutool.core.util.IdUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Zhanghaowei
 * @date 2025/04/25 11:23
 */
public class PaymentConstant {

    private PaymentConstant() {
    }

    public static final DateTimeFormatter Y_M_D_H = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    /**
     * 成功.
     */
    public static final String SUCCESS = "SUCCESS";

    /**
     * 失败.
     */
    public static final String FAIL = "FAIL";

    public static final String OK = "OK";

    public static final String Y = "Y";

    public static final String OUT_TRADE_NO = "out_trade_no";

    public static final String TRADE_NO = "trade_no";

    public static final String TRADE_TYPE = "tradeType";

    public static final String OUT_BIZ_NO = "out_biz_no";

    public static final String COMPLETE_REFUND = "completeRefund";

    public static final String BUSINESS_TYPE_CODE = "businessTypeCode";

    public static final String PASSBACK_PARAMS = "passback_params";

    public static final String PAYMENT_CONFIG_ID = "paymentConfigId";

    public static final String TOTAL_REFUND_FEE = "totalRefundFee";

    public static final BigDecimal HUNDRED = new BigDecimal("100");

    public static final String TRADE_STATUS = "trade_status";

    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";

    /**
     * 通联微信小程序支付方式
     */
    public static final String W06 = "W06";

    /**
     * 通联支付宝JS支付方式
     */
    public static final String A02 = "A02";


    public static final String TRX_STATUS = "trxstatus";

    public static final String CUS_ORDER_ID = "cusorderid";

    public static final String TRX_ID = "trxid";

    public static final String REQ_SN = "reqsn";

    public static final String TRX_SUCCESS = "0000";

    public static final String ERR_MSG = "errMsg";

    public static final String TENANT_ID = "tenantId";

    public static String uniqueId() {
        String format = PaymentConstant.Y_M_D_H.format(LocalDateTime.now());
        String string = IdUtil.getSnowflakeNextIdStr();
        return String.format("%s%s%s", format, string, "0");
    }

}

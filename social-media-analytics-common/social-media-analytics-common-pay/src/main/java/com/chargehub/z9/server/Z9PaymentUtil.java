package com.chargehub.z9.server;


import com.chargehub.payment.bean.PaymentNotifyParam;
import com.chargehub.payment.bean.PaymentRefundNotifyParam;

/**
 * @author Zhanghaowei
 * @date 2025/04/25 16:17
 */
public class Z9PaymentUtil {

    private Z9PaymentUtil() {
    }

    public static PaymentNotifyParam buildPaymentNotifyParam(Object rawData, String configId, String businessTypeCode) {
        PaymentNotifyParam paymentNotifyParam = new PaymentNotifyParam();
        paymentNotifyParam.setRawData(rawData);
        paymentNotifyParam.setBusinessTypeCode(businessTypeCode);
        paymentNotifyParam.setPaymentConfigId(configId);
        return paymentNotifyParam;
    }

    public static PaymentRefundNotifyParam buildPaymentRefundNotifyParam(Object rawData, String configId, String businessTypeCode) {
        PaymentRefundNotifyParam paymentNotifyParam = new PaymentRefundNotifyParam();
        paymentNotifyParam.setRawData(rawData);
        paymentNotifyParam.setBusinessTypeCode(businessTypeCode);
        paymentNotifyParam.setPaymentConfigId(configId);
        return paymentNotifyParam;
    }

}

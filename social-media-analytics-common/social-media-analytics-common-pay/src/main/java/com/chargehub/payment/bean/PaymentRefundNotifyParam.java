package com.chargehub.payment.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Zhanghaowei
 * @date 2025/04/24 14:19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class PaymentRefundNotifyParam extends PaymentNotifyParam {

    /**
     * 商户退款单号
     */
    private String outRefundNo;

}

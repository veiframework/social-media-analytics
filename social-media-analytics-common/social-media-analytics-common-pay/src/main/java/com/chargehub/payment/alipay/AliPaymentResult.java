package com.chargehub.payment.alipay;

import com.chargehub.payment.bean.PaymentResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Zhanghaowei
 * @date 2025/04/03 18:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class AliPaymentResult extends PaymentResult {

    private static final long serialVersionUID = 9187439581292933920L;
    /**
     * 支付宝端订单号
     */
    private String tradeNo;

}

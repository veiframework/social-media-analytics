package com.chargehub.payment.alipay;

import com.chargehub.payment.bean.PaymentRefundParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Zhanghaowei
 * @date 2025/04/07 19:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class AliPaymentRefundParam extends PaymentRefundParam {
    private static final long serialVersionUID = 172492510603522805L;



    private String appAuthToken;

}

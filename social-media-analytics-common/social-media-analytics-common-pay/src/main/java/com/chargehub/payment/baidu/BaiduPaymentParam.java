package com.chargehub.payment.baidu;

import com.chargehub.payment.bean.PaymentParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/04/12 10:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class BaiduPaymentParam extends PaymentParam implements Serializable {
    private static final long serialVersionUID = 6894091686134359141L;



}

package com.chargehub.payment.alipay;

import com.chargehub.payment.bean.PaymentParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/04/03 17:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class AliPaymentParam extends PaymentParam implements Serializable {

    private static final long serialVersionUID = -1213090441910180463L;




    /**
     * 产品码。
     * 商家和支付宝签约的产品码。 枚举值（点击查看签约情况）：
     * <a target="_blank" href="https://opensupport.alipay.com/support/codelab/detail/486/487">FACE_TO_FACE_PAYMENT</a>：当面付产品；
     * 默认值为FACE_TO_FACE_PAYMENT。
     */
    private String productCode;

    /**
     * 订单总金额。 单位为元，精确到小数点后两位，取值范围：[0.01,100000000] 。
     */
    @NotBlank
    @Digits(integer = 10, fraction = 2)
    private String totalAmount;

    /**
     * 新版openId
     */
    private String buyerOpenId;

    /**
     * 旧版openId
     */
    private String buyerId;


    /**
     * 订单标题。
     * 注意：不可使用特殊字符，如 /，=，& 等。
     */
    private String subject;


    /**
     * 订单附加信息。
     * 如果请求时传递了该参数，将在异步通知、对账单中原样返回，同时会在商户和用户的pc账单详情中作为交易描述展示
     */
    private String body;


    /**
     * 订单相对超时时间。从交易创建时间开始计算。
     * 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
     * 当面付场景默认值为3h，如需指定，推荐设置5m及以上。
     * <p>
     * 注：time_expire和timeout_express两者只需传入一个或者都不传，如果两者都传，优先使用time_expire。
     */
    private String timeoutExpress;


    /**
     * 子商户的支付授权码
     */
    private String appAuthToken;
}

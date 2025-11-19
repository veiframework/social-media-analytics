package com.chargehub.payment.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2025/04/02 17:25
 */
@Data
public class PaymentRefundParam implements Serializable {
    private static final long serialVersionUID = -5546330406785842324L;

    @Schema(description = "业务类型编码支付时的业务类型如钱包充值, 月卡充值等")
    private String businessTypeCode = "default";

    /**
     * 付款方式编号  对应微信支付内的tradeType  支付宝内的productCode
     */
    private String tradeType;

    /**
     * 商家支付单号 【必传】, 我方支付订单号
     */
    @NotBlank
    private String outTradeNo;

    /**
     * 退款单号  【必传】, 我方退款订单号
     */
    private String outRefundNo;

    /**
     * 订单金额 前端使用元, 后端会转成分
     */
    @Digits(integer = 10, fraction = 2)
    @NotBlank
    private String totalFee;

    /**
     * 本次退款金额 前端使用元, 后端会转成分
     */
    @NotBlank
    @Digits(integer = 10, fraction = 2)
    private String refundFee;

    /**
     * 退款原因  【可选】
     */
    private String reason;


    /**
     * 第三方交易订单id
     */
    private String transactionId;

    private Map<String, String> extendParams = new HashMap<>(16);

}

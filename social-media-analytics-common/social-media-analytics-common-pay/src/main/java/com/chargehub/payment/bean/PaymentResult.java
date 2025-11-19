package com.chargehub.payment.bean;

import com.chargehub.payment.PaymentChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2025/04/02 17:24
 */
@Data
public class PaymentResult implements Serializable {

    private static final long serialVersionUID = 8099803868608281463L;

    /**
     * 统一下单 订单创建结果  true：成功  false：失败
     */
    private Boolean result;

    /**
     * 提示描述
     */
    private String msg;

    /**
     * 商家单号
     */
    private String outTradeNo;

    /**
     * 总金额
     */
    private BigDecimal totalFee;

    /**
     * 支付渠道
     */
    private PaymentChannel paymentChannel;

    /**
     * 付款方式
     * 微信支付：
     *  NATIVE
     *  JSAPI
     *  MWEB
     *
     * 支付宝支付：
     *  JSAPI_PAY
     *
     * 通联支付：
     * W01	微信扫码支付
     * W02	微信JS支付
     * W03	微信APP支付
     * W06	微信小程序支付
     * W11	微信订单支付
     * A01	支付宝扫码支付
     * A02	支付宝JS支付
     * A03	支付宝APP支付
     * U01	银联扫码支付(CSB)
     * U02	银联JS支付
     * S01	数币扫码支付
     * S03	数字货币H5
     * N03	网联支付
     */
    private String tradeType;

    @Schema(description = "额外参数,如支付配置id等")
    private Map<String, String> extendParam = new HashMap<>(16);

    public PaymentResult() {
    }

    public PaymentResult(Boolean result) {
        this.result = result;
    }


}

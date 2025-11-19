package com.chargehub.payment.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2025/04/02 17:22
 */
@Data
public class PaymentParam implements Serializable {
    private static final long serialVersionUID = 7105710125828706007L;



    /**
     * 商家端单号  【必传】,我方支付订单号
     */
    private String outTradeNo;

    @Schema(description = "支付方式如MINI_PROGRAM, H5, FACE_IDENTIFY, APP")
    private String clientPaymentMethod;

    @Schema(description = "业务类型编码支付时的业务类型如钱包充值, 月卡充值等")
    private String businessTypeCode = "default";

    @Schema(description = "额外参数,如支付配置id等")
    private Map<String, String> extendParam = new HashMap<>(16);


}

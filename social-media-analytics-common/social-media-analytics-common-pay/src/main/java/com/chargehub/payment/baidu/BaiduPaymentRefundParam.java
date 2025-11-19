package com.chargehub.payment.baidu;

import com.chargehub.payment.bean.PaymentRefundParam;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/04/22 14:22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class BaiduPaymentRefundParam extends PaymentRefundParam implements Serializable {
    private static final long serialVersionUID = 1594787010701282605L;

    @JsonProperty("access_token")
    private String accessToken;

    private Integer applyRefundMoney;

    private String bizRefundBatchId;

    private Integer isSkipAudit;

    private Long orderId;

    private String refundReason;

    private Integer refundType;

    private String tpOrderId;

    private Long userId;

    private String refundNotifyUrl;

    private String pmAppKey;

}

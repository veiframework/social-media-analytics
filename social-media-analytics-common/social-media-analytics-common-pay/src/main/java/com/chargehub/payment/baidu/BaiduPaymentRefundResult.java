package com.chargehub.payment.baidu;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/04/22 14:48
 */
@Data
public class BaiduPaymentRefundResult implements Serializable {
    private static final long serialVersionUID = 2744621336901562109L;

    private BaiduResponseData data;

    private Integer errno;

    private String msg;

    @Data
    public static class BaiduResponseData implements Serializable{

        private static final long serialVersionUID = 4660890746753885985L;

        private String refundBatchId;

        private Integer refundPayMoney;

    }
}

package com.chargehub.payment.baidu;

import com.chargehub.payment.bean.PaymentResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2025/04/12 11:00
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class BaiduPaymentResult extends PaymentResult implements Serializable {
    private static final long serialVersionUID = 2676855592171923762L;

    private String appKey;

    private String dealId;

    private String tpOrderId;

    private String totalAmount;

    private String notifyUrl;

    private String dealTitle;

    private Integer signFieldsRange;

    private String rsaSign;


    public String rsaSignResult(BaiduPaymentProperties config) {
        Map<String, Object> map = new HashMap<>();
        map.put("appKey", this.appKey);
        map.put("dealId", this.dealId);
        map.put("tpOrderId", this.tpOrderId);
        map.put("totalAmount", this.totalAmount);
        try {
            return BaiduRSASign.sign(map, config.getPrivateKey());
        } catch (Exception e) {
            log.error(" [Z9] baidu payment sign failed{}", e.getMessage());
            return null;
        }
    }

}

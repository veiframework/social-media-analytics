package com.chargehub.payment.baidu;


import com.chargehub.payment.bean.BasePaymentProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/04/03 15:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaiduPaymentProperties extends BasePaymentProperties implements Serializable {
    private static final long serialVersionUID = -4712017287922623527L;


    private String appKey;

    private String publicKey;

    private String privateKey;

    private String url;

    private String dealId;



}

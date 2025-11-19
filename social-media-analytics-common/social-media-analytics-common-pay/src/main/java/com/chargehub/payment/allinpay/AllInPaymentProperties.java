package com.chargehub.payment.allinpay;


import com.chargehub.payment.bean.BasePaymentProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * https://prodoc.allinpay.com/doc
 *
 * @author Zhanghaowei
 * @date 2025/04/03 15:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AllInPaymentProperties extends BasePaymentProperties implements Serializable {
    private static final long serialVersionUID = -3427089668355563619L;


    private String apiUrl = "https://vsp.allinpay.com/apiweb/unitorder";

    private String appId = "00335940";

    private String cusId = "56349305541TKHT";

    private String md5AppKey = "vVNDfMLJAMhuLptnFB1vvyZEHDto9xuj";

    private String signType = "MD5";

    private String version = "11";

    private String subAppId = "wxd51c2961f2e5f35b";


    public String getNotifyUrl(String id, String businessTypeCode) {
        return this.buildNotifyUrl(this.getNotifyUrl(), "allinpay", id, businessTypeCode);
    }

    public String getRefundNotifyUrl(String id, String businessTypeCode) {
        return this.buildNotifyUrl(this.getRefundNotifyUrl(), "allinpay", id, businessTypeCode);
    }

}

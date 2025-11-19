package com.chargehub.payment;


import com.chargehub.payment.alipay.AliPaymentProperties;
import com.chargehub.payment.allinpay.AllInPaymentProperties;
import com.chargehub.payment.baidu.BaiduPaymentProperties;
import com.chargehub.payment.wechat.WechatPaymentProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author Zhanghaowei
 * @date 2025/04/03 15:33
 */
@Data
@ConfigurationProperties("vei.payment")
public class VeiPaymentProperties {

    @NestedConfigurationProperty
    private WechatPaymentProperties wechat = new WechatPaymentProperties();

    @NestedConfigurationProperty
    private BaiduPaymentProperties baidu = new BaiduPaymentProperties();

    @NestedConfigurationProperty
    private AliPaymentProperties alipay = new AliPaymentProperties();

    @NestedConfigurationProperty
    private AllInPaymentProperties allinpay = new AllInPaymentProperties();



}

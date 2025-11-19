package com.chargehub.thirdparty.config.alipay;

import com.alipay.api.AlipayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/3 10:42
 * @Project：chargehub
 * @Package：com.chargehub.payment.config
 * @Filename：AliPayConf
 */
@Slf4j
@Repository
public class AliPayConf {


    @Value("${alipay.serverUrl}")
    private String serverUrl;
    @Value("${alipay.appId}")
    private String appId;
    @Value("${alipay.privateKey}")
    private String privateKey;
    @Value("${alipay.alipayPublicKey}")
    private String alipayPublicKey;
    @Value("${alipay.signType}")
    private String signType;
    @Value("${alipay.charset}")
    private String charset;
    @Value("${alipay.format}")
    private String format;




    private static AlipayConfig alipayConfig;


    /**
     * 初始化支付宝账户配置信息
     */
    @PostConstruct
    public void init() {
        log.info("【支付宝账户】配置开始**********************");
        alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(serverUrl);
        alipayConfig.setAppId(appId);
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat(format);
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset(charset);
        alipayConfig.setSignType(signType);
        log.info("【支付宝账户】配置完成**********************");
    }



    /**
     * 获取支付宝账户配置
     * @return
     */
    public static AlipayConfig getAlipayConfig() {

        return alipayConfig;
    }
}

package com.chargehub.thirdparty.config.invoice;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 16:57
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.config.invoice
 * @Filename：InvoiceConfigProperties
 */
@Data
@Component
@ConfigurationProperties(prefix = "invoice.xindiantu")
public class InvoiceConfigProperties {

    private String apiUrl;
    private String qyId;
    private String sksbh;
    private String fplxdm;
    private String kplx;
    private String tspz;
    private String zsfs;
    private String qdbz;
    private String xsfMc;
    private String xsfNsrsbh;
    private String xsfDzdh;
    private String xsfYhzh;
    private String kpr;
    private String sqr;
    private String dlzh;
    private String gmfzrrbs;

    private DetailParam detailParam;


    @Data
    public static class DetailParam {
        private String fphxz;
        private String hsbz;
        private String spmc;
        private String spbm;
        private String slv;
        private String lslbs;
        private String yhzcbs;
        private String dw;

//        private String dj;
//        private String sl;
//        private String je;
    }



}

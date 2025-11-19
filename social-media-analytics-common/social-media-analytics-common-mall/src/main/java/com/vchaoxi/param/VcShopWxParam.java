package com.vchaoxi.param;


import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
public class VcShopWxParam {

    @NotNull(groups = {EditMpInfo.class,EditMchInfo.class})
    private Integer shopId;


    /**
     * 公众号商户信息
     */
    @NotEmpty(groups = {EditMpInfo.class})
    private String mpAppId; //公众号appId
    @NotEmpty(groups = {EditMpInfo.class})
    private String mpMainBody;  //公众号主体
    @NotEmpty(groups = {EditMpInfo.class})
    private String mpName;   //公众号名称
    @NotEmpty(groups = {EditMpInfo.class})
    private String mpQr;    //公众号二维码


    @NotEmpty(groups = {EditMchInfo.class})
    private String mchId;   //商户号
    @NotEmpty(groups = {EditMchInfo.class})
    private String mchKey;   //商户号秘钥
    @NotEmpty(groups = {EditMchInfo.class})
    private String mchCertName;   //商户号安全证书名称




    public interface EditMpInfo { }
    public interface EditMchInfo { }

}
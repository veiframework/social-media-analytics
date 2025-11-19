package com.vchaoxi.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商铺相关 - 商铺微信配置表
 * </p>
 *
 * @author wangjiangtao
 * @since 2023-06-14
 */
@Data
public class VcShopWxVo implements Serializable {

    private Integer id;

    /**
     * 商铺id
     */
    private Integer shopId;

    /**
     * 商家公众号appid
     */
    private String mpAppid;

    /**
     * 商家公众号appSecret
     */
    private String mpAppSecret;

    /**
     * 公众号token
     */
    private String mpToken;

    /**
     * 公众号aesKey
     */
    private String mpAesKey;

    /**
     * 商家公众号二维码
     */
    private String mpQr;

    /**
     * 商家公众号主体
     */
    private String mpMainBody;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户号秘钥
     */
    private String mchKey;

    /**
     * 商户号证书名称
     */
    private String mchCertName;

    /**
     * 小程序appId
     */
    private String maAppid;

    /**
     * 小程序秘钥
     */
    private String maSecret;

    /**
     * 小程序名称
     */
    private String maName;

    /**
     * 小程序二维码
     */
    private String maQr;

    /**
     * 公众号名称
     */
    private String mpName;

    /**
     * 是否允许商铺自定义公众号商户信息    1允许  0不允许
     */
    private Integer custom;

    /**
     * 当前商家开启独立部署后是否进行修改  1是  0否
     */
    private Integer customEdit;

    /**
     * 添加时间
     */
    private LocalDateTime insertTime;
}

package com.vchaoxi.vo;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 商铺相关 - 商铺表
 * </p>
 *
 * @author wangjiangtao
 * @since 2023-06-14
 */
@Data
public class VcShopShopVo {


    private Integer id;

    /**
     * 代理商id
     */
    private Integer agentId;

    /**
     * 商家名称
     */
    private String name;

    /**
     * 商家logo
     */
    private String logo;

    /**
     * 商家品牌名称
     */
    private String brand;

    /**
     * 商家二维码
     */
    private String qrCode;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 营业时间
     */
    private String businessHours;

    /**
     * 商家客服电话
     */
    private String servicePhone;

    /**
     * 客户推广员id
     */
    private Integer promoterId;

    /**
     * 添加时间
     */
    private LocalDateTime insertTime;

    /**
     * 是否允许商铺自定义公众号商户信息    1允许  0不允许
     */
    private Integer custom;

    /**
     * 当前商家开启独立部署后是否进行修改  1是  0否
     */
    private Integer customEdit;

    /**
     * 用户手机号
     */
    private String adminMobile;

    /**
     * 用户名
     */
    private String adminName;

}

package com.vchaoxi.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WinesGoodsSpecificationVo {
    private static final long serialVersionUID = 1L;
    private Integer id;

    /**
     * 商品表的商品ID
     */
    private Integer goodsId;

    /**
     * 商品规格名称
     */
    private String specification;

    /**
     * 商品规格值
     */
    private String value;

    /**
     * 商品规格图片
     */
    private String picUrl;

    /**
     * 商品价格
     */
    private BigDecimal amount;

    /**
     * 会员价格
     */
    private BigDecimal memAmount;

    /**
     * vip价格
     */
    private BigDecimal vipAmount;


}

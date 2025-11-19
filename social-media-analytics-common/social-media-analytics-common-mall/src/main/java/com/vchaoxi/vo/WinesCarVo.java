package com.vchaoxi.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WinesCarVo {
    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 用户表的用户ID
     */
    private Integer userId;

    /**
     * 商品表的商品ID
     */
    private Integer goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品货品的价格
     */
    private BigDecimal price;

    /**
     * 商品货品的数量
     */
    private Integer number;

    /**
     * 购物车中商品是否选择状态
     */
    private Integer checked;

    /**
     * 商品图片或者商品货品图片
     */
    private String picUrl;

    /**
     * 规格的id
     */
    private Integer specificationId;

    /**
     * 规格
     */
    private String specifications;

    private String group;
}

package com.vchaoxi.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpecificationParam {
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
     *   商品标准价格
     */
    private BigDecimal amount;

    /**
     * 商品会员价格
     */
    private BigDecimal memAmount;

    /**
     * 商品vip价格
     */
    private BigDecimal vipAmount;

}

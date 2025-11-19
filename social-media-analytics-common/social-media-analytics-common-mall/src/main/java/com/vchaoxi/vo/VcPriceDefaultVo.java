package com.vchaoxi.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VcPriceDefaultVo {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 区间开始
     */
    private Integer lowerLimit;

    /**
     * 区间结束
     */
    private Integer upperLimit;

    /**
     * 1:分钟，2:小时，3:日，4:月
     */
    private Integer timeUnit;

    /**
     * 商品价格
     */
    private BigDecimal price;

}

package com.vchaoxi.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesVolumeStatisticsVo {
//    //哪一年的
//    private String yearKey;
//    //周期的key
//    private String periodKey;
    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品编号
     */
    private String goodsNo;

    /**
     * 销售金额
     */
    private BigDecimal amount;

    /**
     * 销售数量
     */
    private Integer goodsNum;
}

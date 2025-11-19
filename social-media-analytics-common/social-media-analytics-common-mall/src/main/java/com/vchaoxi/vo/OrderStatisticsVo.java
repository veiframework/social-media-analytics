package com.vchaoxi.vo;

import lombok.Data;

@Data
public class OrderStatisticsVo {
    //哪一年的
    private String yearKey;
    //周期的key
    private String periodKey;
    //周期的值
    private Integer periodValue;
}

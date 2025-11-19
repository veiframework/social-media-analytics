package com.vchaoxi.vo;

import lombok.Data;

import java.util.List;

/**
 * 宅急送的物流信息
 */

@Data
public class ZjsLogisticsInfoVo {
    /**
     * 客户标识
     */
    private String clientFlag;

    private String description;

    private List<ZjsLogisticsOrderVo> orders;
}

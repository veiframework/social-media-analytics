package com.vchaoxi.vo;

import lombok.Data;

import java.util.List;

@Data
public class ZjsLogisticsOrderVo {
    private String mailNo;
    private String orderNo;
    private String mailStatus;
    private String statusTime;
    private List<ZjsLogisticsStepsVo> steps;
}

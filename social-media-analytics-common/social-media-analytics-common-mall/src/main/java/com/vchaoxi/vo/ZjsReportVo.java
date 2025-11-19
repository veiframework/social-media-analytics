package com.vchaoxi.vo;

import lombok.Data;

/**
 * 宅急送创建出库订单的返回
 */

@Data
public class ZjsReportVo {
    private String code;
    private String message;
    private String status;
    private String describe;
    private String body;

}

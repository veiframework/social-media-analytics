package com.chargehub.common.security.template.event;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/07/16 18:30
 */
@Data
public class ChargeExportStatusEvent implements Serializable {

    private static final long serialVersionUID = -2653341884573080046L;

    private Integer status;

    private String id;

    private String failedReason;

    private double spendTime;

    private String fileName;

    public ChargeExportStatusEvent(Integer status, String id, double spendTime, String fileName) {
        this.status = status;
        this.id = id;
        this.spendTime = spendTime;
        this.fileName = fileName;
    }

    public ChargeExportStatusEvent(Integer status, String id, String failedReason, double spendTime) {
        this.status = status;
        this.id = id;
        this.failedReason = failedReason;
        this.spendTime = spendTime;
    }
}

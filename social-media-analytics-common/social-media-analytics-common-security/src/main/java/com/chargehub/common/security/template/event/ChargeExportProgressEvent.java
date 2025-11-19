package com.chargehub.common.security.template.event;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/07/17 18:23
 */
@Data
public class ChargeExportProgressEvent implements Serializable {

    private static final long serialVersionUID = -8653341884573080046L;
    private String id;

    private Integer totalNum;

    private Integer currentNum;

    private double spendTime;

    public ChargeExportProgressEvent(String id, int totalNum, int currentNum, double spendTime) {
        this.id = id;
        this.totalNum = totalNum;
        this.currentNum = currentNum;
        this.spendTime = spendTime;
    }
}

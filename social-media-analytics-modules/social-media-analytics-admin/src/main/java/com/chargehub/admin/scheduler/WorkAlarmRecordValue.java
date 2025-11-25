package com.chargehub.admin.scheduler;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class WorkAlarmRecordValue implements Serializable {
    private static final long serialVersionUID = 1468787052384548668L;

    private Integer value;

    private Integer count;

    public WorkAlarmRecordValue(Integer value, Integer count) {
        this.value = value;
        this.count = count;
    }




}

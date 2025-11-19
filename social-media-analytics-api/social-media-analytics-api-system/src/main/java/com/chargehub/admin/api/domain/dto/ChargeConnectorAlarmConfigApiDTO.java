package com.chargehub.admin.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * charge_connector_alarm_config
 * date: 2024-10-29
 *
 * @author TiAmo@13721682347@163.com
 */
@Data
public class ChargeConnectorAlarmConfigApiDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;

    private String connectorType;

    private String alarmType;

    private String code;

    private String reason;

    private Boolean enable;

    private Integer delFlag;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}

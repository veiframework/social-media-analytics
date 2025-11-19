package com.chargehub.biz.sys.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * charge_connector_alarm_config
 * date: 2024-10-29
 *
 * @author TiAmo@13721682347@163.com
 */
@Data
@Schema(description = "charge_connector_alarm_config")
@TableName("charge_connector_alarm_config")
public class ChargeConnectorAlarmConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(name = "主键")
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @NotNull
    @Schema(description = "充电接口类型 dict[connector_protocol_type]: YKC SLX GH")
    private String connectorType;

    @NotNull
    @Schema(description = "告警配置类型 dict[connector_alarm_type]: real_time, charge_end_reason")
    private String alarmType;

    @NotNull
    @Schema(description = "编码")
    private String code;

    @NotNull
    @Schema(description = "原因")
    private String reason;

    @Schema(description = "开/关")
    private Boolean enable;

    @TableLogic
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

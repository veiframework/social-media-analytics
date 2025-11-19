package com.chargehub.biz.sys.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * ChargeConnectorAlarmRecord
 * date: 2024-10-29
 *
 * @author TiAmo@13721682347@163.com
 */
@Data
@Schema(description = "charge_connector_alarm_record")
@TableName("charge_connector_alarm_record")
public class ChargeConnectorAlarmRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(name = "主键")
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @NotNull
    private String provinceId;

    @NotNull
    private String cityId;

    @NotNull
    private String countyId;

    @Schema(description = "区域ID")
    private String regionId;

    @NotNull
    private Long stationId;

    @Excel(name = "gunCode")
    @NotNull
    private String gunCode;

    @Excel(name = "pileCode")
    @NotNull
    private String pileCode;

    @Excel(name = "告警类型")
    @NotNull
    @Schema(description = "告警配置类型 dict[connector_alarm_type]: real_time, charge_end_reason")
    private String alarmType;

    @Excel(name = "上报类型")
    @NotNull
    @Schema(description = "告警配置类型 dict[connector_alarm_report_type]: 上报类型 connector_alarm、plat_alarm")
    private String reportType;

    @Excel(name = "故障状态")
    @NotNull
    @Schema(description = "告警配置类型 dict[connector_alarm_status] 0,10")
    private Integer status;

    @Excel(name = "开始时间")
    @Schema(description = "开始时间")
    private Date startTime;

    @Excel(name = "结束时间")
    @Schema(description = "结束时间")
    private Date endTime;

    @Schema(description = "原因编码")
    private String code;

    @Schema(description = "原因")
    private String reason;

    @Schema(description = "元数据")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject metadata;

    @Schema(description = "hlht connectorID")
    private String connectorId;

    @Schema(description = "转告警工单单号")
    private String woNo;

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

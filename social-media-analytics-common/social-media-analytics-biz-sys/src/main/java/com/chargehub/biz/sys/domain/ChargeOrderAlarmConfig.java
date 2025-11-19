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
 * ChargeOrderAlarmConfig
 * date: 2024/11/06
 *
 * @author TiAmo@13721682347@163.com
 */
@Data
@Schema(description = "ChargeOrderAlarmConfig")
@TableName("charge_order_alarm_config")
public class ChargeOrderAlarmConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(name = "主键")
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @NotNull
    @Schema(description = "告警配置类型 dict[connector_alarm_type]: real_time, charge_end_reason")
    private String alarmType;

    @NotNull
    @Schema(description = "触发类型 once, period")
    private String triggerType;

    @NotNull
    @Schema(description = "周期[天]")
    private Integer periodTime;

    @NotNull
    @Schema(description = "周期最小值")
    private Integer periodMin;

    @NotNull
    @Schema(description = "触发阈值[百分比]")
    private Double threshold;

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

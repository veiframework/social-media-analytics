package com.chargehub.admin.alarm.dto;

import com.chargehub.admin.alarm.domain.SocialMediaWorkAlarm;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class SocialMediaWorkAlarmDto implements Serializable, Z9CrudDto<SocialMediaWorkAlarm> {

    private static final long serialVersionUID = -4514754746573576446L;

    private Integer rowNum;

    private String errorMsg;


    private String id;

    @ApiModelProperty("关键字")
    private String keyword;

    @ApiModelProperty("关键字的值")
    private String keywordValue;

    @NotBlank
    @ApiModelProperty("告警类型  interval-间隔")
    private String type;

    @NotBlank
    @ApiModelProperty("告警名称")
    private String alarmName;

    @NotBlank
    @ApiModelProperty("告警字段")
    private String alarmField;

    @NotBlank
    @ApiModelProperty("告警表达式")
    private String alarmExpression;

    @ApiModelProperty("初始间隔")
    private Integer startInterval;

    @ApiModelProperty("消息字段")
    private List<String> msgFields = new ArrayList<>();

    @NotBlank
    @ApiModelProperty("消息模板")
    private String msgTemplate;

    @NotBlank
    @ApiModelProperty("cron表达式")
    private String cronExpression;

    @NotBlank
    @ApiModelProperty("状态")
    private String state;
}

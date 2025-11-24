package com.chargehub.admin.alarm.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@TableName(autoResultMap = true)
@Data
public class SocialMediaWorkAlarm implements Serializable, Z9CrudEntity {


    private static final long serialVersionUID = -4437286276140198318L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("告警类型  interval-间隔")
    private String type;

    @ApiModelProperty("告警名称")
    private String alarmName;

    @ApiModelProperty("告警字段")
    private String alarmField;

    @ApiModelProperty("告警表达式")
    private String alarmExpression;

    @ApiModelProperty("初始间隔")
    private Integer startInterval;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty("消息字段")
    private List<String> msgFields;

    @ApiModelProperty("消息模板")
    private String msgTemplate;

    @ApiModelProperty("cron表达式")
    private String cronExpression;

    @ApiModelProperty("状态")
    private String state;

    @ApiModelProperty("关键字")
    private String keyword;

    @ApiModelProperty("关键字的值")
    private String keywordValue;

    private Date createTime;

    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @Override
    public String getUniqueId() {
        return this.id;
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }

}

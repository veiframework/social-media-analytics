package com.chargehub.admin.alarm.vo;

import com.chargehub.common.security.annotation.Dict;
import com.chargehub.common.security.template.vo.Z9CrudVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class SocialMediaWorkAlarmVo implements Z9CrudVo, Serializable {
    private static final long serialVersionUID = -9009827698704152563L;


    private String id;

    @ApiModelProperty("告警类型  interval-间隔")
    private String type;


    @ApiModelProperty("关键字")
    private String keyword;

    @ApiModelProperty("关键字的值")
    private String keywordValue;

    @ApiModelProperty("告警名称")
    private String alarmName;

    @ApiModelProperty("告警字段")
    private String alarmField;

    @ApiModelProperty("告警表达式")
    private String alarmExpression;

    @ApiModelProperty("初始间隔")
    private Integer startInterval;

    @ApiModelProperty("消息字段")
    private List<String> msgFields;

    @ApiModelProperty("消息模板")
    private String msgTemplate;

    @ApiModelProperty("cron表达式")
    private String cronExpression;

    @ApiModelProperty("状态")
    private String state;

    private Date createTime;

    private Date updateTime;

    @Dict(dictTable = "sys_user", nameColumn = "nick_name", valueColumn = "user_id")
    private String creator;

    @Dict(dictTable = "sys_user", nameColumn = "nick_name", valueColumn = "user_id")
    private String updater;


}

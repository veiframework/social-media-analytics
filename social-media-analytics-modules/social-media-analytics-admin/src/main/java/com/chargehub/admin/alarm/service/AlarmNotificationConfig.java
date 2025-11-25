package com.chargehub.admin.alarm.service;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class AlarmNotificationConfig implements Serializable {
    private static final long serialVersionUID = 959219362968837131L;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("webhook")
    private String webhook;

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("消息字段")
    private List<String> msgFields;

    @ApiModelProperty("消息模板")
    private String msgTemplate;
}
package com.chargehub.admin.alarm.dto;

import com.chargehub.admin.alarm.domain.AlarmWebhook;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class AlarmWebhookDto implements Serializable, Z9CrudDto<AlarmWebhook> {
    private static final long serialVersionUID = 5945022801151790294L;

    private Integer rowNum;

    private String errorMsg;


    private String id;

    @NotBlank
    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("用户id")
    private String userId;

    @NotBlank
    @ApiModelProperty("webhook")
    private String webhook;

    @ApiModelProperty("token")
    private String token;

    @NotBlank
    @ApiModelProperty("是否禁用")
    private String disabled;


}

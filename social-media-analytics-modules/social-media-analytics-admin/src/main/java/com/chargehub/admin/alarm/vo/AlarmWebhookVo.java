package com.chargehub.admin.alarm.vo;

import com.chargehub.common.security.template.vo.Z9CrudVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class AlarmWebhookVo implements Serializable, Z9CrudVo {
    private static final long serialVersionUID = 3008801578096131322L;

    private String id;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("webhook")
    private String webhook;

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("是否禁用")
    private String disabled;

}

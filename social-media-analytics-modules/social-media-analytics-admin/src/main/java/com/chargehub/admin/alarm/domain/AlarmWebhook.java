package com.chargehub.admin.alarm.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class AlarmWebhook implements Serializable, Z9CrudEntity {

    private static final long serialVersionUID = -4103867640187221325L;

    @TableId
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


    @Override
    public String getUniqueId() {
        return this.id;
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }
}

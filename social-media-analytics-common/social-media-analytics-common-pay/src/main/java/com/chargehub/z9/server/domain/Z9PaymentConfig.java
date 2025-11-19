package com.chargehub.z9.server.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2025/05/19 16:33
 */
@TableName(value = "Z9_PAYMENT_CONFIG", autoResultMap = true)
@Data
public class Z9PaymentConfig implements Serializable, Z9CrudEntity {
    private static final long serialVersionUID = 8288570666612170280L;

    @TableId(value = "Z9_ID", type = IdType.ASSIGN_ID)
    private String id;

    @Schema(description = "租户id")
    @TableField(value = "Z9_TENANT_ID", fill = FieldFill.INSERT)
    private String tenantId;

    @TableField("Z9_PAYMENT_CHANNEL")
    @Schema(description = "支付渠道")
    private Integer paymentChannel;

    @Schema(description = "是否支持代理支付")
    @TableField("Z9_PROXY_PAYMENT")
    private Boolean proxyPayment;

    @Schema(description = "父级id,开启代理模式时有用")
    @TableField("Z9_PARENT_ID")
    private String parentId;

    @TableField(value = "Z9_CONTENT", typeHandler = JacksonTypeHandler.class)
    private ObjectNode content;

    @Schema(description = "创建者")
    @TableField(value = "Z9_CREATOR", fill = FieldFill.INSERT)
    private String creator;

    @Schema(description = "更新者")
    @TableField(value = "Z9_UPDATER", fill = FieldFill.INSERT_UPDATE)
    private String updater;


    @Schema(description = "创建时间")
    @TableField(value = "Z9_INSERT_TIME", fill = FieldFill.INSERT)
    private Date insertTime;

    @Schema(description = "修改时间")
    @TableField(value = "Z9_UPDATE_TIME", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Override
    public String getUniqueId() {
        return id;
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }
}

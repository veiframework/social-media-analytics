package com.chargehub.z9.server.vo;

import com.chargehub.common.security.template.vo.Z9CrudVo;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2025/05/19 17:15
 */
@Data
public class Z9PaymentConfigVo implements Serializable, Z9CrudVo {
    private static final long serialVersionUID = 7572126218367366777L;

    private String id;

    @Schema(description = "租户id")
    private String tenantId;

    @Schema(description = "支付渠道")
    private Integer paymentChannel;

    @Schema(description = "支付配置")
    private ObjectNode content;


    @Schema(description = "是否支持代理支付")
    private Boolean proxyPayment;

    @Schema(description = "父级id,开启代理模式时有用")
    private String parentId;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "更新者")
    private String updater;

    @Schema(description = "创建时间")
    private Date insertTime;

    @Schema(description = "修改时间")
    private Date updateTime;

}

package com.chargehub.z9.server.dto;

import com.chargehub.common.security.template.dto.Z9CrudDto;
import com.chargehub.z9.server.domain.Z9PaymentConfig;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/05/19 17:10
 */
@Data
public class Z9PaymentConfigDto implements Serializable, Z9CrudDto<Z9PaymentConfig> {
    private static final long serialVersionUID = 846913374383202491L;


    private String id;

    @NotNull
    @Schema(description = "支付渠道")
    private Integer paymentChannel;

    @Schema(description = "是否支持代理支付")
    private boolean proxyPayment;

    @Schema(description = "父级id,开启代理模式时有用")
    private String parentId;

    @NotBlank
    @Schema(description = "配置内容")
    private ObjectNode content;

    private Integer rowNum;

    private String errorMsg;


}

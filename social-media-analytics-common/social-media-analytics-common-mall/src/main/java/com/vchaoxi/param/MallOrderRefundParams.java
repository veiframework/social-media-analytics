package com.vchaoxi.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 09:49
 */
@Data
public class MallOrderRefundParams implements Serializable {


    private static final long serialVersionUID = 9158985920294182146L;

    @NotBlank
    @ApiModelProperty("订单id")
    private String orderId;

    @NotBlank
    @ApiModelProperty("退款原因")
    private String refundReason;

}

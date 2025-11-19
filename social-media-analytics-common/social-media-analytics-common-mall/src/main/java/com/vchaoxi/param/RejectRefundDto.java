package com.vchaoxi.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/08/16 13:38
 */
@Data
public class RejectRefundDto implements Serializable {
    private static final long serialVersionUID = -2931389746547901019L;

    @NotBlank
    private String orderId;

    @NotBlank
    private String rejectReason;


}

package com.vchaoxi.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 11:16
 */
@Data
public class CommissionStatusDto implements Serializable {
    private static final long serialVersionUID = 2364399941415292444L;

    @NotBlank
    private String id;


    @NotBlank
    @ApiModelProperty("状态- received到账, waiting- 即将到账")
    private String status;


}

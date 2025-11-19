package com.vchaoxi.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 14:46
 */
@Data
public class CommissionRateConfigDto implements Serializable {
    private static final long serialVersionUID = 1464650613505909125L;

    @NotNull
    @ApiModelProperty("提成比例")
    private BigDecimal commissionRate;


}

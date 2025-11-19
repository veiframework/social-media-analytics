package com.vchaoxi.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CommissionRateParam {
    /**
     * 设置提成比率
     */
    @NotNull
    private BigDecimal commissionRate;
}

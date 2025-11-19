package com.vchaoxi.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 11:20
 */
@Data
public class CommissionWithdrawDto implements Serializable {
    private static final long serialVersionUID = 7206957675967021791L;

    @NotNull
    private BigDecimal amount;


    private String loginId;



}

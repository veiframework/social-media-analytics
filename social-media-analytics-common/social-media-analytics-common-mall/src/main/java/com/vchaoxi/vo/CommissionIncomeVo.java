package com.vchaoxi.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Zhanghaowei
 * @date 2025/08/27 18:44
 */
@Data
public class CommissionIncomeVo {

    private BigDecimal totalIncome;

    private BigDecimal yesterdayIncome;

    private BigDecimal totalCommissionIncome;

    private String loginId;
}

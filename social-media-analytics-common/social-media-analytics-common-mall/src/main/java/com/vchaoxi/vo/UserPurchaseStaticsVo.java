package com.vchaoxi.vo;

import lombok.Data;

import java.math.BigDecimal;

//用户购买统计
@Data
public class UserPurchaseStaticsVo {
    /**
     * 下单数量
     */
    private Integer orderNum;

    /**
     * 下单总额
     */
    private BigDecimal totalOrderAmount;

    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 用户昵称
     */
    private String userNick;

    /**
     * 用户手机号
     */
    private String userPhone;
}

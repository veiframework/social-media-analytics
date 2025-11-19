package com.vchaoxi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单相关 - 订单支付记录表
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@Data
public class OrderPayRecordVo implements Serializable {

    private Integer id;

    /**
     * 代理商id
     */
    private Integer agentId;

    /**
     * 客户id
     */
    private Integer shopId;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 支付记录类型  1下单
     */
    private Integer recordType;

    /**
     * 支付方式   1微信支付
     */
    private Integer payWay;

    /**
     * 支付状态  0待支付   1已支付   2已完成   3已退款
     */
    private Integer status;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 商品数量
     */
    private Integer goodsNum;

    /**
     * 备注信息
     */
    private String remarks;

    /**
     * 支付时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /**
     * 添加时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertTime;
}

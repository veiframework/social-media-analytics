package com.vchaoxi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 订单相关 - 订单信息
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@Data
public class OrderVo implements Serializable {

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
     * 订单单号
     */
    private String orderNo;


    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 会员等级（1:普通用户 2:会员 3:VIP，酒品售卖中使用）
     */
    private Integer memGrade;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 物流公司发货状态0（默认）1:已收到发货通知处理中 2：已发货
     */
    private Integer deliveryStatus;

    /**
     * 订单总金额
     */
    private BigDecimal amount;


    /**
     * 订单支付时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /**
     * 订单付款方式 1:微信支付 2:线下支付
     */
    private Integer payWay;

    /**
     * 发票的id
     */
    private Integer invoiceId;


    /**
     * 快递单号
     */
    private String trackingNo;



    @ApiModelProperty("取件时间")
    private String pickTimeRange;

    /**
     * 发货时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryTime;

    /**
     * 收货时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receivingTime;

    /**
     * 收货人地址id
     */
    private Integer receiverAddressId;

    /**
     * 添加时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertTime;


    /**
     * 订单详情id
     */
    private List<OrderInfoVo> orderInfoVos;

    /**
     * 收货信息
     */
    private WinesReceiverAddressVo addressVo;

    /**
     * 购买人信息
     */
    private UserVo userVo;

    /**
     * 发票信息
     */
    private InvoiceVo invoiceVo;

    /**
     * 驳回原因
     */
    private String rejectReason;

    @ApiModelProperty("运费")
    private BigDecimal freight;

    /**
     * 该订单是否进行物流揽件  0无  1有
     */
    private Integer pickingLogistics;

    /**
     * 该订单是否进行物流配送  0无  1有
     */
    private Integer deliveryLogistics;

    /**
     * 订单类型  1.柜子订单 2.门店订单 3.预约上门 4.校园订单； 当location_type=0时 该值取值范围为1-3； location_type=1时 该字段取值为4
     */
    private Integer orderType;

    @ApiModelProperty("退款原因")
    private String refundReason;


    @ApiModelProperty("取件快递单号")
    private String pickTrackingNo;
}

package com.vchaoxi.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单相关 - 订单信息
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@TableName("vc_order_order")
public class VcOrderOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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
     * 物流公司发货状态0（默认）1:物流发货中 2：已发货
     */
    private Integer deliveryStatus;

    /**
     * 订单总金额
     */
    private BigDecimal amount;

    /**
     * 运费
     */
    private BigDecimal freight;

    /**
     * 订单付款方式 1:微信支付 2:线下支付
     */
    private Integer payWay;

    /**
     * 发票的id
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer invoiceId;

    /**
     * 订单支付时间
     */
    private LocalDateTime payTime;

    /**
     * 快递单号
     */
    private String trackingNo;

    @ApiModelProperty("取件时间")
    private String pickTimeRange;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 收货时间
     */
    private LocalDateTime receivingTime;

    /**
     * 收货人地址id
     */
    private Integer receiverAddressId;

    /**
     * 添加时间
     */
    private LocalDateTime insertTime;

    /**
     * 最后修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    private Integer isDelete;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 驳回原因
     */
    private String rejectReason;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMemGrade() {
        return memGrade;
    }

    public void setMemGrade(Integer memGrade) {
        this.memGrade = memGrade;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(Integer deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public Integer getPayWay() {
        return payWay;
    }

    public void setPayWay(Integer payWay) {
        this.payWay = payWay;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public LocalDateTime getReceivingTime() {
        return receivingTime;
    }

    public void setReceivingTime(LocalDateTime receivingTime) {
        this.receivingTime = receivingTime;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public Integer getReceiverAddressId() {
        return receiverAddressId;
    }

    public void setReceiverAddressId(Integer receiverAddressId) {
        this.receiverAddressId = receiverAddressId;
    }


    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public LocalDateTime getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public Integer getPickingLogistics() {
        return pickingLogistics;
    }

    public void setPickingLogistics(Integer pickingLogistics) {
        this.pickingLogistics = pickingLogistics;
    }

    public Integer getDeliveryLogistics() {
        return deliveryLogistics;
    }

    public void setDeliveryLogistics(Integer deliveryLogistics) {
        this.deliveryLogistics = deliveryLogistics;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getPickTimeRange() {
        return pickTimeRange;
    }

    public void setPickTimeRange(String pickTimeRange) {
        this.pickTimeRange = pickTimeRange;
    }

    public String getPickTrackingNo() {
        return pickTrackingNo;
    }

    public void setPickTrackingNo(String pickTrackingNo) {
        this.pickTrackingNo = pickTrackingNo;
    }

    @Override
    public String toString() {
        return "VcOrderOrder{" +
                "id=" + id +
                ", agentId=" + agentId +
                ", shopId=" + shopId +
                ", orderNo='" + orderNo + '\'' +
                ", userId=" + userId +
                ", memGrade=" + memGrade +
                ", status=" + status +
                ", deliveryStatus=" + deliveryStatus +
                ", amount=" + amount +
                ", freight=" + freight +
                ", payWay=" + payWay +
                ", invoiceId=" + invoiceId +
                ", payTime=" + payTime +
                ", trackingNo='" + trackingNo + '\'' +
                ", pickTimeRange='" + pickTimeRange + '\'' +
                ", deliveryTime=" + deliveryTime +
                ", receivingTime=" + receivingTime +
                ", receiverAddressId=" + receiverAddressId +
                ", insertTime=" + insertTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                ", deleteTime=" + deleteTime +
                ", rejectReason='" + rejectReason + '\'' +
                ", pickingLogistics=" + pickingLogistics +
                ", deliveryLogistics=" + deliveryLogistics +
                ", orderType=" + orderType +
                ", refundReason='" + refundReason + '\'' +
                ", pickTrackingNo='" + pickTrackingNo + '\'' +
                '}';
    }

}

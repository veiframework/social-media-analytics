package com.vchaoxi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * <p>
 * 订单相关 - 订单信息
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@TableName("vc_order_info")
public class VcOrderInfo implements Serializable {

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
     * 订单id
     */
    private Integer orderId;

    /**
     * 订单单号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Integer userId;


    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品编号
     */
    private String goodsNo;

    /**
     * 商品规格的id
     */
    private Integer specificationId;

    /**
     * 规格
     */
    private String specifications;

    /**
     * 商品主图
     */
    private String goodsImg;

    /**
     * 商品类型id
     */
    private Integer goodsTypeId;

    /**
     * 商品类型名称
     */
    private String goodsTypeName;

    /**
     * 订单状态
     */
    private Integer status;


    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 商品数量
     */
    private Integer goodsNum;

    /**
     * 商品单价
     */
    private BigDecimal unitPrice;
    /**
     * 订单支付时间
     */
    private LocalDateTime payTime;

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

    @ApiModelProperty("用户上传图片")
    private String userImages;



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
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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
    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getSpecificationId() {
        return specificationId;
    }

    public void setSpecificationId(Integer specificationId) {
        this.specificationId = specificationId;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }
    public Integer getGoodsTypeId() {
        return goodsTypeId;
    }

    public void setGoodsTypeId(Integer goodsTypeId) {
        this.goodsTypeId = goodsTypeId;
    }
    public String getGoodsTypeName() {
        return goodsTypeName;
    }

    public void setGoodsTypeName(String goodsTypeName) {
        this.goodsTypeName = goodsTypeName;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
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

    public String getUserImages() {
        return userImages;
    }

    public void setUserImages(String userImages) {
        this.userImages = userImages;
    }


    @Override
    public String toString() {
        return "VcOrderInfo{" +
                "id=" + id +
                ", agentId=" + agentId +
                ", shopId=" + shopId +
                ", orderId=" + orderId +
                ", orderNo='" + orderNo + '\'' +
                ", userId=" + userId +
                ", goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", goodsNo='" + goodsNo + '\'' +
                ", specificationId=" + specificationId +
                ", specifications='" + specifications + '\'' +
                ", goodsImg='" + goodsImg + '\'' +
                ", goodsTypeId=" + goodsTypeId +
                ", goodsTypeName='" + goodsTypeName + '\'' +
                ", status=" + status +
                ", amount=" + amount +
                ", goodsNum=" + goodsNum +
                ", unitPrice=" + unitPrice +
                ", payTime=" + payTime +
                ", insertTime=" + insertTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                ", deleteTime=" + deleteTime +
                ", userImages='" + userImages + '\'' +
                '}';
    }
}

package com.vchaoxi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 酒类售卖商品规格表
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-07-29
 */
@TableName("wines_goods_specification")
public class WinesGoodsSpecification implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商品表的商品ID
     */
    private Integer goodsId;

    /**
     * 商品规格名称
     */
    private String specification;

    /**
     * 商品规格值
     */
    private String value;

    /**
     * 商品规格图片
     */
    private String picUrl;

    /**
     * 商品价格
     */
    private BigDecimal amount;

    /**
     * 会员价格
     */
    private BigDecimal memAmount;

    /**
     * vip价格
     */
    private BigDecimal vipAmount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除
     */
    private Integer isDelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }
    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getMemAmount() {
        return memAmount;
    }

    public void setMemAmount(BigDecimal memAmount) {
        this.memAmount = memAmount;
    }

    public BigDecimal getVipAmount() {
        return vipAmount;
    }

    public void setVipAmount(BigDecimal vipAmount) {
        this.vipAmount = vipAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "WinesGoodsSpecification{" +
            "id=" + id +
            ", goodsId=" + goodsId +
            ", specification=" + specification +
            ", value=" + value +
            ", picUrl=" + picUrl +
                ", amount=" + amount +
                ", memAmount=" + memAmount +
                ", vipAmount=" + vipAmount +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            ", isDelete=" + isDelete +
        "}";
    }
}

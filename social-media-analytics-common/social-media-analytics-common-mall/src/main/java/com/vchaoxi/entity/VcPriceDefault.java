package com.vchaoxi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-07-17
 */
@TableName("vc_price_default")
public class VcPriceDefault implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 区间开始
     */
    private Integer lowerLimit;

    /**
     * 区间结束
     */
    private Integer upperLimit;

    /**
     * 1:分钟，2:小时，3:日，4:月
     */
    private Integer timeUnit;

    /**
     * 商品价格
     */
    private BigDecimal price;

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
    public Integer getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Integer lowerLimit) {
        this.lowerLimit = lowerLimit;
    }
    public Integer getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
    }
    public Integer getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(Integer timeUnit) {
        this.timeUnit = timeUnit;
    }
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    @Override
    public String toString() {
        return "VcPriceDefault{" +
            "id=" + id +
            ", goodsId=" + goodsId +
            ", lowerLimit=" + lowerLimit +
            ", upperLimit=" + upperLimit +
            ", timeUnit=" + timeUnit +
            ", price=" + price +
            ", insertTime=" + insertTime +
            ", updateTime=" + updateTime +
            ", isDelete=" + isDelete +
            ", deleteTime=" + deleteTime +
        "}";
    }
}

package com.vchaoxi.logistic.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-31
 */
@TableName("vc_logistics_delivery")
public class VcLogisticsDelivery implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 快递公司 ID
     */
    private String deliveryId;

    /**
     * 快递公司名称
     */
    private String deliveryName;

    /**
     * 是否支持散单, 1表示支持
     */
    private Integer canUseCash;

    /**
     * 是否支持查询面单余额, 1表示支持
     */
    private Integer canGetQuota;

    /**
     * 支持的服务类型
     */
    private String serviceType;

    /**
     * 散单对应的bizid，当can_use_cash=1时有效
     */
    private String cashBizId;

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
    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }
    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }
    public Integer getCanUseCash() {
        return canUseCash;
    }

    public void setCanUseCash(Integer canUseCash) {
        this.canUseCash = canUseCash;
    }
    public Integer getCanGetQuota() {
        return canGetQuota;
    }

    public void setCanGetQuota(Integer canGetQuota) {
        this.canGetQuota = canGetQuota;
    }
    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    public String getCashBizId() {
        return cashBizId;
    }

    public void setCashBizId(String cashBizId) {
        this.cashBizId = cashBizId;
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
        return "VcLogisticsDelivery{" +
            "id=" + id +
            ", deliveryId=" + deliveryId +
            ", deliveryName=" + deliveryName +
            ", canUseCash=" + canUseCash +
            ", canGetQuota=" + canGetQuota +
            ", serviceType=" + serviceType +
            ", cashBizId=" + cashBizId +
            ", insertTime=" + insertTime +
            ", updateTime=" + updateTime +
            ", isDelete=" + isDelete +
            ", deleteTime=" + deleteTime +
        "}";
    }
}

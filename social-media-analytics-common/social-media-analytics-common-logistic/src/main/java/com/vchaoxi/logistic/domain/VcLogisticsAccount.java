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
 * @since 2024-01-30
 */
@TableName("vc_logistics_account")
public class VcLogisticsAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 代理商id
     */
    private Integer agentId;

    /**
     * 商家id
     */
    private Integer shopId;

    /**
     * 快递公司编号
     */
    private String deliveryId;

    /**
     * 快递公司名称
     */
    private String deliveryName;

    /**
     * 快递公司客户编码
     */
    private String bizId;

    /**
     * 快递公司客户密码
     */
    private String password;

    /**
     * 备注内容（提交EMS审核需要） 格式要求： 电话：xxxxx 联系人：xxxxx 服务类型：xxxxx 发货地址：xxxx

     */
    private String remarkContent;

    /**
     * 绑定状态  1绑定成功  0解除绑定
     */
    private Integer status;

    /**
     * 电子面单余额
     */
    private Integer quotaNum;

    /**
     * 电子面单余额更新时间
     */
    private LocalDateTime quotaUpdateTime;

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
    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getRemarkContent() {
        return remarkContent;
    }

    public void setRemarkContent(String remarkContent) {
        this.remarkContent = remarkContent;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getQuotaNum() {
        return quotaNum;
    }

    public void setQuotaNum(Integer quotaNum) {
        this.quotaNum = quotaNum;
    }
    public LocalDateTime getQuotaUpdateTime() {
        return quotaUpdateTime;
    }

    public void setQuotaUpdateTime(LocalDateTime quotaUpdateTime) {
        this.quotaUpdateTime = quotaUpdateTime;
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
        return "VcLogisticsAccount{" +
            "id=" + id +
            ", agentId=" + agentId +
            ", shopId=" + shopId +
            ", deliveryId=" + deliveryId +
            ", deliveryName=" + deliveryName +
            ", bizId=" + bizId +
            ", password=" + password +
            ", remarkContent=" + remarkContent +
            ", status=" + status +
            ", quotaNum=" + quotaNum +
            ", quotaUpdateTime=" + quotaUpdateTime +
            ", insertTime=" + insertTime +
            ", updateTime=" + updateTime +
            ", isDelete=" + isDelete +
            ", deleteTime=" + deleteTime +
        "}";
    }
}

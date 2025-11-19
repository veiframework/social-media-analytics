package com.vchaoxi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商铺相关 - 商铺表
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@TableName("vc_shop_shop")
public class VcShopShop implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 代理商id
     */
    private Integer agentId;

    /**
     * 商家名称
     */
    private String name;

    /**
     * 商家logo
     */
    private String logo;

    /**
     * 商家品牌名称
     */
    private String brand;

    /**
     * 商家二维码
     */
    private String qrCode;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 营业时间
     */
    private String businessHours;

    /**
     * 商家客服电话
     */
    private String servicePhone;

    /**
     * 客户推广员id
     */
    private Integer promoterId;

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


    private Integer openLogistics;


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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }
    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }
    public String getServicePhone() {
        return servicePhone;
    }

    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }
    public Integer getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(Integer promoterId) {
        this.promoterId = promoterId;
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

    public Integer getOpenLogistics() {
        return openLogistics;
    }

    public void setOpenLogistics(Integer openLogistics) {
        this.openLogistics = openLogistics;
    }

    @Override
    public String toString() {
        return "VcShopShop{" +
            "id=" + id +
            ", agentId=" + agentId +
            ", name=" + name +
            ", logo=" + logo +
            ", brand=" + brand +
            ", qrCode=" + qrCode +
            ", contacts=" + contacts +
            ", businessHours=" + businessHours +
            ", servicePhone=" + servicePhone +
            ", promoterId=" + promoterId +
            ", insertTime=" + insertTime +
            ", updateTime=" + updateTime +
            ", isDelete=" + isDelete +
            ", deleteTime=" + deleteTime +
            ", openLogistics=" +openLogistics +
        "}";
    }
}

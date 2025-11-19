package com.vchaoxi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 通用配置信息字段表
 * </p>
 *
 * @author hanfuxian
 * @since 2024-08-05
 */
@TableName("vc_agent_general_config")
public class AgentGeneralConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 线下核销地址
     */
    private String offlineRedemptionAddress;
    /**
     * 线下核销联系人
     */
    private String offlineRedemptionContact;
    /**
     * 线下核销联系方式
     */
    private String offlineRedemptionPhone;

    /**
     * 添加时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

    public String getOfflineRedemptionAddress() {
        return offlineRedemptionAddress;
    }

    public void setOfflineRedemptionAddress(String offlineRedemptionAddress) {
        this.offlineRedemptionAddress = offlineRedemptionAddress;
    }

    public String getOfflineRedemptionContact() {
        return offlineRedemptionContact;
    }

    public void setOfflineRedemptionContact(String offlineRedemptionContact) {
        this.offlineRedemptionContact = offlineRedemptionContact;
    }

    public String getOfflineRedemptionPhone() {
        return offlineRedemptionPhone;
    }

    public void setOfflineRedemptionPhone(String offlineRedemptionPhone) {
        this.offlineRedemptionPhone = offlineRedemptionPhone;
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
        return "AgentGeneralConfig{" +
                "id=" + id +
                ", offlineRedemptionAddress='" + offlineRedemptionAddress + '\'' +
                ", offlineRedemptionContact='" + offlineRedemptionContact + '\'' +
                ", offlineRedemptionPhone='" + offlineRedemptionPhone + '\'' +
                ", insertTime=" + insertTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                ", deleteTime=" + deleteTime +
                '}';
    }
}

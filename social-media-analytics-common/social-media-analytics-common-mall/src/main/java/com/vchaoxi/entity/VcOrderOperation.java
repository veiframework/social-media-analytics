package com.vchaoxi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单相关 - 订单操作记录表
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@TableName("vc_order_operation")
public class VcOrderOperation implements Serializable {

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
     * 柜子商品记录id
     */
    private Integer lockerGoodsId;

    /**
     * 操作类型
     * 101 管理员存放 102管理员回收
     * 201 用户开箱
     */
    private Integer operateType;

    /**
     * 操作平台
     */
    private Integer plantform;

    /**
     * 操作用户类型
     */
    private Integer userType;

    /**
     * 操作的用户id
     */
    private Integer userId;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 操作状态  0未知  1成功 2失败 
     */
    private Integer status;

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

    public Integer getLockerGoodsId() {
        return lockerGoodsId;
    }

    public void setLockerGoodsId(Integer lockerGoodsId) {
        this.lockerGoodsId = lockerGoodsId;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Integer getPlantform() {
        return plantform;
    }

    public void setPlantform(Integer plantform) {
        this.plantform = plantform;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
        return "VcOrderOperation{" +
                "id=" + id +
                ", agentId=" + agentId +
                ", shopId=" + shopId +
                ", lockerGoodsId=" + lockerGoodsId +
                ", operateType=" + operateType +
                ", plantform=" + plantform +
                ", userType=" + userType +
                ", userId=" + userId +
                ", ip='" + ip + '\'' +
                ", status=" + status +
                ", insertTime=" + insertTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                ", deleteTime=" + deleteTime +
                '}';
    }
}

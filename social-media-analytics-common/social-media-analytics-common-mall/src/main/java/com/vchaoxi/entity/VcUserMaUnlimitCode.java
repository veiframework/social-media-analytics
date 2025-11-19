package com.vchaoxi.entity;

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
 * @author hanfuxian
 * @since 2024-10-22
 */
@TableName("vc_user_ma_unlimit_code")
public class VcUserMaUnlimitCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 小程序id
     */
    private String maAppid;

    private String scene;

    private String page;

    private Integer checkPath;

    /**
     * 环境
     */
    private String envVersion;

    /**
     * url地址
     */
    private String url;

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
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getMaAppid() {
        return maAppid;
    }

    public void setMaAppid(String maAppid) {
        this.maAppid = maAppid;
    }
    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }
    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
    public Integer getCheckPath() {
        return checkPath;
    }

    public void setCheckPath(Integer checkPath) {
        this.checkPath = checkPath;
    }
    public String getEnvVersion() {
        return envVersion;
    }

    public void setEnvVersion(String envVersion) {
        this.envVersion = envVersion;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        return "VcUserMaUnlimitCode{" +
            "id=" + id +
            ", userId=" + userId +
            ", maAppid=" + maAppid +
            ", scene=" + scene +
            ", page=" + page +
            ", checkPath=" + checkPath +
            ", envVersion=" + envVersion +
            ", url=" + url +
            ", insertTime=" + insertTime +
            ", updateTime=" + updateTime +
            ", isDelete=" + isDelete +
            ", deleteTime=" + deleteTime +
        "}";
    }
}

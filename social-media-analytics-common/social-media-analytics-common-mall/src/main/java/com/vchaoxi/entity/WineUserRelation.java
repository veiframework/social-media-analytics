package com.vchaoxi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 酒品销售用户关系表
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-07-29
 */
@TableName("wine_user_relation")
public class WineUserRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 父用户id
     */
    private Integer parentUserId;

    /**
     * 子用户id
     */
    private Integer childUserId;

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
    public Integer getParentUserId() {
        return parentUserId;
    }

    public void setParentUserId(Integer parentUserId) {
        this.parentUserId = parentUserId;
    }
    public Integer getChildUserId() {
        return childUserId;
    }

    public void setChildUserId(Integer childUserId) {
        this.childUserId = childUserId;
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
        return "WineUserRelation{" +
            "id=" + id +
            ", parentUserId=" + parentUserId +
            ", childUserId=" + childUserId +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            ", isDelete=" + isDelete +
        "}";
    }
}

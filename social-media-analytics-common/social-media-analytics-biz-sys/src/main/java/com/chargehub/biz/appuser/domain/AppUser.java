package com.chargehub.biz.appuser.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 * 
 * @author system
 * @since 2024-03-21
 */
@Data
@TableName("app_user")
public class AppUser implements Z9CrudEntity, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 微信unionid
     */
    private String unionid;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer delFlag;

    @ApiModelProperty("积分")
    private Integer points;

    @Override
    public String getUniqueId() {
        return this.id;
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }
}
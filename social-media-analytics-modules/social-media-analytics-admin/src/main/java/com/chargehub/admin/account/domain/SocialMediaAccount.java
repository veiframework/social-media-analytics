package com.chargehub.admin.account.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.chargehub.common.security.template.annotation.CrudSubUniqueId;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class SocialMediaAccount implements Serializable, Z9CrudEntity {

    private static final long serialVersionUID = 7975531864356203684L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("租户id")
    private String tenantId;

    @ApiModelProperty("平台id")
    private String platformId;

    @ApiModelProperty("第三方用户id")
    private String uid;

    @CrudSubUniqueId
    @ApiModelProperty("第三方用户加密id")
    private String secUid;

    @ApiModelProperty("第三方用户昵称")
    private String nickname;

    @ApiModelProperty("accessToken")
    private String accessToken;

    @ApiModelProperty("refreshToken")
    private String refreshToken;

    @ApiModelProperty("过期时间")
    private Date expireTime;

    @ApiModelProperty("第三方openId")
    private String openId;

    private Date createTime;

    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    private Integer deleted;


    @Override
    public String getUniqueId() {
        return this.id;
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }
}

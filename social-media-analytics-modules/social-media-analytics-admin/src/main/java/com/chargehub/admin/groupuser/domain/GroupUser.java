package com.chargehub.admin.groupuser.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class GroupUser implements Serializable, Z9CrudEntity {
    private static final long serialVersionUID = -8339898094201936450L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("父用户id")
    private String parentUserId;

    @Override
    public String getUniqueId() {
        return id;
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }
}

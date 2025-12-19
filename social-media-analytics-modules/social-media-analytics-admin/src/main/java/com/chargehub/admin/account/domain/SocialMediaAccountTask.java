package com.chargehub.admin.account.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class SocialMediaAccountTask implements Serializable, Z9CrudEntity {

    private static final long serialVersionUID = 5317952125228078260L;

    @TableId(type = IdType.ASSIGN_ID)
    private String accountId;

    private String platformId;

    private Date createTime;

    @TableField(fill = FieldFill.INSERT)
    private String creator;


    @Override
    public String getUniqueId() {
        return accountId;
    }

    @Override
    public void setUniqueId(String id) {
        this.accountId = id;
    }
}

package com.chargehub.admin.work.domain;

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
public class SocialMediaWorkTask implements Serializable, Z9CrudEntity {

    private static final long serialVersionUID = 7541877574998269384L;


    @TableId(type = IdType.ASSIGN_ID)
    private String workId;

    private String platformId;

    private Date createTime;

    @TableField(fill = FieldFill.INSERT)
    private String creator;


    @Override
    public String getUniqueId() {
        return this.workId;
    }

    @Override
    public void setUniqueId(String id) {
        this.workId = id;
    }

}

package com.chargehub.admin.work.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class SocialMediaWorkPriority implements Serializable, Z9CrudEntity {

    private static final long serialVersionUID = -3607618293506726930L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("优先级")
    private Integer priority;

    @ApiModelProperty("正常采集间隔")
    private Integer normalInterval;

    @ApiModelProperty("堆积时采集间隔")
    private Integer backlogInterval;

    @ApiModelProperty("表达式")
    private String priorityExpression;

    private Date createTime;

    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;


    @Override
    public String getUniqueId() {
        return this.getId();
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }
}

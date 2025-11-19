package com.chargehub.biz.agreement.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2025/08/08 16:26
 */
@Data
public class Agreement implements Z9CrudEntity {

    @TableId
    private String id;


    @ApiModelProperty(value = "协议类型")
    private String type;


    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Override
    public String getUniqueId() {
        return id;
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }
}

package com.vchaoxi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 15:47
 */
@TableName("vc_member_open_option")
@Data
public class MemberOpenOption implements Z9CrudEntity {


    @TableId(type = IdType.AUTO)
    private Integer id;


    @ApiModelProperty("会员开通选项类型 0.连续包月  1.单次月卡  2.单次季卡  3.单次年卡")
    private Integer type;


    @ApiModelProperty("会员开通选项类型名称")
    private String typeName;


    @ApiModelProperty("月数 -1表示连续无限制")
    private Integer months;


    @ApiModelProperty("天数")
    private Integer days;


    @ApiModelProperty("原价")
    private BigDecimal originalPrice;


    @ApiModelProperty("售价")
    private BigDecimal price;


    @ApiModelProperty("选项状态  1上架   0下架")
    private Integer status;


    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @ApiModelProperty("修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    @ApiModelProperty("删除标志（0代表存在 1代表删除）")
    @TableLogic
    private Integer delFlag;

    @Override
    public String getUniqueId() {
        return String.valueOf(id);
    }

    @Override
    public void setUniqueId(String id) {
        this.id = Integer.parseInt(id);
    }
}

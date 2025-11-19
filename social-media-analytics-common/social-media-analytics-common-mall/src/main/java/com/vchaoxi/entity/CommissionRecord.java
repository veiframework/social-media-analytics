package com.vchaoxi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Zhanghaowei
 * @date 2025/08/27 17:54
 */
@Data
@TableName(value = "vc_commission_record", autoResultMap = true)
public class CommissionRecord implements Serializable, Z9CrudEntity {
    private static final long serialVersionUID = -1940824888917267272L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("登录人id")
    private String loginId;

    @ApiModelProperty("来源用户的id")
    private String sourceLoginId;

    @ApiModelProperty("类型: commission-提成, withdraw-提现")
    private String type;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("金额")
    private BigDecimal amount;


    @ApiModelProperty("状态- received到账, waiting- 即将到账")
    private String status;

    @ApiModelProperty("订单id")
    private String orderId;


    @ApiModelProperty("商品信息")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> goodsInfo;

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

    @Override
    public String getUniqueId() {
        return getId();
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }
}

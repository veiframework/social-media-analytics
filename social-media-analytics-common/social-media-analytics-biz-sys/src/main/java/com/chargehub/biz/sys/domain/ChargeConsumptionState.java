package com.chargehub.biz.sys.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Zhanghaowei
 * @date 2024/05/10 17:02
 */
@Data
@TableName("charg_consumption")
public class ChargeConsumptionState implements Serializable, Z9CrudEntity {

    private static final long serialVersionUID = 8173181523700871991L;


    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "是否开过发票：0.未开过  1.已开过")
    private Integer invoiceState;

    @ApiModelProperty(value = "发票编号")
    private String invoiceNum;

    @ApiModelProperty("互联互通订单编号")
    private String icnOrderId;


    @ApiModelProperty(value = "充电量")
    private BigDecimal chargeAmount;


    @ApiModelProperty(value = "充电金额")
    private BigDecimal chargeMoney;


    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "充电结束状态")
    private Integer endType;

    @ApiModelProperty("支付方式")
    private String payType;

    @ApiModelProperty(value = "实收服务费")
    private BigDecimal practicalServiceMoney;

    @ApiModelProperty(value = "实收总金额")
    private BigDecimal practicalMoney;

    @ApiModelProperty(value = "结算时间")
    private Integer orderState;

    @Override
    public String getUniqueId() {
        return String.valueOf(id);
    }

    @Override
    public void setUniqueId(String id) {
        this.setId(Integer.parseInt(id));
    }
}

package com.chargehub.biz.sys.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 发票管理
 * @Author: 马腾达
 * @Date: 2023-08-11
 * @Version: V1.0
 */
@Data
@TableName("charg_invoice")
@ApiModel(value = "charg_invoice对象", description = "发票管理")
public class ChargeInvoice implements Serializable, Z9CrudEntity {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;


    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "状态：0.未审核  1.已通过，2.未通过")
    private Integer invoiceState;


    @ApiModelProperty(value = "发票金额")
    private BigDecimal money;


    @ApiModelProperty(value = "发票充电费")
    private BigDecimal charge;


    @ApiModelProperty(value = "发票服务费")
    private BigDecimal service;


    @ApiModelProperty(value = "发票度数")
    private BigDecimal power;


    @ApiModelProperty(value = "发票编号")
    private String invoiceNum;

    @ApiModelProperty(value = "名称，必填")
    private String name;


    @ApiModelProperty(value = "纳税人识别号,必填")
    private String taxpayerNumber;


    @ApiModelProperty(value = "公司地址")
    private String address;


    @ApiModelProperty(value = "公司电话")
    private String companyTel;


    @ApiModelProperty(value = "开户银行")
    private String bank;


    @ApiModelProperty(value = "银行账号")
    private String bankNumber;


    @ApiModelProperty(value = "手机号")
    private String phone;


    @ApiModelProperty(value = "邮箱")
    private String email;


    @ApiModelProperty(value = "申请开票时间")
    private Date startTime;


    @ApiModelProperty(value = "审核通过时间")
    private Date endTime;


    @ApiModelProperty(value = "发票代码")
    private String fpdm;


    @ApiModelProperty(value = "发票号码")
    private String fphm;


    @ApiModelProperty(value = "pdf发票地址")
    private String pdfUrl;


    @ApiModelProperty(value = "驳回原因")
    private String causeRejection;


    @ApiModelProperty(value = "备注")
    private String remark;


    @TableLogic
    @ApiModelProperty(value = "删除标志（0代表存在 1代表删除）")
    private String delFlag;

    @ApiModelProperty(value = "代开发票订单数据")
    public String chargConsumptionIds;


    @ApiModelProperty(value = "发票类型")
    private Integer type;

    @ApiModelProperty(value = "发票来源0-内部,1-新电图")
    private Integer orderSource;

    @ApiModelProperty(value = "抬头类型：1：公司，2：个人")
    private Integer titleType;


    @ApiModelProperty(value = "发票类型,01-专票,02-普票")
    private String fplxdm;

    @ApiModelProperty(value = "创建者")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;


    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


    @ApiModelProperty(value = "更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;


    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    @Override
    public String getUniqueId() {
        return String.valueOf(getId());
    }

    @Override
    public void setUniqueId(String id) {
        setId(Integer.parseInt(id));
    }
}

package com.chargehub.biz.sys.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.*;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 发票抬头管理
 * @Author: 马腾达
 * @Date: 2023-08-14
 * @Version: V1.0
 */
@Data
@TableName("charg_invoice_title")
@ApiModel(value = "charg_invoice_title对象", description = "发票抬头管理")
public class ChargeInvoiceTitle implements Serializable, Z9CrudEntity {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @Excel(name = "个人/公司名称", width = 15)
    @ApiModelProperty(value = "个人/公司名称")
    private String name;


    @Excel(name = "纳税人识别号,必填", width = 15)
    @ApiModelProperty(value = "纳税人识别号,必填")
    private String taxpayerNumber;


    @Excel(name = "公司地址", width = 15)
    @ApiModelProperty(value = "公司地址")
    private String address;


    @Excel(name = "公司电话", width = 15)
    @ApiModelProperty(value = "公司电话")
    private String companyTel;


    @Excel(name = "开户银行", width = 15)
    @ApiModelProperty(value = "开户银行")
    private String bank;


    @Excel(name = "银行账号", width = 15)
    @ApiModelProperty(value = "银行账号")
    private String bankNumber;


    @Excel(name = "手机号", width = 15)
    @ApiModelProperty(value = "手机号")
    private String phone;


    @Excel(name = "邮箱", width = 15)
    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "抬头类型：1：公司，2：个人")
    private Integer titleType;

    @ApiModelProperty(value = "发票类型,01-专票,02-普票")
    private String fplxdm;

    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;


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

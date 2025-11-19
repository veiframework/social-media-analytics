package com.chargehub.biz.sys.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
public class EcGChargInvoiceDTO implements Serializable {


    private static final long serialVersionUID = -7815614011762674480L;

    @ApiModelProperty(value = "发票抬头")
    private String receiptTitle;
    @ApiModelProperty(value = "开票方式")
    private String receiptMethod;
    @ApiModelProperty(value = "抬头类型")
    private String receiveType;
    @ApiModelProperty(value = "纳税人识别号")
    private String taxpayerNumber;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "电话")
    private String phoneNumber;
    @ApiModelProperty(value = "开户行")
    private String bankName;
    @ApiModelProperty(value = "账户")
    private String bankAccount;
    @ApiModelProperty(value = "联系电话")
    private String receiveNumber;
    @ApiModelProperty(value = "可开票金额")
    private BigDecimal receiptAmount;

    @ApiModelProperty(value = "发票流水号")
    private String xdtInvoiceAppNo;

    private List<Order> orders;

    @ApiModelProperty(value = "发票编号")
    private String invoiceNum;

    @ApiModelProperty(value = "订单id")
    private String relateIds;

    @ApiModelProperty(value = "邮箱")
    private String email;

}
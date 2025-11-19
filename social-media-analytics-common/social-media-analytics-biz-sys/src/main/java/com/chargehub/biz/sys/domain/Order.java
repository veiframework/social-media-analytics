package com.chargehub.biz.sys.domain;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Order implements Serializable {

	private static final long serialVersionUID = -5683940505033388899L;

	@ApiModelProperty(value = "服务费")
	private BigDecimal serviceAmount;

	@ApiModelProperty(value = "充电费")
	private BigDecimal electricAmount;

	@ApiModelProperty(value = "orderCode")
	private String orderCode;

	@ApiModelProperty(value = "总金额")
	private  BigDecimal receiptApplyAmount;

}

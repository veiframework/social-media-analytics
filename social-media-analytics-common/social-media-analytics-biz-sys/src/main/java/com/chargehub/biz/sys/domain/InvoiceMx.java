package com.chargehub.biz.sys.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class InvoiceMx implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "单据行号")
	public String djhh;
	@ApiModelProperty(value = "商品代码")
	public String spdm;
	@ApiModelProperty(value = "商品名称")
	public String spmc;
	@ApiModelProperty(value = "规格型号")
	public String ggxh;
	@ApiModelProperty(value = "计量单位(成品油时，单位只能为升、吨)")
	public String jldw;
	@ApiModelProperty(value = "商品批次")
	public String sppc;
	@ApiModelProperty(value = "税率（必填）例:0.13")
	public String tax;
	@ApiModelProperty(value = "商品数量")
	public String spsl;
	@ApiModelProperty(value = "商品含税单价(元) (负数)")
	public String hsdj;
	@ApiModelProperty(value = "含税金额(元)（必填）")
	public String hsje;
	@ApiModelProperty(value = "商品不含税单价(元)")
	public String bhsdj;
	@ApiModelProperty(value = "不含税金额(元,保留两位)")
	public String bhsje;
	@ApiModelProperty(value = "税额(元,保留两位)")
	public String se;
	@ApiModelProperty(value = "商品分类(可选)")
	public String spfl;
	@ApiModelProperty(value = "税收编码(长度19位) (必填)")
	public String ssbm;
	@ApiModelProperty(value = "零税率标识（必填）")
	public String lslbs;
	@ApiModelProperty(value = "优惠政策标识 0-使用优惠政策  1-不使用(必填)")
	public String yhzcbs;
	@ApiModelProperty(value = "增值税特殊管理")
	public String zzstsgl;
	@ApiModelProperty(value = "商品折扣金额(单据折扣或明细折扣只能选择一种)")
	public String zkje;

}

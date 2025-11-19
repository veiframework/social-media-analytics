package com.vchaoxi.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class InvoiceParam {
    /**
     * 订单Id
     */
    @NotNull(groups = {Add.class, Edit.class, Del.class})
    private Integer orderId;

    /**
     * 发票编号
     */
    @NotEmpty(groups = {Add.class})
    private String invoiceCode;

    /**
     * 发票抬头
     */
    private String invoiceTitle;

    /**
     * 纳税人识别号
     */
    private String tin;

    /**
     * 地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 发票金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 开票时间
     */
    private String invoiceTime;

    /**
     * 付款单位
     */
    private String payer;

    /**
     * 发票的图片地址
     */
    private String invoiceUrl;

    public interface Add {}
    public interface Edit {}
    public interface Del {}
}

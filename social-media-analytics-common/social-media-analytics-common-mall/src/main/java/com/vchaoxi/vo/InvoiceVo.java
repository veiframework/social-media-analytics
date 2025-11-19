package com.vchaoxi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvoiceVo {
    private Integer id;

    /**
     * 发票编号
     */
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
     * 商品名称
     */
    private String googsName;

    /**
     * 商品数量
     */
    private Integer goodsNum;

    /**
     * 商品金额
     */
    private BigDecimal goodsAmount;

    /**
     * 开票时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime invoiceTime;

    /**
     * 付款单位
     */
    private String payer;

    /**
     * 发票的图片地址
     */
    private String invoiceUrl;
}

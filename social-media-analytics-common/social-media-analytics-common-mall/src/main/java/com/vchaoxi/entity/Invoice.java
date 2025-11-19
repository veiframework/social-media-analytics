package com.vchaoxi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 发票管理
 * </p>
 *
 * @author hanfuxian
 * @since 2024-11-14
 */
@TableName("wine_invoice")
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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
    private LocalDateTime invoiceTime;

    /**
     * 付款单位
     */
    private String payer;

    /**
     * 发票的图片地址
     */
    private String invoiceUrl;

    /**
     * 添加时间
     */
    private LocalDateTime insertTime;

    /**
     * 最后修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    private Integer isDelete;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }
    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }
    public String getGoogsName() {
        return googsName;
    }

    public void setGoogsName(String googsName) {
        this.googsName = googsName;
    }
    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }
    public BigDecimal getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(BigDecimal goodsAmount) {
        this.goodsAmount = goodsAmount;
    }
    public LocalDateTime getInvoiceTime() {
        return invoiceTime;
    }

    public void setInvoiceTime(LocalDateTime invoiceTime) {
        this.invoiceTime = invoiceTime;
    }
    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getInvoiceUrl() {
        return invoiceUrl;
    }

    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }

    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
    public LocalDateTime getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        this.deleteTime = deleteTime;
    }

    @Override
    public String toString() {
        return "Invoice{" +
            "id=" + id +
            ", invoiceTitle=" + invoiceTitle +
            ", tin=" + tin +
            ", address=" + address +
            ", phone=" + phone +
            ", invoiceAmount=" + invoiceAmount +
            ", googsName=" + googsName +
            ", goodsNum=" + goodsNum +
            ", goodsAmount=" + goodsAmount +
            ", invoiceTime=" + invoiceTime +
            ", payer=" + payer +
                ", invoiceUrl=" + invoiceUrl +
            ", insertTime=" + insertTime +
            ", updateTime=" + updateTime +
            ", isDelete=" + isDelete +
            ", deleteTime=" + deleteTime +
        "}";
    }
}

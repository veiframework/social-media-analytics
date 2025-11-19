package com.vchaoxi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品相关 - 商品表
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@TableName("vc_goods_goods")
public class VcGoodsGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 代理商id
     */
    private Integer agentId;

    /**
     * 商铺id
     */
    private Integer shopId;

    /**
     * 商铺分类
     */
    private Integer type;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品编号
     */
    private String goodsNo;

    /**
     * 分类状态  1上架  0下架
     */
    private Integer status;

    /**
     * 商铺主图
     */
    private String img;

    /**
     * 商品宣传图片列表
     */
    private String gallery;

    /**
     * 商品价格
     */
    private BigDecimal amount;

    @ApiModelProperty("运费")
    private BigDecimal freight;

    /**
     * 会员价格
     */
    private BigDecimal memAmount;

    /**
     * vip价格
     */
    private BigDecimal vipAmount;

    /**
     * 商品销量
     */
    private Integer salesNumber;

    /**
     * 序号
     */
    private Integer serial;

    /**
     * 商品存放的箱格尺寸类型 多个尺寸英文逗号分割 为空表示支持全部箱格尺寸
     */
    private String boxSizes;

    /**
     * 商品服务单位  双 次 件等
     */
    private String unit;

    /**
     * 商品详情
     */
    private String details;

    /**
     * 商品简介
     */
    private String brief;

    /**
     * 是否设置成了banner
     */
    private Integer isBanner;
    /**
     * 是否促销 0正常  1促销
     */
    private Integer isPromotion;

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

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getMemAmount() {
        return memAmount;
    }

    public void setMemAmount(BigDecimal memAmount) {
        this.memAmount = memAmount;
    }

    public BigDecimal getVipAmount() {
        return vipAmount;
    }

    public void setVipAmount(BigDecimal vipAmount) {
        this.vipAmount = vipAmount;
    }

    public Integer getSalesNumber() {
        return salesNumber;
    }

    public void setSalesNumber(Integer salesNumber) {
        this.salesNumber = salesNumber;
    }

    public Integer getSerial() {
        return serial;
    }

    public void setSerial(Integer serial) {
        this.serial = serial;
    }

    public String getBoxSizes() {
        return boxSizes;
    }

    public void setBoxSizes(String boxSizes) {
        this.boxSizes = boxSizes;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public Integer getIsBanner() {
        return isBanner;
    }

    public void setIsBanner(Integer isBanner) {
        this.isBanner = isBanner;
    }

    public Integer getIsPromotion() {
        return isPromotion;
    }

    public void setIsPromotion(Integer isPromotion) {
        this.isPromotion = isPromotion;
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


    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    @Override
    public String toString() {
        return "VcGoodsGoods{" +
                "id=" + id +
                ", agentId=" + agentId +
                ", shopId=" + shopId +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", goodsNo='" + goodsNo + '\'' +
                ", status=" + status +
                ", img='" + img + '\'' +
                ", gallery='" + gallery + '\'' +
                ", amount=" + amount +
                ", memAmount=" + memAmount +
                ", vipAmount=" + vipAmount +
                ", salesNumber=" + salesNumber +
                ", serial=" + serial +
                ", boxSizes='" + boxSizes + '\'' +
                ", unit='" + unit + '\'' +
                ", details='" + details + '\'' +
                ", brief='" + brief + '\'' +
                ", isBanner=" + isBanner +
                ", isPromotion=" + isPromotion +
                ", insertTime=" + insertTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                ", deleteTime=" + deleteTime +
                ", freight=" + freight +
                '}';
    }
}

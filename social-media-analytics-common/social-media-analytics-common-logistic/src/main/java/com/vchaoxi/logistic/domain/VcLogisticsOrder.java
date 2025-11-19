package com.vchaoxi.logistic.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-31
 */
@TableName("vc_logistics_order")
public class VcLogisticsOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 代理商id
     */
    private Integer agentId;

    /**
     * 商家id
     */
    private Integer shopId;

    /**
     * 用户订单id
     */
    private Integer orderId;

    /**
     * 我方订单单号  对应微信orderId
     */
    private String orderNo;

    /**
     * 物流订单类型  1揽件订单  2配送订单
     */
    private Integer orderType;

    /**
     * 唯一订单编号  对应微信端orderId
     */
    private String wxOrderId;

    /**
     * 运单号
     */
    private String waybillId;

    /**
     * 用户openid，当add_source=2时无需填写（不发送物流服务通知）
     */
    private String openid;

    /**
     * 快递公司ID
     */
    private String deliveryId;

    /**
     * 快递公司名称
     */
    private String deliveryName;

    /**
     * 快递公司客户编码
     */
    private String bizId;

    /**
     * 快递备注信息，比如"易碎物品"，不超过1024字节
     */
    private String customRemark;

    /**
     * 订单标签id，用于平台型小程序区分平台上的入驻方，tagid须与入驻方账号一一对应，非平台型小程序无需填写该字段
     */
    private Integer tagid;

    /**
     * 订单来源，0为小程序订单，2为App或H5订单，填2则不发送物流服务通知
     */
    private Integer addSource;

    /**
     * App或H5的appid，add_source=2时必填，需和开通了物流助手的小程序绑定同一open账号
     */
    private String wxAppid;

    /**
     * 发件人姓名，不超过64字节
     */
    private String senderName;

    /**
     * 发件人座机号码，若不填写则必须填写 mobile，不超过32字节
     */
    private String senderTel;

    /**
     * 发件人手机号码，若不填写则必须填写 tel，不超过32字节
     */
    private String senderMobile;

    /**
     * 发件人公司名称，不超过64字节
     */
    private String senderCompany;

    /**
     * 发件人邮编，不超过10字节
     */
    private String senderPostCode;

    /**
     * 发件人国家，不超过64字节
     */
    private String senderCountry;

    /**
     * 发件人省份，比如："广东省"，不超过64字节
     */
    private String senderProvince;

    private Integer senderProvinceCode;

    /**
     * 发件人市/地区，比如："广州市"，不超过64字节
     */
    private String senderCity;

    private Integer senderCityCode;

    /**
     * 发件人区/县，比如："海珠区"，不超过64字节
     */
    private String senderArea;

    private Integer senderAreaCode;

    /**
     * 发件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节
     */
    private String senderAddress;

    /**
     * 发件人姓名，不超过64字节
     */
    private String receiverName;

    /**
     * 发件人座机号码，若不填写则必须填写 mobile，不超过32字节
     */
    private String receiverTel;

    /**
     * 发件人手机号码，若不填写则必须填写 tel，不超过32字节
     */
    private String receiverMobile;

    /**
     * 发件人公司名称，不超过64字节
     */
    private String receiverCompany;

    /**
     * 发件人邮编，不超过10字节
     */
    private String receiverPostCode;

    /**
     * 发件人国家，不超过64字节
     */
    private String receiverCountry;

    /**
     * 发件人省份，比如："广东省"，不超过64字节
     */
    private String receiverProvince;

    private Integer receiverProvinceCode;

    /**
     * 发件人市/地区，比如："广州市"，不超过64字节
     */
    private String receiverCity;

    private Integer receiverCityCode;

    /**
     * 发件人区/县，比如："海珠区"，不超过64字节
     */
    private String receiverArea;

    private Integer receiverAreaCode;

    /**
     * 发件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节
     */
    private String receiverAddress;

    /**
     * 包裹数量, 默认为1
     */
    private Integer cargoCount;

    /**
     * 货物总重量，比如1.2，单位是千克(kg)
     */
    private BigDecimal cargoWeight;

    /**
     * 货物长度，比如20.0，单位是厘米(cm)
     */
    private BigDecimal cargoSpaceX;

    /**
     * 货物宽度，比如15.0，单位是厘米(cm)
     */
    private BigDecimal cargoSpaceY;

    /**
     * 货物高度，比如10.0，单位是厘米(cm)
     */
    private BigDecimal cargoSpaceZ;

    /**
     *物品信息
     */
    private String cargoDetailList;

    /**
     * 商家小程序的路径，建议为订单页面
     */
    private String shopWxaPath;

    /**
     * 商品缩略图 url；shop.detail_list为空则必传，shop.detail_list非空可不传。
     */
    private String shopImgUrl;

    /**
     * 商品名称, 不超过128字节；shop.detail_list为空则必传，shop.detail_list非空可不传。
     */
    private String shopGoodsName;

    /**
     * 商品数量；shop.detail_list为空则必传。shop.detail_list非空可不传，默认取shop.detail_list的size
     */
    private Integer shopGoodsCount;

    /**
     * 商品详情列表，适配多商品场景，用以消息落地页展
     */
    private String shopDetailList;

    /**
     * 是否保价，0 表示不保价，1 表示保价
     */
    private Integer useInsured;

    /**
     * 保价金额，单位是分，比如: 10000 表示 100 元
     */
    private Integer insuredValue;

    /**
     * 服务类型ID
     */
    private Integer serviceType;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * Unix 时间戳, 单位秒，顺丰必须传。 预期的上门揽件时间，0表示已事先约定取件时间；否则请传预期揽件时间戳，需大于当前时间，收件员会在预期时间附近上门。例如expect_time为“1557989929”，表示希望收件员将在2019年05月16日14:58:49-15:58:49内上门取货。说明：若选择 了预期揽件时间，请不要自己打单，由上门揽件的时候打印。如果是下顺丰散单，则必传此字段，否则不会有收件员上门揽件。
     */
    private Integer expectTime;

    /**
     * 分单策略，【0：线下网点签约，1：总部签约结算】，不传默认线下网点签约。目前支持圆通。
     */
    private Integer takeMode;

    /**
     * 订单状态  1正常  0取消
     */
    private Integer status;

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
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    public String getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(String waybillId) {
        this.waybillId = waybillId;
    }
    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }
    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }
    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
    public String getCustomRemark() {
        return customRemark;
    }

    public void setCustomRemark(String customRemark) {
        this.customRemark = customRemark;
    }
    public Integer getTagid() {
        return tagid;
    }

    public void setTagid(Integer tagid) {
        this.tagid = tagid;
    }
    public Integer getAddSource() {
        return addSource;
    }

    public void setAddSource(Integer addSource) {
        this.addSource = addSource;
    }
    public String getWxAppid() {
        return wxAppid;
    }

    public void setWxAppid(String wxAppid) {
        this.wxAppid = wxAppid;
    }
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    public String getSenderTel() {
        return senderTel;
    }

    public void setSenderTel(String senderTel) {
        this.senderTel = senderTel;
    }
    public String getSenderMobile() {
        return senderMobile;
    }

    public void setSenderMobile(String senderMobile) {
        this.senderMobile = senderMobile;
    }
    public String getSenderCompany() {
        return senderCompany;
    }

    public void setSenderCompany(String senderCompany) {
        this.senderCompany = senderCompany;
    }
    public String getSenderPostCode() {
        return senderPostCode;
    }

    public void setSenderPostCode(String senderPostCode) {
        this.senderPostCode = senderPostCode;
    }
    public String getSenderCountry() {
        return senderCountry;
    }

    public void setSenderCountry(String senderCountry) {
        this.senderCountry = senderCountry;
    }
    public String getSenderProvince() {
        return senderProvince;
    }

    public void setSenderProvince(String senderProvince) {
        this.senderProvince = senderProvince;
    }
    public Integer getSenderProvinceCode() {
        return senderProvinceCode;
    }

    public void setSenderProvinceCode(Integer senderProvinceCode) {
        this.senderProvinceCode = senderProvinceCode;
    }
    public String getSenderCity() {
        return senderCity;
    }

    public void setSenderCity(String senderCity) {
        this.senderCity = senderCity;
    }
    public Integer getSenderCityCode() {
        return senderCityCode;
    }

    public void setSenderCityCode(Integer senderCityCode) {
        this.senderCityCode = senderCityCode;
    }
    public String getSenderArea() {
        return senderArea;
    }

    public void setSenderArea(String senderArea) {
        this.senderArea = senderArea;
    }
    public Integer getSenderAreaCode() {
        return senderAreaCode;
    }

    public void setSenderAreaCode(Integer senderAreaCode) {
        this.senderAreaCode = senderAreaCode;
    }
    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }
    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
    public String getReceiverTel() {
        return receiverTel;
    }

    public void setReceiverTel(String receiverTel) {
        this.receiverTel = receiverTel;
    }
    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }
    public String getReceiverCompany() {
        return receiverCompany;
    }

    public void setReceiverCompany(String receiverCompany) {
        this.receiverCompany = receiverCompany;
    }
    public String getReceiverPostCode() {
        return receiverPostCode;
    }

    public void setReceiverPostCode(String receiverPostCode) {
        this.receiverPostCode = receiverPostCode;
    }
    public String getReceiverCountry() {
        return receiverCountry;
    }

    public void setReceiverCountry(String receiverCountry) {
        this.receiverCountry = receiverCountry;
    }
    public String getReceiverProvince() {
        return receiverProvince;
    }

    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
    }
    public Integer getReceiverProvinceCode() {
        return receiverProvinceCode;
    }

    public void setReceiverProvinceCode(Integer receiverProvinceCode) {
        this.receiverProvinceCode = receiverProvinceCode;
    }
    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }
    public Integer getReceiverCityCode() {
        return receiverCityCode;
    }

    public void setReceiverCityCode(Integer receiverCityCode) {
        this.receiverCityCode = receiverCityCode;
    }
    public String getReceiverArea() {
        return receiverArea;
    }

    public void setReceiverArea(String receiverArea) {
        this.receiverArea = receiverArea;
    }
    public Integer getReceiverAreaCode() {
        return receiverAreaCode;
    }

    public void setReceiverAreaCode(Integer receiverAreaCode) {
        this.receiverAreaCode = receiverAreaCode;
    }
    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }
    public Integer getCargoCount() {
        return cargoCount;
    }

    public void setCargoCount(Integer cargoCount) {
        this.cargoCount = cargoCount;
    }
    public BigDecimal getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(BigDecimal cargoWeight) {
        this.cargoWeight = cargoWeight;
    }
    public BigDecimal getCargoSpaceX() {
        return cargoSpaceX;
    }

    public void setCargoSpaceX(BigDecimal cargoSpaceX) {
        this.cargoSpaceX = cargoSpaceX;
    }
    public BigDecimal getCargoSpaceY() {
        return cargoSpaceY;
    }

    public void setCargoSpaceY(BigDecimal cargoSpaceY) {
        this.cargoSpaceY = cargoSpaceY;
    }
    public BigDecimal getCargoSpaceZ() {
        return cargoSpaceZ;
    }

    public void setCargoSpaceZ(BigDecimal cargoSpaceZ) {
        this.cargoSpaceZ = cargoSpaceZ;
    }
    public String getShopWxaPath() {
        return shopWxaPath;
    }

    public void setShopWxaPath(String shopWxaPath) {
        this.shopWxaPath = shopWxaPath;
    }
    public String getShopImgUrl() {
        return shopImgUrl;
    }

    public void setShopImgUrl(String shopImgUrl) {
        this.shopImgUrl = shopImgUrl;
    }
    public String getShopGoodsName() {
        return shopGoodsName;
    }

    public void setShopGoodsName(String shopGoodsName) {
        this.shopGoodsName = shopGoodsName;
    }
    public Integer getShopGoodsCount() {
        return shopGoodsCount;
    }

    public void setShopGoodsCount(Integer shopGoodsCount) {
        this.shopGoodsCount = shopGoodsCount;
    }
    public String getShopDetailList() {
        return shopDetailList;
    }

    public void setShopDetailList(String shopDetailList) {
        this.shopDetailList = shopDetailList;
    }
    public Integer getUseInsured() {
        return useInsured;
    }

    public void setUseInsured(Integer useInsured) {
        this.useInsured = useInsured;
    }
    public Integer getInsuredValue() {
        return insuredValue;
    }

    public void setInsuredValue(Integer insuredValue) {
        this.insuredValue = insuredValue;
    }
    public Integer getServiceType() {
        return serviceType;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public Integer getExpectTime() {
        return expectTime;
    }

    public void setExpectTime(Integer expectTime) {
        this.expectTime = expectTime;
    }
    public Integer getTakeMode() {
        return takeMode;
    }

    public void setTakeMode(Integer takeMode) {
        this.takeMode = takeMode;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCargoDetailList() {
        return cargoDetailList;
    }

    public void setCargoDetailList(String cargoDetailList) {
        this.cargoDetailList = cargoDetailList;
    }

    public String getWxOrderId() {
        return wxOrderId;
    }

    public void setWxOrderId(String wxOrderId) {
        this.wxOrderId = wxOrderId;
    }

    @Override
    public String toString() {
        return "VcLogisticsOrder{" +
                "id=" + id +
                ", agentId=" + agentId +
                ", shopId=" + shopId +
                ", orderId=" + orderId +
                ", orderNo='" + orderNo + '\'' +
                ", orderType=" + orderType +
                ", wxOrderId='" + wxOrderId + '\'' +
                ", waybillId='" + waybillId + '\'' +
                ", openid='" + openid + '\'' +
                ", deliveryId='" + deliveryId + '\'' +
                ", deliveryName='" + deliveryName + '\'' +
                ", bizId='" + bizId + '\'' +
                ", customRemark='" + customRemark + '\'' +
                ", tagid=" + tagid +
                ", addSource=" + addSource +
                ", wxAppid='" + wxAppid + '\'' +
                ", senderName='" + senderName + '\'' +
                ", senderTel='" + senderTel + '\'' +
                ", senderMobile='" + senderMobile + '\'' +
                ", senderCompany='" + senderCompany + '\'' +
                ", senderPostCode='" + senderPostCode + '\'' +
                ", senderCountry='" + senderCountry + '\'' +
                ", senderProvince='" + senderProvince + '\'' +
                ", senderProvinceCode=" + senderProvinceCode +
                ", senderCity='" + senderCity + '\'' +
                ", senderCityCode=" + senderCityCode +
                ", senderArea='" + senderArea + '\'' +
                ", senderAreaCode=" + senderAreaCode +
                ", senderAddress='" + senderAddress + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", receiverTel='" + receiverTel + '\'' +
                ", receiverMobile='" + receiverMobile + '\'' +
                ", receiverCompany='" + receiverCompany + '\'' +
                ", receiverPostCode='" + receiverPostCode + '\'' +
                ", receiverCountry='" + receiverCountry + '\'' +
                ", receiverProvince='" + receiverProvince + '\'' +
                ", receiverProvinceCode=" + receiverProvinceCode +
                ", receiverCity='" + receiverCity + '\'' +
                ", receiverCityCode=" + receiverCityCode +
                ", receiverArea='" + receiverArea + '\'' +
                ", receiverAreaCode=" + receiverAreaCode +
                ", receiverAddress='" + receiverAddress + '\'' +
                ", cargoCount=" + cargoCount +
                ", cargoWeight=" + cargoWeight +
                ", cargoSpaceX=" + cargoSpaceX +
                ", cargoSpaceY=" + cargoSpaceY +
                ", cargoSpaceZ=" + cargoSpaceZ +
                ", cargoDetailList='" + cargoDetailList + '\'' +
                ", shopWxaPath='" + shopWxaPath + '\'' +
                ", shopImgUrl='" + shopImgUrl + '\'' +
                ", shopGoodsName='" + shopGoodsName + '\'' +
                ", shopGoodsCount=" + shopGoodsCount +
                ", shopDetailList='" + shopDetailList + '\'' +
                ", useInsured=" + useInsured +
                ", insuredValue=" + insuredValue +
                ", serviceType=" + serviceType +
                ", serviceName='" + serviceName + '\'' +
                ", expectTime=" + expectTime +
                ", takeMode=" + takeMode +
                ", status=" + status +
                ", insertTime=" + insertTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                ", deleteTime=" + deleteTime +
                '}';
    }
}

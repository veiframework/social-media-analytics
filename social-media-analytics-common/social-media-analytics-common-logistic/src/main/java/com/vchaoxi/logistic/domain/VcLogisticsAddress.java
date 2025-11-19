package com.vchaoxi.logistic.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-30
 */
@TableName("vc_logistics_address")
public class VcLogisticsAddress implements Serializable {

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
     * 发件人姓名，不超过64字节
     */
    private String name;

    /**
     * 发件人座机号码，若不填写则必须填写 mobile，不超过32字节
     */
    private String tel;

    /**
     * 发件人手机号码，若不填写则必须填写 tel，不超过32字节
     */
    private String mobile;

    /**
     * 发件人公司名称，不超过64字节
     */
    private String company;

    /**
     * 发件人邮编，不超过10字节
     */
    private String postCode;

    /**
     * 发件人国家，不超过64字节
     */
    private String country;

    /**
     * 发件人省份，比如："广东省"，不超过64字节
     */
    private String province;

    /**
     * 发件人省份编码，比如："440100"
     */
    private Integer provinceCode;

    /**
     * 发件人市/地区，比如："广州市"，不超过64字节
     */
    private String city;

    /**
     * 发件人市/地区编码，比如："440100"
     */
    private Integer cityCode;

    /**
     * 发件人区/县，比如："海珠区"，不超过64字节
     */
    private String area;

    /**
     * 发件人区/县编码，比如："440100"
     */
    private Integer areaCode;

    /**
     * 发件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节
     */
    private String address;

    /**
     * 是否为默认地址 1是 0否
     */
    private Integer isDefault;

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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
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

    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    public Integer getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(Integer areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public String toString() {
        return "VcLogisticsAddress{" +
                "id=" + id +
                ", agentId=" + agentId +
                ", shopId=" + shopId +
                ", name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", mobile='" + mobile + '\'' +
                ", company='" + company + '\'' +
                ", postCode='" + postCode + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", provinceCode=" + provinceCode +
                ", city='" + city + '\'' +
                ", cityCode=" + cityCode +
                ", area='" + area + '\'' +
                ", areaCode=" + areaCode +
                ", address='" + address + '\'' +
                ", isDefault=" + isDefault +
                ", insertTime=" + insertTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                ", deleteTime=" + deleteTime +
                '}';
    }
}

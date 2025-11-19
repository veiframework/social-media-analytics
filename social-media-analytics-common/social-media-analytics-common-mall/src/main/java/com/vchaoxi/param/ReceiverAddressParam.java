package com.vchaoxi.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ReceiverAddressParam {

    @NotNull(groups = {Del.class, SetDefault.class, Edit.class},message = "地址的id不能为空")
    private Integer id;

    /**
     * 收货人姓名
     */
    @NotEmpty(groups = {Add.class, Edit.class},message = "收货人姓名不能为空")
    private String userName;

    /**
     * 邮编
     */
    private String postalCode;

    /**
     * 第一级地址
     */
    @NotEmpty(groups = {Add.class, Edit.class},message = "省级名称不能为空")
    private String provinceName;

    /**
     * 第二级地址
     */
    @NotEmpty(groups = {Add.class, Edit.class},message = "市级名称不能为空")
    private String cityName;

    /**
     * 第三级地址
     */
    @NotEmpty(groups = {Add.class, Edit.class},message = "县级名称不能为空")
    private String countyName;

    /**
     * 第四级地址
     */
    private String streetName;

    /**
     * 详细信息
     */
    private String detailInfo;

    /**
     * 新选择器详细收货地址信息
     */
    private String detailInfoNew;

    /**
     * 收货人手机号码
     */
    @NotEmpty(groups = {Add.class, Edit.class},message = "收件人手机号不能为空")
    private String telNumber;

    /**
     * 是否为默认地址（0:否，1:是）
     */
    private Integer isDefault;

    @NotNull(groups = {Add.class, Edit.class},message = "省级id不能为空")
    private Integer provinceCode;

    @NotNull(groups = {Add.class, Edit.class},message = "市级id不能为空")
    private Integer cityCode;

    @NotNull(groups = {Add.class, Edit.class},message = "区级id不能为空")
    private Integer countyCode;

    public interface Add {}
    public interface Del{}
    public interface Edit{}
    public interface SetDefault{}
}

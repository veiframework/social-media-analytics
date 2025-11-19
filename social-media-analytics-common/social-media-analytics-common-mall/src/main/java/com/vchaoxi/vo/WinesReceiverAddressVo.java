package com.vchaoxi.vo;

import lombok.Data;

@Data
public class WinesReceiverAddressVo {
    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 收货人姓名
     */
    private String userName;

    /**
     * 邮编
     */
    private String postalCode;

    /**
     * 第一级地址
     */
    private String provinceName;

    /**
     * 第二级地址
     */
    private String cityName;

    /**
     * 第三级地址
     */
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
    private String telNumber;

    /**
     * 是否为默认地址（0:否，1:是）
     */
    private Integer isDefault;

    private Integer provinceCode;

    private Integer cityCode;

    private Integer countyCode;
}

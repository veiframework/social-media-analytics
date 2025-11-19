package com.vchaoxi.logistic.param;


import com.chargehub.common.core.utils.validation.customized.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 * <p>
 * 
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-30
 */
@Data
public class LogisticsAddressParam {


    @NotNull(groups = {Edit.class,Del.class})
    private Integer id;

    /**
     * 商家id
     */
    private Integer shopId;

    /**
     * 发件人姓名，不超过64字节
     */
    @NotEmpty(groups = {Add.class,Edit.class})
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
    @NotEmpty(groups = {Add.class,Edit.class})
    private String province;

    @NotNull(groups = {Add.class,Edit.class})
    private Integer provinceCode;

    /**
     * 发件人市/地区，比如："广州市"，不超过64字节
     */
    @NotEmpty(groups = {Add.class,Edit.class})
    private String city;

    @NotNull(groups = {Add.class,Edit.class})
    private Integer cityCode;

    /**
     * 发件人区/县，比如："海珠区"，不超过64字节
     */
    @NotEmpty(groups = {Add.class,Edit.class})
    private String area;

    @NotNull(groups = {Add.class,Edit.class})
    private Integer areaCode;

    /**
     * 发件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节
     */
    @NotEmpty(groups = {Add.class,Edit.class})
    private String address;

    /**
     * 是否为默认地址 1是 0否
     */
    @EnumValue(intValues = {0,1},groups = {Add.class,Edit.class})
    private Integer isDefault;



    public interface Add {}
    public interface Edit {}
    public interface Del {}
}

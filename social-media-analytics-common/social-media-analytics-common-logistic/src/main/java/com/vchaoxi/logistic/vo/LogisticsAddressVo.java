package com.vchaoxi.logistic.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

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
@Data
public class LogisticsAddressVo implements Serializable {


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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertTime;


    /**
     * 商家名称
     */
    private String shopName;

}

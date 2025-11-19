package com.chargehub.thirdparty.api.domain.dto;


import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 15:14
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.dto
 * @Filename：InvoiceDto
 */
@Data
public class InvoiceDto {


    /**
     * 名换
     */
    @NotEmpty
    private String name;

    /**
     * 公司地址
     */
    private String address;

    /**
     * 公司电话
     */
    private String companyTel;

    /**
     * 开户行
     */
    private String bank;

    /**
     * 银行账号
     */
    private String bankNumber;

    /**
     * 纳税人识别号
     */
//    @NotEmpty
    private String taxpayerNumber;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 发票金额
     */
    @NotNull
    private BigDecimal money;

    /**
     * 发票度数
     */
    @NotNull
    private BigDecimal power;

    /**
     * 发票编号
     */
    private String invoiceNum;

    private String email;

    /**
     * 发票类型
     */
    private String fplxdm;

}

package com.chargehub.thirdparty.api.domain.vo.invoice;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 18:15
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.vo.invoice
 * @Filename：InvoiceVo
 */
@Data
public class InvoiceVo {

    /**
     * 查询结果
     */
    private Boolean result;

    /**
     * 异常描述
     */
    private String errMsg;

    /**
     * 发票状态   1开票中  2已完成  -1异常状态
     */
    private Integer status;

    /**
     * 发票编号
     */
    private String invoiceNum;

    /**
     * 发票代码
     */
    private String invoiceCode;

    /**
     * 发票pdf地址
     */
    private String invoicePdfUrl;
}

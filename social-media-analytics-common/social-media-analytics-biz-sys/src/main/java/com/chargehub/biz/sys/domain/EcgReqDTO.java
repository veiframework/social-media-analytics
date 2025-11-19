package com.chargehub.biz.sys.domain;


import com.alibaba.fastjson2.JSONArray;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "新电途")
@Data
public class EcgReqDTO implements Serializable {


    private static final long serialVersionUID = 7504641507371833307L;
    /**
     * 结果代码
     */
    @Schema(description = "结果代码")
    @JsonProperty("BizCode")
    private String bizCode;

    /**
     * 发票流水号
     */
    @Schema(description = "结果代码描述")
    @JsonProperty("BizMsg")
    private String bizMsg;

    /**
     * 发票流水号
     */
    @Schema(description = "发票流水号")
    private String orderCodes;
    /*
     *  发票参数
     * */
    @Schema(description = "发票参数")
    private JSONArray invoices;
    /**
     * 开票数量
     */
    @Schema(description = "开票数量")
    private Integer invoiceNums;
    /**
     * 唯一标识
     */
    @Schema(description = "唯一标识")
    private Long receiptApplyld;
}

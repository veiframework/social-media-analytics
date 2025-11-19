package com.chargehub.thirdparty.api.domain.dto.divide;

import com.chargehub.common.core.utils.json.CustomBigDecimalForDivideSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;

@Data
@ToString
public class DivideReqDTO {

    @JsonProperty("devideConfigId")
    private Long divisionStationConfigId;

    @JsonProperty("stationId")
    private String stationId;

    // 电站名称
    @JsonProperty("stationName")
    private String stationName;

    // 电费
    @JsonProperty("elecAmount")
    @JsonSerialize(using = CustomBigDecimalForDivideSerializer.class)
    private BigDecimal electricityFee;

    // 服务费，订单中的服务费字段，月卡全部抵扣则该值为0
    @JsonProperty("serveAmount")
    @JsonSerialize(using = CustomBigDecimalForDivideSerializer.class)
    private BigDecimal serviceFee;

    // 月卡服务费（不含红包）指的是当时购买月卡时实际支付的金额,需扣除红包,计算的服务费
    @JsonProperty("monthlyAmount")
    @JsonSerialize(using = CustomBigDecimalForDivideSerializer.class)
    private BigDecimal monthCardServiceFee;

    //卫生费
    @JsonProperty("healthAmount")
    @JsonSerialize(using = CustomBigDecimalForDivideSerializer.class)
    private BigDecimal cleanFee;

    // 运营管理费
    @JsonProperty("operateAmount")
    @JsonSerialize(using = CustomBigDecimalForDivideSerializer.class)
    private BigDecimal operationManageFee;

    // 充电桩保险费
    @JsonProperty("stationSafeAmount")
    @JsonSerialize(using = CustomBigDecimalForDivideSerializer.class)
    private BigDecimal pileInsuranceFee;

    // 其他费用
    @JsonProperty("otherAmount")
    @JsonSerialize(using = CustomBigDecimalForDivideSerializer.class)
    private BigDecimal otherFee;

    // 电费分成比率 (0-1) 小数点后4位
    @JsonProperty("elecDevideRate")
    @JsonSerialize(using = CustomBigDecimalForDivideSerializer.class)
    private BigDecimal electricityDivisionRate;

    // 利润分成比率 (0-1) 小数点后4位
    @JsonProperty("profitDevideRate")
    @JsonSerialize(using = CustomBigDecimalForDivideSerializer.class)
    private BigDecimal profitDivisionRate;

    // 应清分金额
    @JsonProperty("shouldSortoutAmount")
    @JsonSerialize(using = CustomBigDecimalForDivideSerializer.class)
    private BigDecimal settlementAmount;

    // 清分时间区间
    @JsonProperty("settleTimeStart")
    private String settlementDateRangeStart;

    @JsonProperty("settleTimeEnd")
    private String settlementDateRangeEnd;

    // 付款方账号
    @JsonProperty("payAccountNo")
    private String payerBankCard;

    // 收款方名称
    @JsonProperty("recAccountName")
    private String payeeName;

    // 收款方账号
    @JsonProperty("recAccountNo")
    private String payeeBankCard;

    // 收款方开户行
    @JsonProperty("recOpenBankName")
    private String payeeBank;

    @JsonProperty("sign")
    private String sign;

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        DivideReqDTO entity = new DivideReqDTO();
        entity.setSettlementAmount(BigDecimal.ONE);
        Map<String, Object> map = objectMapper.convertValue(entity,  new TypeReference<Map<String, Object>>() {});
        System.out.println(map);
    }


}

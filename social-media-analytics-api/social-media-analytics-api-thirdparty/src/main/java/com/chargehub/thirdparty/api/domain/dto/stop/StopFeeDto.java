package com.chargehub.thirdparty.api.domain.dto.stop;

import com.chargehub.thirdparty.api.annotation.StopPlatform;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 19:21
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.dto.stop
 * @Filename：StopFeeDto
 */
@Data
public class StopFeeDto {


    /**
     * 停车平台
     */
    private StopPlatform stopPlatform;

    /**
     * 车牌号
     */
    private String carNumber;

    /**
     * 商家自定义单号（原系统采用订单内的 ConsumptionNum 字段）
     */
    private String orderNo;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 充电开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date chargStartTime;

    /**
     * 充电结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date chargEndTime;

    /**
     * 充电站编号
     */
    private Integer stationId;

    /**
     * 充电量
     */
    private BigDecimal chargeAmount;


    /**
     * 停车场编号
     */
    private String parkingNumber;
}

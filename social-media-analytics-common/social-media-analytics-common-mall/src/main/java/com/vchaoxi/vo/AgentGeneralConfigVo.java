package com.vchaoxi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 通用配置信息字段表
 * </p>
 *
 * @author hanfuxian
 * @since 2024-08-05
 */
@Data
public class AgentGeneralConfigVo implements Serializable {

    private Integer id;

    /**
     * 线下核销地址
     */
    private String offlineRedemptionAddress;
    /**
     * 线下核销联系人
     */
    private String offlineRedemptionContact;
    /**
     * 线下核销联系方式
     */
    private String offlineRedemptionPhone;

    /**
     * 添加时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

}

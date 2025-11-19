package com.vchaoxi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * <p>
 * 应用说明
 * </p>
 *
 * @author hanfuxian
 * @since 2024-09-19
 */
@Data
public class VcShopAgreementVo {


    private Integer id;

    /**
     * 代理商id
     */
    private Integer agentId;

    /**
     * 客户ID
     */
    private Integer shopId;

    /**
     * 注册说明
     */
    private String signupDetails;

    /**
     * 升级说明
     */
    private String upgradeDetails;

    /**
     * 添加时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertTime;


}

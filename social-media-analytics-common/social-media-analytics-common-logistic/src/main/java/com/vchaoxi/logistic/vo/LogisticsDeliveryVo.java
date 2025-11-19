package com.vchaoxi.logistic.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-31
 */
@Data
public class LogisticsDeliveryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 快递公司 ID
     */
    private String deliveryId;

    /**
     * 快递公司名称
     */
    private String deliveryName;

    /**
     * 是否支持散单, 1表示支持
     */
    private Integer canUseCash;

    /**
     * 是否支持查询面单余额, 1表示支持
     */
    private Integer canGetQuota;

    /**
     * 支持的服务类型
     */
    private Object serviceType;

    /**
     * 散单对应的bizid，当can_use_cash=1时有效
     */
    private String cashBizId;
}

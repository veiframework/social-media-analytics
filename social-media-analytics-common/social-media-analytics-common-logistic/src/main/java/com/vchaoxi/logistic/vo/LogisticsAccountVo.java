package com.vchaoxi.logistic.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

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
public class LogisticsAccountVo {


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
     * 快递公司编号
     */
    private String deliveryId;

    /**
     * 快递公司名称
     */
    private String deliveryName;

    /**
     * 快递公司客户编码
     */
    private String bizId;

    /**
     * 快递公司客户密码
     */
    private String password;

    /**
     * 备注内容（提交EMS审核需要） 格式要求： 电话：xxxxx 联系人：xxxxx 服务类型：xxxxx 发货地址：xxxx

     */
    private String remarkContent;

    /**
     * 绑定状态  1绑定成功  0解除绑定
     */
    private Integer status;

    /**
     * 电子面单余额
     */
    private Integer quotaNum;

    /**
     * 电子面单余额更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime quotaUpdateTime;

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

package com.vchaoxi.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * <p>
 * 代理商相关 - 代理商表
 * </p>
 *
 * @author wangjiangtao
 * @since 2022-10-11
 */
@Data
public class AgentVo {

    private Integer id;

    /**
     * 代理商名称
     */
    private String name;

    /**
     * 商家logo
     */
    private String logo;

    /**
     * 品牌名称
     */
    private String brand;

    /**
     * 客服电话
     */
    private String servicePhone;

    /**
     * 添加时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertTime;




//
//    /**
//     * 商铺超管账号信息
//     */
//    private ErpAdminVo adminVo;

    /**
     * 是否允许自定义公众号配置信息
     */
    private Integer custom;
}

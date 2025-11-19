package com.vchaoxi.vo;

import com.chargehub.common.security.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单相关 - 订单信息
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@Data
public class OrderInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 代理商id
     */
    private Integer agentId;

    /**
     * 客户id
     */
    private Integer shopId;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 订单单号
     */
    private String orderNo;


    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 商品编号
     */
    private String goodsNo;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 规格的id
     */
    private Integer specificationId;

    /**
     * 规格
     */
    private String specifications;

    /**
     * 商品主图
     */
    private String goodsImg;

    /**
     * 商品类型id
     */
    private Integer goodsTypeId;

    /**
     * 商品类型名称
     */
    private String goodsTypeName;

    /**
     * 商品分组
     */
    private String goodsGroup;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 商品数量
     */
    private Integer goodsNum;

    /**
     * 商品单价
     */
    private BigDecimal unitPrice;
    /**

    /**
     * 订单支付时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /**
     * 添加时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertTime;

    @ApiModelProperty("用户上传照片")
    private String userImages;

}

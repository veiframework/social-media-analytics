package com.vchaoxi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 商品相关 - 商品表
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@Data
public class GoodsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 代理商id
     */
    private Integer agentId;

    /**
     * 商铺id
     */
    private Integer shopId;

    /**
     * 商品分类
     */
    private Integer type;

    /**
     * 商品分类
     */
    private String typeName;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品编号
     */
    private String goodsNo;

    /**
     * 分类状态  1上架  0下架
     */
    private Integer status;

    /**
     * 商铺主图
     */
    private String img;

    /**
     * 商品宣传图片列表
     */
    private String gallery;

    /**
     * 商品宣传图片列表
     */
    private List<String> galleryList;

    /**
     * 商品价格
     */
    private BigDecimal amount;

    @ApiModelProperty("运费")
    private BigDecimal freight;

    /**
     * 会员价格
     */
    private BigDecimal memAmount;

    /**
     * vip价格
     */
    private BigDecimal vipAmount;

    /**
     * 商品销量
     */
    private Integer salesNumber;
    /**
     * 序号
     */
    private Integer serial;

    /**
     * 商品存放的箱格尺寸类型 多个尺寸英文逗号分割 为空表示支持全部箱格尺寸
     */
    private String boxSizes;

    /**
     * 商品服务单位  双 次 件等
     */
    private String unit;

    /**
     * 商品详情
     */
    private String details;

    /**
     * 商品简介
     */
    private String brief;

    /**
     * 是否设置了banner图
     */
    private Integer isBanner;
    /**
     * 是否促销 0正常  1促销
     */
    private Integer isPromotion;

    /**
     * 添加时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertTime;

    /**
     * 最后修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;




    /**
     * 商品分类
     */
    private TypeVo typeVo;

    /**
     * 商品规格列表
     */
    private List<WinesGoodsSpecificationVo> goodsSpecificationVoList;

    /**
     * 记录当前商品在某个柜子内是否存在现货  1存在  0不存在
     */
    private Integer inStock;
}

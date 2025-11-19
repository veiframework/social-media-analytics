package com.vchaoxi.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 商品相关 - 商品类型表
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@Data
public class TypeVo implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer id;

    /**
     * 代理商id
     */
    private Integer agentId;


    private Integer parentId;

    private String parentName;

    /**
     * 商铺id
     */
    private Integer shopId;

    /**
     * 类型名称
     */
    private String name;

    /**
     * 分类状态  1启用  0停用
     */
    private Integer status;

    /**
     * 序号
     */
    private Integer serial;
}

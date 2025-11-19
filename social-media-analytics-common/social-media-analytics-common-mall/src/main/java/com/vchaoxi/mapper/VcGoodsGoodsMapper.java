package com.vchaoxi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vchaoxi.entity.VcGoodsGoods;
import com.vchaoxi.vo.GoodsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品相关 - 商品表 Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
public interface VcGoodsGoodsMapper extends BaseMapper<VcGoodsGoods> {


    /**
     *
     * @param goodsName
     * @param sortType（不传默认，1、销量，2:价格正序，3:价格倒序）
     * @param memGrade (会员等级 1:普通用户，2:会员用户，3:vip用户)
     * @return
     */
    public List<GoodsVo> goodsSearchWithSort(Page<GoodsVo> page,@Param("goodsName") String goodsName,
                                             @Param("sortType") Integer sortType,
                                             @Param("memGrade") Integer memGrade,
                                             @Param("parentId") Integer parentId,
                                             @Param("type") Integer type);

    /**
     * 查询商品列表
     * @param page
     * @param name
     * @param agentId
     * @param shopId
     * @param status
     * @return
     */
    public List<GoodsVo> adminSelect(Page<GoodsVo> page, @Param("name") String name,
                                     @Param("agentId") Integer agentId,
                                     @Param("shopId") Integer shopId,
                                     @Param("type") Integer type,
                                     @Param("status") Integer status,
                                     @Param(value = "goodsNo") String goodsNo,
                                     @Param("parentId") Integer parentId);


    /**
     * 根据商品id查询商品信息
     * @param id
     * @return
     */
    public GoodsVo selectByGoodId(@Param("id")Integer id);



    /**
     * 查询商品信息 通过商品分类id
     * @param typeId
     * @return
     */
    public List<Map<String,Object>> selectByTypeId(@Param("typeId")Integer typeId);



    /**
     * 用户查询商品列表
     * @param type
     * @param parentType
     *
     * @return
     */
    public List<GoodsVo> userGoodsList(@Param("type") Integer type,
                                       @Param("parentType")Integer parentType);




}

package com.vchaoxi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vchaoxi.entity.VcGoodsType;
import com.vchaoxi.vo.TypeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品相关 - 商品类型表 Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
public interface VcGoodsTypeMapper extends BaseMapper<VcGoodsType> {


    /**
     * 根据类型id查询类型
     * @param id
     * @return
     */
    public TypeVo selectByTypeId(@Param("id")Integer id);


    /**
     * 管理员查询商品分类列表
     * @param page
     * @param name
     * @param agentId
     * @param shopId
     * @return
     */
    public List<TypeVo> adminSelect(Page<TypeVo> page, @Param("name") String name, @Param("agentId")Integer agentId,
                                    @Param("shopId") Integer shopId ,@Param("parentId") Integer parentId);



    /**
     * 通过门店查询商品分类
     * @param shopId
     * @return
     */
    public List<TypeVo> selectByShopId(@Param("shopId")Integer shopId,
                                       @Param("parentId") Integer parentId);


    /**
     * 查询门店商品分类名称
     * @param shopId
     * @return
     */
    public List<Map<String,Object>> selectTypeNameByShopId(@Param("shopId")Integer shopId);

}

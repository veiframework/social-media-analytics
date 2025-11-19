package com.vchaoxi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vchaoxi.entity.VcShopShop;
import com.vchaoxi.vo.VcShopShopVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商铺相关 - 商铺表 Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
public interface VcShopShopMapper extends BaseMapper<VcShopShop> {

    /**
     * 查询商家列表*
     * @param page
     * @param agentId
     * @param shopName
     * @return
     */
    public List<VcShopShopVo> erpSelectList(Page<VcShopShopVo> page, @Param("agentId")Integer agentId, @Param("shopName") String shopName);


    /**
     * 通过id查询商家详情*
     * @param id
     * @return
     */
    public VcShopShopVo erpSelectById(@Param("id")Integer id);


    /**
     * 通过代理商查询客户id
     * @param agentId
     * @return
     */
    public List<Integer> selectShopIdsByAgentId(@Param("agentId")Integer agentId);


    /**
     * 查询全部的商家列表
     * @param agentId
     * @return
     */
    public List<VcShopShopVo> selectAllShop(@Param("agentId")Integer agentId);
}

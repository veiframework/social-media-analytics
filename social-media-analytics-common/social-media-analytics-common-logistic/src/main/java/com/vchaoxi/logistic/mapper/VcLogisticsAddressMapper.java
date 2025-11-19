package com.vchaoxi.logistic.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vchaoxi.logistic.domain.VcLogisticsAddress;
import com.vchaoxi.logistic.vo.LogisticsAddressVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-30
 */
public interface VcLogisticsAddressMapper extends BaseMapper<VcLogisticsAddress> {


    /**
     * 管理员查询物流地址
     * @param page
     * @param agentId
     * @param shopId
     * @return
     */
    public List<LogisticsAddressVo> adminSelectList(Page<LogisticsAddressVo> page, @Param("agentId") Integer agentId, @Param("shopId") Integer shopId);


    /**
     * 根据商家查询物流地址
     * @param shopId
     * @return
     */
    public List<LogisticsAddressVo> selectByShopId(@Param("shopId") Integer shopId);


    /**
     * 查询商家默认地址
     * @param shopId
     * @return
     */
    public LogisticsAddressVo selectShopDefault(@Param("shopId") Integer shopId);
}

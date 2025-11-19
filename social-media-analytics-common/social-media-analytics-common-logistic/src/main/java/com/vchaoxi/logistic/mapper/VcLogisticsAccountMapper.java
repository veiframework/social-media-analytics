package com.vchaoxi.logistic.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vchaoxi.logistic.domain.VcLogisticsAccount;
import com.vchaoxi.logistic.vo.LogisticsAccountVo;
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
public interface VcLogisticsAccountMapper extends BaseMapper<VcLogisticsAccount> {


    /**
     * 管理员查询物流账户
     * @param page
     * @param agentId
     * @param shopId
     * @return
     */
    public List<LogisticsAccountVo> adminSelectList(Page<LogisticsAccountVo> page, @Param("agentId")Integer agentId, @Param("shopId")Integer shopId);


    /**
     * 根据商家查询物流账号
     * @param shopId
     * @return
     */
    public List<LogisticsAccountVo> selectByShopId(@Param("shopId")Integer shopId);
}

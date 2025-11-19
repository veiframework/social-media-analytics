package com.vchaoxi.logistic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vchaoxi.logistic.domain.VcLogisticsDelivery;
import com.vchaoxi.logistic.vo.LogisticsDeliveryVo;


import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-31
 */
public interface VcLogisticsDeliveryMapper extends BaseMapper<VcLogisticsDelivery> {


    /**
     * 查询支持的物流公司
     * @return
     */
    public List<LogisticsDeliveryVo> selectDelivery();
}

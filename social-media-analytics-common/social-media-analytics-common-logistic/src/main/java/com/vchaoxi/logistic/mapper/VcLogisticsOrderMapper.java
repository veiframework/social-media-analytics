package com.vchaoxi.logistic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vchaoxi.logistic.domain.VcLogisticsOrder;
import com.vchaoxi.logistic.vo.LogisticsOrderVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-31
 */
public interface VcLogisticsOrderMapper extends BaseMapper<VcLogisticsOrder> {


    /**
     * 管理员查询物流订单列表
     * @param page
     * @param agentId
     * @param shopId
     * @param orderNo
     * @param waybillId
     * @param status
     * @return
     */
    public List<LogisticsOrderVo> adminSelectList(Page<LogisticsOrderVo> page, @Param("agentId") Integer agentId,
                                                  @Param("shopId") Integer shopId, @Param("orderNo") String orderNo,
                                                  @Param("waybillId") String waybillId, @Param("status") Integer status);


    /**
     * 查询订单物流信息  根据订单id和物流订单类型
     * @param orderId
     * @param orderType
     * @return
     */
    public LogisticsOrderVo selectByOrderIdAndType(@Param("orderId") Integer orderId,@Param("orderType") Integer orderType);


    /**
     * 查询物流订单的运单号
     * @param orderId
     * @param orderType
     * @return
     */
    public String selectWaybillIdByOrderIdAndType(@Param("orderId") Integer orderId,@Param("orderType") Integer orderType);
}

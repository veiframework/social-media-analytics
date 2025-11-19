package com.vchaoxi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vchaoxi.entity.VcOrderInfo;
import com.vchaoxi.vo.OrderInfoVo;
import com.vchaoxi.vo.SalesVolumeStatisticsVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 订单相关 - 订单信息 Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
public interface VcOrderInfoMapper extends BaseMapper<VcOrderInfo> {


    /**
     * 根据订单id查询订单详情列表
     * @param orderId
     * @return
     */
    public List<OrderInfoVo> selectByOrderId(@Param("orderId")Integer orderId);


    /**
     * 根据订单详情id查询订单信息
     * @param orderInfoId
     * @return
     */
    public OrderInfoVo selectByOrderInfoId(@Param("orderInfoId")Integer orderInfoId);

    //统计相关

    /**
     * 按日统计
     * @param startTime
     * @param endTime
     * @param sortType 1:销量排序，2:销售额排序
     * @return
     */
    public List<SalesVolumeStatisticsVo> salesVolumeStatistics(String startTime, String endTime,Integer sortType);

    /**
     * 一段时间内总销售额的统计
     * @param startTime
     * @param endTime
     * @return
     */
    public BigDecimal totalSalesStatistics(String startTime, String endTime);

    /**
     * 获取分组名称
     *
     * @param goodsTypeId
     * @return
     */
    String getGroup(String goodsTypeId);

}

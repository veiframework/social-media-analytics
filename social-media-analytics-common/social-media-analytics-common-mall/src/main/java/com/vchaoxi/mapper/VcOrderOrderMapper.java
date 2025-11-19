package com.vchaoxi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vchaoxi.entity.VcOrderOrder;
import com.vchaoxi.vo.OrderStatisticsVo;
import com.vchaoxi.vo.OrderStatusVo;
import com.vchaoxi.vo.OrderVo;
import com.vchaoxi.vo.UserPurchaseStaticsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单相关 - 订单信息 Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
public interface VcOrderOrderMapper extends BaseMapper<VcOrderOrder> {


    /**
     * 查询订单详情
     * @param orderId
     * @return
     */
    public OrderVo selectByOrderId(@Param("orderId")Integer orderId);


    /**
     * 管理员查询订单列表
     * @param page
     * @param agentId
     * @param shopId
     * @param orderNo
     * @param status
     * @return
     */
    public List<OrderVo> adminSelect(Page<OrderVo> page, @Param("agentId")Integer agentId,
                                     @Param("shopId")Integer shopId,
                                     @Param("orderNo")String orderNo,
                                     @Param("status")Integer status,
                                     @Param("payWay")Integer payWay,
                                     @Param("invoiceStatus")Integer invoiceStatus,
                                     @Param("deliveryStatus")Integer deliveryStatus,
                                     @Param("startTime")String startTime,
                                     @Param("endTime")String endTime);

    /**
     * 用户端查询订单列表
     * @param userId
     * @param status
     * @return
     */
    public List<OrderVo> userSelect(Page<OrderVo> page,@Param("userId")Integer userId, @Param("status")Integer status);

    /**
     * 用户端查询订单状态数量
     */
    public OrderStatusVo userSelOrderStatus(@Param("userId")Integer userId);

    //订单统计相关
    public List<OrderStatisticsVo> orderStatisticsByDay(String startTime, String endTime);
    public List<OrderStatisticsVo> orderStatisticsByWeek(String startTime, String endTime);
    //按月统计订单量
    public List<OrderStatisticsVo> orderStatisticsByMonth(String startTime, String endTime);
    //按年统计订单量
    public List<OrderStatisticsVo> orderStatisticsByYear(String startTime, String endTime);

    /**
     * 用户下单统计
     * @param startTime
     * @param endTime
     * @param sortType 1:订单量排序，2:购买额排序
     * @return
     */
    public List<UserPurchaseStaticsVo> userPuchaseStatics(Page<UserPurchaseStaticsVo> page,String startTime, String endTime,Integer sortType);

}

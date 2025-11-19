package com.vchaoxi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vchaoxi.entity.VcOrderPayRecord;
import com.vchaoxi.vo.OrderPayRecordVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 订单相关 - 订单支付记录表 Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
public interface VcOrderPayRecordMapper extends BaseMapper<VcOrderPayRecord> {


    /**
     * 通过单号查询订单支付记录
     * @param orderId
     * @return
     */
    public OrderPayRecordVo selectByOrderId(@Param("orderId")Integer orderId);
}

package com.vchaoxi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vchaoxi.entity.VcOrderOrder;
import com.vchaoxi.entity.VcUserPayRecord;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.param.OrderParam;
import com.vchaoxi.vo.CommonResult;
import com.vchaoxi.vo.WxDeliverUploadShippingResultVo;
import com.vchaoxi.vo.ZjsLogisticsInfoVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 订单相关 - 订单信息 服务类
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
public interface IVcOrderOrderService extends IService<VcOrderOrder> {


    /**
     * 创建订单
     * @param orderParam
     * @return
     */
    public CommonResult createOrder(OrderParam orderParam);

    public CommonResult createOrderByCar(VcUserUser curLoginUser, Integer receiveAddressId, OrderParam orderParam);


    /**
     * 创建支付订单信息
     * @param orderParam
     * @return
     */
    public VcUserPayRecord payOrder(OrderParam orderParam);

    /**
     * 创建线下支付订单信息
     * @param orderParam
     * @return
     */
    public CommonResult payOrderOffline(OrderParam orderParam);
    /**
     * 订单退款
     * @param orderParam
     * @return
     */
    public CommonResult refundOrder(OrderParam orderParam);



    /**
     * 发货
     * @param orderParam
     * @return
     */
    public CommonResult deliverGoods(OrderParam orderParam);



    /**
     * 宅急送订单回调
     * @param httpServletRequest
     * @return
     */
    public Map<String,String> zjsDeliverNotyfy(HttpServletRequest httpServletRequest);

    /**
     * 微信支付订单回调通知
     * @param httpServletRequest
     * @return
     */
    public String wxPayNotify(HttpServletRequest httpServletRequest);

    /**
     * 微信支付退款回调
     * @param httpServletRequest
     * @return
     */
    public String wxRefundNotify(HttpServletRequest httpServletRequest);

    /**
     * 完成收货相关的处理
     * @param orderId
     *  @param handleType (0:自动处理，1:用户手动确认)
     */
    public CommonResult completeReceipt(Integer orderId,Integer handleType);

    /**
     * 通过单号获取物流信息
     * @param mailNo
     * @return
     */
    public ZjsLogisticsInfoVo logisticsInfo(String mailNo);

    /**
     * 微信端发货信息提交
     * @return
     */
    public WxDeliverUploadShippingResultVo uploadShippingInfo(Integer orderId);

    /**
     * 删除订单
     * @param orderParam
     * @return
     */
    CommonResult delOrderById(OrderParam orderParam);

    public void finishOrder(String orderId);
}

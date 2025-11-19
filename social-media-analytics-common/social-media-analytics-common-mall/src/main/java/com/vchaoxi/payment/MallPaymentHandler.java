package com.vchaoxi.payment;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.chargehub.common.core.utils.ThreadHelper;
import com.chargehub.payment.PaymentChannel;
import com.chargehub.payment.PaymentCommand;
import com.chargehub.payment.PaymentCommandDecorator;
import com.chargehub.payment.bean.*;
import com.vchaoxi.constant.OrderConstant;
import com.vchaoxi.constant.SysConstant;
import com.vchaoxi.entity.*;
import com.vchaoxi.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Zhanghaowei
 * @date 2025/08/12 17:59
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class MallPaymentHandler implements PaymentCommandDecorator {

    @Autowired
    private IVcOrderOrderService vcOrderOrderService;

    @Autowired
    private IVcUserPayRecordService vcUserPayRecordService;

    @Autowired
    private IVcOrderInfoService iVcOrderInfoService;

    @Autowired
    private IVcOrderInfoService vcOrderInfoService;

    @Autowired
    private IVcOrderPayRecordService vcOrderPayRecordService;

    @Autowired
    private IVcGoodsGoodsService vcGoodsGoodsService;

    @Autowired
    private IVcShopWxService vcShopWxService;

    @Autowired
    private IVcUserUserService vcUserUserService;

    @Override
    public String businessTypeCode() {
        return "mall";
    }

    @Override
    public void paymentNotify(PaymentNotifyParam param) {
        String orderNo = param.getOutTradeNo();
        //启动新线程 ，处理订单业务逻辑


        VcOrderOrder vcOrderOrder = vcOrderOrderService.lambdaQuery().eq(VcOrderOrder::getOrderNo, orderNo).one();
        Assert.notNull(vcOrderOrder, "订单不存在");

        Integer status = vcOrderOrder.getStatus();
        if (OrderConstant.ORDER_STATUS_TIMEOUT.equals(status)) {
            //订单超时, 但是用户支付成功则自动退款
            ThreadHelper.execute(() -> {
                ThreadUtil.safeSleep(30 * 1000);
                PaymentCommand paymentCommand = SpringUtil.getBean(PaymentCommand.class);
                PaymentRefundParam paymentRefundParam = new PaymentRefundParam();
                paymentRefundParam.setOutTradeNo(orderNo);
                paymentRefundParam.setTotalFee(vcOrderOrder.getAmount().toString());
                paymentRefundParam.setRefundFee(vcOrderOrder.getAmount().toString());
                paymentRefundParam.setTradeType("JSAPI");
                paymentRefundParam.setBusinessTypeCode("mall");
                paymentRefundParam.setExtendParams(MapUtil.of("tenantId", "0"));
                paymentCommand.createRefund(paymentRefundParam, PaymentChannel.WECHAT);
            });
            return;
        }
        int updateStatus = OrderConstant.ORDER_STATUS_PAY;
        Integer orderType = vcOrderOrder.getOrderType();
        if (OrderConstant.ORDER_TYPE_APPOINTMENT.equals(orderType)) {
            updateStatus = OrderConstant.ORDER_INFO_STATUS_102;
        }
        //修改订单为已支付
        vcOrderOrderService.lambdaUpdate().eq(VcOrderOrder::getId, vcOrderOrder.getId()).set(VcOrderOrder::getStatus, updateStatus)
                .set(VcOrderOrder::getPayTime, LocalDateTime.now()).update();

        vcOrderInfoService.lambdaUpdate().eq(VcOrderInfo::getIsDelete, 0).eq(VcOrderInfo::getOrderId, vcOrderOrder.getId())
                .set(VcOrderInfo::getStatus, OrderConstant.ORDER_DETAIL_STATUS_PAY).set(VcOrderInfo::getPayTime, LocalDateTime.now()).update();


        vcOrderPayRecordService.lambdaUpdate().eq(VcOrderPayRecord::getRecordType, OrderConstant.ORDER_PAY_RECORD_TYPE_PATY)
                .eq(VcOrderPayRecord::getOrderId, vcOrderOrder.getId()).eq(VcOrderPayRecord::getIsDelete, 0)
                .set(VcOrderPayRecord::getPayTime, LocalDateTime.now()).set(VcOrderPayRecord::getPayWay, OrderConstant.PAY_WAY_WECHAT)
                .set(VcOrderPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_PAY).update();

        //修改支付记录为已完成
        vcUserPayRecordService.lambdaUpdate().eq(VcUserPayRecord::getRecordId, vcOrderOrder.getId())
                .set(VcUserPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_PAY)
                .set(VcUserPayRecord::getPayWay, OrderConstant.PAY_WAY_WECHAT)
                .set(VcUserPayRecord::getPayTime, LocalDateTime.now()).update();
        //处理订单产品销量问题
        List<VcOrderInfo> orderInfoList = vcOrderInfoService.lambdaQuery().eq(VcOrderInfo::getIsDelete, 0)
                .eq(VcOrderInfo::getOrderId, vcOrderOrder.getId()).list();
        if (!CollectionUtils.isEmpty(orderInfoList)) {
            Map<Integer, Integer> mergedMap = orderInfoList.stream()
                    .collect(Collectors.groupingBy(VcOrderInfo::getGoodsId,
                            Collectors.summingInt(VcOrderInfo::getGoodsNum)));
            List<VcGoodsGoods> goodsList = new ArrayList<>();
            mergedMap.forEach((goodsId, totalGoodsNum) -> {
                VcGoodsGoods vcGoodsGoods = vcGoodsGoodsService.getById(goodsId);
                if (vcGoodsGoods != null) {
                    Integer salesNum = vcGoodsGoods.getSalesNumber() + totalGoodsNum;
                    vcGoodsGoods.setSalesNumber(salesNum);
                    goodsList.add(vcGoodsGoods);
                }
            });
            vcGoodsGoodsService.updateBatchById(goodsList);
            log.info("增加产品销量的操作orderId:{}", vcOrderOrder.getId());
        }
    }

    @Override
    public void refundNotify(PaymentRefundNotifyParam param) {
        String orderNo = param.getOutTradeNo();
        VcOrderOrder vcOrderOrder = vcOrderOrderService.lambdaQuery().eq(VcOrderOrder::getOrderNo, orderNo).one();
        Assert.notNull(vcOrderOrder, "订单不存在");
        //修改订单为已支付
        vcOrderOrderService.lambdaUpdate().eq(VcOrderOrder::getId, vcOrderOrder.getId())
                .set(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_RETURN)
                .set(VcOrderOrder::getPayTime, LocalDateTime.now()).update();

        vcOrderInfoService.lambdaUpdate().eq(VcOrderInfo::getIsDelete, 0).eq(VcOrderInfo::getOrderId, vcOrderOrder.getId())
                .set(VcOrderInfo::getStatus, OrderConstant.ORDER_DETAIL_STATUS_RETURN).set(VcOrderInfo::getPayTime, LocalDateTime.now()).update();


        vcOrderPayRecordService.lambdaUpdate().eq(VcOrderPayRecord::getRecordType, OrderConstant.ORDER_PAY_RECORD_TYPE_REFUND)
                .eq(VcOrderPayRecord::getOrderId, vcOrderOrder.getId()).eq(VcOrderPayRecord::getIsDelete, 0)
                .set(VcOrderPayRecord::getPayTime, LocalDateTime.now()).set(VcOrderPayRecord::getPayWay, OrderConstant.PAY_WAY_WECHAT)
                .set(VcOrderPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_RETURN).update();

        //修改支付记录为已完成
        vcUserPayRecordService.lambdaUpdate().eq(VcUserPayRecord::getRecordId, vcOrderOrder.getId())
                .set(VcUserPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_RETURN)
                .set(VcUserPayRecord::getPayWay, OrderConstant.PAY_WAY_WECHAT)
                .set(VcUserPayRecord::getPayTime, LocalDateTime.now()).update();
    }

    @Override
    public void createOrder(PaymentResult paymentResult) {

    }

    @Override
    public void createRefund(PaymentRefundParam param) {
        String outTradeNo = param.getOutTradeNo();
        VcOrderOrder vcOrderOrder = vcOrderOrderService.lambdaQuery().eq(VcOrderOrder::getOrderNo, outTradeNo).one();
        Assert.notNull(vcOrderOrder, "订单不存在");

        if (vcOrderOrder.getStatus().equals(OrderConstant.ORDER_STATUS_FAILED_REFUND)) {
            //退款失败更新退款编号
            vcUserPayRecordService.lambdaUpdate()
                    .eq(VcUserPayRecord::getRecordId, vcOrderOrder.getId())
                    .set(VcUserPayRecord::getOrderNo, param.getOutRefundNo())
                    .update();
            return;
        }

        List<VcOrderInfo> list = iVcOrderInfoService.lambdaQuery().eq(VcOrderInfo::getOrderId, vcOrderOrder.getId()).list();
        Assert.notEmpty(list, "缺少订单详情");
        int goodsNum = list.stream().mapToInt(VcOrderInfo::getGoodsNum).sum();


        Assert.isFalse(vcOrderOrder.getStatus().equals(OrderConstant.ORDER_STATUS_PAY) && !vcOrderOrder.getDeliveryStatus().equals(OrderConstant.DELIVERY_STATUS_DEFAULT), "当前订单已交付物流公司发货中，无法退款");

        Assert.isTrue(vcOrderOrder.getStatus().equals(OrderConstant.ORDER_STATUS_PAY) || vcOrderOrder.getStatus().equals(OrderConstant.ORDER_STATUS_TIMEOUT) || vcOrderOrder.getStatus().equals(OrderConstant.ORDER_STATUS_REQUIRE_REFUND), "当前订单不允许本操作");

        vcOrderOrderService.lambdaUpdate().eq(VcOrderOrder::getId, vcOrderOrder.getId()).set(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_RETURNING).update();
        VcShopWx vcShopWx = vcShopWxService.lambdaQuery().eq(VcShopWx::getIsDelete, 0).eq(VcShopWx::getShopId, vcOrderOrder.getShopId()).one();

        //添加退款记录
        VcOrderPayRecord vcOrderPayRecord = new VcOrderPayRecord();
        vcOrderPayRecord.setAgentId(vcOrderOrder.getAgentId());
        vcOrderPayRecord.setShopId(vcOrderOrder.getShopId());
        vcOrderPayRecord.setOrderId(vcOrderOrder.getId());
        vcOrderPayRecord.setRecordType(OrderConstant.ORDER_PAY_RECORD_TYPE_REFUND);
        vcOrderPayRecord.setStatus(OrderConstant.ORDER_PAY_STATUS_RETURNING);
        vcOrderPayRecord.setAmount(vcOrderOrder.getAmount());//退款的金额
        vcOrderPayRecord.setGoodsNum(goodsNum);
        vcOrderPayRecordService.save(vcOrderPayRecord);

        //添加用户退款记录
        VcUserPayRecord vcUserPayRecord = new VcUserPayRecord();
        vcUserPayRecord.setOrderNo(param.getOutRefundNo());
        vcUserPayRecord.setUserId(vcOrderOrder.getUserId());
        vcUserPayRecord.setAgentId(vcOrderOrder.getAgentId());
        vcUserPayRecord.setShopId(vcOrderOrder.getShopId());
        vcUserPayRecord.setRecordType(OrderConstant.ORDER_PAY_RECORD_TYPE_REFUND);
        vcUserPayRecord.setAmount(vcOrderOrder.getAmount());
        vcUserPayRecord.setGoodsNum(goodsNum);
        vcUserPayRecord.setStatus(OrderConstant.ORDER_PAY_STATUS_RETURNING);
        vcUserPayRecord.setMchId(vcShopWx.getMchId());
        vcUserPayRecord.setRecordId(vcOrderOrder.getId());
        vcUserPayRecordService.save(vcUserPayRecord);

    }

    @Override
    public void afterCreateRefund(PaymentRefundResult result) {
        if (!result.getRefundResult()) {
            vcOrderOrderService.lambdaUpdate()
                    .eq(VcOrderOrder::getOrderNo, result.getOutTradeNo())
                    .set(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_FAILED_REFUND)
                    .update();
        }
    }

}

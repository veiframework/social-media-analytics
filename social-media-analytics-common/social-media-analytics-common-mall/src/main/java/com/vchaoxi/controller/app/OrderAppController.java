package com.vchaoxi.controller.app;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.common.core.constant.HttpStatus;
import com.chargehub.common.core.web.controller.BaseController;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.payment.PaymentChannel;
import com.chargehub.payment.PaymentCommand;
import com.chargehub.payment.wechat.WechatPaymentParam;
import com.vchaoxi.constant.OrderConstant;
import com.vchaoxi.entity.VcOrderOrder;
import com.vchaoxi.entity.VcUserPayRecord;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.entity.WinesReceiverAddress;
import com.vchaoxi.mapper.VcOrderInfoMapper;
import com.vchaoxi.mapper.VcOrderOrderMapper;
import com.vchaoxi.mapper.VcOrderPayRecordMapper;
import com.vchaoxi.param.MallOrderRefundParams;
import com.vchaoxi.param.OrderParam;
import com.vchaoxi.service.IVcOrderInfoService;
import com.vchaoxi.service.IVcOrderOrderService;
import com.vchaoxi.service.IVcUserUserService;
import com.vchaoxi.service.IWinesReceiverAddressService;
import com.vchaoxi.vo.CommonResult;
import com.vchaoxi.vo.OrderStatusVo;
import com.vchaoxi.vo.OrderVo;
import com.vchaoxi.vo.WinesReceiverAddressVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商铺商铺服务相关接口
 */
@RestController
@RequestMapping("/app/order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "用户订单管理")
public class OrderAppController extends BaseController {


    @Autowired
    private IVcOrderOrderService vcOrderOrderService;
    @Autowired
    private IVcOrderInfoService vcOrderInfoService;
    @Autowired
    private IWinesReceiverAddressService iWinesReceiverAddressService;
    @Autowired
    private IVcUserUserService vcUserUserService;
    private final PaymentCommand paymentCommand;


    private final VcOrderOrderMapper vcOrderOrderMapper;
    private final VcOrderInfoMapper vcOrderInfoMapper;
    private final VcOrderPayRecordMapper vcOrderPayRecordMapper;


    /**
     * 查询订单列表 status
     *
     * @param status 0待支付   1已支付（待发货）  2超时未支付 3已发货 4收货完成
     * @return
     */
    @GetMapping("/list")
    @RequiresLogin(doIntercept = false)
    @ApiOperation("分页")
    public CommonResult list(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, @RequestParam(defaultValue = "10", value = "pageSize") Integer pageSize,
                             @RequestParam(value = "status", defaultValue = "") Integer status) {

        VcUserUser curLoginUser = vcUserUserService.getCurrentUser();
        Page<OrderVo> page = new Page<>(pageNum, pageSize);
        List<OrderVo> userSelect = vcOrderOrderMapper.userSelect(page, curLoginUser.getId(), status);
        for (OrderVo orderVo : userSelect) {
            orderVo.setOrderInfoVos(vcOrderInfoMapper.selectByOrderId(orderVo.getId()));
        }
        return CommonResult.success(page.setRecords(userSelect));
    }


    /**
     * 立即购买创建订单
     *
     * @return
     */
    @PostMapping("/create-order")
    @RequiresLogin(doIntercept = false)
    public CommonResult createOrder(@RequestBody @Validated({OrderParam.Create.class}) OrderParam orderParam) {
        if (orderParam.getGoodsNum() <= 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "请至少选择一个商品购买");
        }
        VcUserUser curLoginUser = vcUserUserService.getCurrentUser();

        VcUserUser selUsl = vcUserUserService.getById(curLoginUser.getId());
        orderParam.setCurLoginUser(selUsl);
        return vcOrderOrderService.createOrder(orderParam);
    }

    @PostMapping("/create-order-by-car")

    public CommonResult createOrderByCar(@RequestBody @Validated({OrderParam.CarCreate.class}) OrderParam orderParam) {
        VcUserUser curLoginUser = vcUserUserService.getCurrentUser();

        VcUserUser selUsl = vcUserUserService.getById(curLoginUser.getId());
        return vcOrderOrderService.createOrderByCar(selUsl, orderParam.getReceiveAddressId(), orderParam);
    }


    /**
     * 支付订单
     *
     * @param orderParam
     * @return
     */
    @PostMapping("/pay-order")
    @Transactional(rollbackFor = Exception.class)
    @RequiresLogin(doIntercept = false)
    public CommonResult payOrder(@RequestBody @Validated({OrderParam.PayOrder.class}) OrderParam orderParam) {

        //查询订单信息
        VcOrderOrder vcOrderOrder = vcOrderOrderService.lambdaQuery().eq(VcOrderOrder::getId, orderParam.getOrderId()).one();
        if (vcOrderOrder == null || vcOrderOrder.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "订单不存在或已删除");
        }

        VcUserUser curLoginUser = vcUserUserService.getCurrentUser();

        if (!curLoginUser.getId().equals(vcOrderOrder.getUserId())) {
            return CommonResult.error(HttpStatus.FORBIDDEN, "您无该权限");
        }

        //判断订单状态
        if (!vcOrderOrder.getStatus().equals(OrderConstant.ORDER_STATUS_NOT_PAY)) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "当前订单不允许本操作");
        }
        String orderId = IdWorker.getIdStr();
        vcOrderOrder.setOrderNo(orderId);
        vcOrderOrderService.lambdaUpdate().eq(VcOrderOrder::getId, vcOrderOrder.getId()).set(VcOrderOrder::getOrderNo, orderId).update();
        //生成支付订单
        orderParam.setVcOrderOrder(vcOrderOrder);
        orderParam.setOrderNotify("");
        orderParam.setIp("");
        orderParam.setCurLoginUser(curLoginUser);
        orderParam.setMaOpenId(curLoginUser.getMaOpenid());
        orderParam.setTradeType("JSAPI");
        VcUserPayRecord vcUserPayRecord = vcOrderOrderService.payOrder(orderParam);


        WechatPaymentParam param = new WechatPaymentParam();
        param.setTotalFee(vcUserPayRecord.getAmount().toString());
        param.setTradeType("JSAPI");
        param.setOpenid(orderParam.getMaOpenId());
        param.setBody("下单支付：" + vcUserPayRecord.getOrderNo());
        param.setOutTradeNo(vcUserPayRecord.getOrderNo());
        param.setClientPaymentMethod("MINI_PROGRAM");
        param.setExtendParam(MapUtil.of("tenantId", "0"));
        param.setTimeExpire(3);
        param.setBusinessTypeCode("mall");
        param.setOutTradeNo(vcOrderOrder.getOrderNo());
        return CommonResult.success(paymentCommand.createOrder(param, PaymentChannel.WECHAT));
    }

    /**
     * 线下支付订单
     *
     * @param orderParam
     * @return
     */
    @PostMapping("/pay-order-offline")
    @RequiresLogin(doIntercept = false)

    public CommonResult payOrderOffline(@RequestBody @Validated({OrderParam.PayOrder.class}) OrderParam orderParam) {

        //查询订单信息
        VcOrderOrder vcOrderOrder = vcOrderOrderService.lambdaQuery().eq(VcOrderOrder::getId, orderParam.getOrderId()).one();
        if (vcOrderOrder == null || vcOrderOrder.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "订单不存在或已删除");
        }

        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();

        if (!vcUserUser.getId().equals(vcOrderOrder.getUserId())) {
            return CommonResult.error(HttpStatus.FORBIDDEN, "您无该权限");
        }

        //判断订单状态
        if (!vcOrderOrder.getStatus().equals(OrderConstant.ORDER_STATUS_NOT_PAY)) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "当前订单不允许本操作");
        }

        //生成线下支付订单
        orderParam.setVcOrderOrder(vcOrderOrder);
        return vcOrderOrderService.payOrderOffline(orderParam);
    }


    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/order-info")
    @RequiresLogin(doIntercept = false)

    public CommonResult orderInfo(@RequestParam(value = "id") Integer id) {
        OrderVo orderVo = vcOrderOrderMapper.selectByOrderId(id);
        if (orderVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "订单不存在或已删除");
        }
        //判断当前用户是否拥有操作权限
        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();

        if (!vcUserUser.getId().equals(orderVo.getUserId())) {
            return CommonResult.error(HttpStatus.FORBIDDEN, "您无该权限");
        }
        WinesReceiverAddress receiverAddress = iWinesReceiverAddressService.getById(orderVo.getReceiverAddressId());
        WinesReceiverAddressVo receiverAddressVo = new WinesReceiverAddressVo();
        BeanUtils.copyProperties(receiverAddress, receiverAddressVo);

        orderVo.setOrderInfoVos(vcOrderInfoMapper.selectByOrderId(orderVo.getId()));
        orderVo.setAddressVo(receiverAddressVo);

        Map<String, Object> map = new HashMap<>();
        map.put("order", orderVo);
        //如果当前订单为已支付状态  则返回支付记录
        if (orderVo.getStatus().equals(OrderConstant.ORDER_STATUS_PAY)) {
            map.put("payRecord", vcOrderPayRecordMapper.selectByOrderId(orderVo.getId()));
        }
        return CommonResult.success(map);
    }

    /**
     * 确认收货
     *
     * @param orderParam
     * @return
     */
    @PostMapping("/complete_receipt")
    @RequiresLogin(doIntercept = false)

    public CommonResult completeReceipt(@RequestBody @Validated({OrderParam.ConfirmReceipt.class}) OrderParam orderParam) {
        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();

        VcOrderOrder orderOrder = vcOrderOrderService.getById(orderParam.getOrderId());
        if (orderOrder == null || orderOrder.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.FORBIDDEN, "该订单不存在或已删除");
        }
        if (!vcUserUser.getId().equals(orderOrder.getUserId())) {
            return CommonResult.error(HttpStatus.FORBIDDEN, "您无权限操作该订单");
        }

        return CommonResult.success(vcOrderOrderService.completeReceipt(orderParam.getOrderId(), 1));
    }

    @GetMapping("/order_status_num")
    @RequiresLogin(doIntercept = false)

    public CommonResult orderStatusNum() {

        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();

        OrderStatusVo statusVo = vcOrderOrderMapper.userSelOrderStatus(vcUserUser.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("statusVo", statusVo);
        return CommonResult.success(map);
    }


    @GetMapping("/order_logistics_info")
    @RequiresLogin(doIntercept = false)

    public CommonResult orderLogisticsInfo(@RequestParam(value = "id") Integer id) {

        OrderVo orderVo = vcOrderOrderMapper.selectByOrderId(id);
        if (orderVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "订单不存在或已删除");
        }
        //判断当前用户是否拥有操作权限
        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();
        if (!vcUserUser.getId().equals(orderVo.getUserId())) {
            return CommonResult.error(HttpStatus.FORBIDDEN, "您无该权限");
        }
//        if (orderVo.getStatus() != 3 && orderVo.getStatus() != 4) {
//            return CommonResult.error(HttpStatus.FORBIDDEN,"该订单还未发货");
//        }

        return CommonResult.success(vcOrderOrderService.logisticsInfo(orderVo.getTrackingNo()));
    }

    @ApiOperation("申请退款")
    @PostMapping("/require/refund")
    public CommonResult requireRefund(@RequestBody @Validated MallOrderRefundParams params) {
        vcOrderOrderService.lambdaUpdate()
                .set(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_REQUIRE_REFUND)
                .set(VcOrderOrder::getRefundReason, params.getRefundReason())
                .eq(VcOrderOrder::getId, params.getOrderId())
                .update();
        return CommonResult.success();
    }

}

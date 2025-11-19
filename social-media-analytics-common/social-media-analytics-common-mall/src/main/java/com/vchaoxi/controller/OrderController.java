package com.vchaoxi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.common.core.constant.HttpStatus;
import com.chargehub.common.core.web.controller.BaseController;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.utils.SecurityUtils;
import com.vchaoxi.constant.OrderConstant;
import com.vchaoxi.constant.SysConstant;
import com.vchaoxi.entity.Invoice;
import com.vchaoxi.entity.VcOrderOrder;
import com.vchaoxi.entity.WinesReceiverAddress;
import com.vchaoxi.mapper.VcOrderInfoMapper;
import com.vchaoxi.mapper.VcOrderOrderMapper;
import com.vchaoxi.mapper.VcOrderPayRecordMapper;
import com.vchaoxi.mapper.VcUserUserMapper;
import com.vchaoxi.param.OrderParam;
import com.vchaoxi.param.RejectRefundDto;
import com.vchaoxi.service.IInvoiceService;
import com.vchaoxi.service.IVcOrderOrderService;
import com.vchaoxi.service.IWinesReceiverAddressService;
import com.vchaoxi.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 订单相关接口
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController extends BaseController {

    @Autowired
    private HttpServletRequest httpServletRequest;


    @Autowired
    private IVcOrderOrderService iVcOrderOrderService;
    @Autowired
    private IWinesReceiverAddressService iWinesReceiverAddressService;
    @Autowired
    private IInvoiceService invoiceService;

    private final VcOrderOrderMapper vcOrderOrderMapper;
    private final VcOrderInfoMapper vcOrderInfoMapper;
    private final VcOrderPayRecordMapper vcOrderPayRecordMapper;
    private final VcUserUserMapper vcUserUserMapper;


    /**
     * 删除订单
     *
     * @param orderParam
     * @return
     */
    @PostMapping("/del")
    @RequiresPermissions({"operateData:order:del"})
    public CommonResult delOrderById(@RequestBody @Validated({OrderParam.DelOrder.class}) OrderParam orderParam) {
        return iVcOrderOrderService.delOrderById(orderParam);
    }


    /**
     * 获取订单列表
     *
     * @return
     */
    @GetMapping("/list")
    @RequiresPermissions({"operateData:order:list"})
    public CommonResult list(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum,
                             @RequestParam(defaultValue = "10", value = "pageSize") Integer pageSize,
                             @RequestParam(value = "orderNo", defaultValue = "") String orderNo,
                             @RequestParam(value = "shopId", defaultValue = "") Integer shopId,
                             @RequestParam(value = "status", defaultValue = "") Integer status,
                             @RequestParam(value = "payWay", defaultValue = "") Integer payWay,
                             @RequestParam(value = "deliveryStatus", defaultValue = "0") Integer deliveryStatus,
                             @RequestParam(value = "startTime", defaultValue = "") String startTime,
                             @RequestParam(value = "endTime", defaultValue = "") String endTime) {

        Integer agentId = getAgentId();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (shopId == null) {
            shopId = loginUser.getShopId();
        }

        Page<OrderVo> page = new Page<>(pageNum, pageSize);
        List<OrderVo> list = vcOrderOrderMapper.adminSelect(page, agentId, shopId, orderNo, status, payWay, null, deliveryStatus, startTime, endTime);
        for (OrderVo orderVo : list) {
            orderVo.setOrderInfoVos(vcOrderInfoMapper.selectByOrderId(orderVo.getId()));
        }
        return CommonResult.success(page.setRecords(list));
    }


    /**
     * 获取现下支付的订单列表
     *
     * @return
     */
    @GetMapping("/offline_list")
    @RequiresPermissions({"operateData:order:offline_list"})
    public CommonResult offlineList(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum,
                                    @RequestParam(defaultValue = "10", value = "pageSize") Integer pageSize,
                                    @RequestParam(value = "orderNo", defaultValue = "") String orderNo,
                                    @RequestParam(value = "shopId", defaultValue = "") Integer shopId,
                                    @RequestParam(value = "status", defaultValue = "") Integer status,
                                    @RequestParam(value = "invoiceStatus", defaultValue = "") Integer invoiceStatus,
                                    @RequestParam(value = "deliveryStatus", defaultValue = "0") Integer deliveryStatus,
                                    @RequestParam(value = "startTime", defaultValue = "") String startTime,
                                    @RequestParam(value = "endTime", defaultValue = "") String endTime) {

        Integer agentId = getAgentId();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (shopId == null) {
            shopId = loginUser.getShopId();
        }

        Page<OrderVo> page = new Page<>(pageNum, pageSize);
        List<OrderVo> list = vcOrderOrderMapper.adminSelect(page, agentId, shopId, orderNo, status, 2, invoiceStatus, deliveryStatus, startTime, endTime);
        for (OrderVo orderVo : list) {
            orderVo.setOrderInfoVos(vcOrderInfoMapper.selectByOrderId(orderVo.getId()));
            if (orderVo.getInvoiceId() != null) {
                Invoice invoice = invoiceService.getById(orderVo.getInvoiceId());
                InvoiceVo invoiceVo = new InvoiceVo();
                BeanUtils.copyProperties(invoice, invoiceVo);
                orderVo.setInvoiceVo(invoiceVo);
            }
        }
        return CommonResult.success(page.setRecords(list));
    }


    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/info")
    @RequiresPermissions({"operateData:order:info"})
    public CommonResult info(@RequestParam(value = "id", defaultValue = "") Integer id) {
        OrderVo orderVo = vcOrderOrderMapper.selectByOrderId(id);
        if (orderVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "订单不存在或已删除");
        }


        WinesReceiverAddress receiverAddress = iWinesReceiverAddressService.getById(orderVo.getReceiverAddressId());
        WinesReceiverAddressVo receiverAddressVo = new WinesReceiverAddressVo();
        BeanUtils.copyProperties(receiverAddress, receiverAddressVo);

        //查询子订单信息
        orderVo.setOrderInfoVos(vcOrderInfoMapper.selectByOrderId(orderVo.getId()));
        orderVo.setAddressVo(receiverAddressVo);

        UserVo selUserVo = vcUserUserMapper.userSelectById(orderVo.getUserId());
        if (orderVo.getMemGrade() != null) {
            selUserVo.setMemGrade(orderVo.getMemGrade());
        }
        orderVo.setUserVo(selUserVo);
        if (orderVo.getInvoiceId() != null) {
            Invoice invoice = invoiceService.getById(orderVo.getInvoiceId());
            InvoiceVo invoiceVo = new InvoiceVo();
            BeanUtils.copyProperties(invoice, invoiceVo);
            orderVo.setInvoiceVo(invoiceVo);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("orderVo", orderVo);
        //如果当前订单为已支付状态  则返回支付记录
        if (orderVo.getStatus().equals(OrderConstant.ORDER_STATUS_PAY)) {
            map.put("payRecord", vcOrderPayRecordMapper.selectByOrderId(orderVo.getId()));
        }
        return CommonResult.success(map);
    }

    @PostMapping("/zjs_deliver-notyfy")
    public Map<String, String> zjsDeliverNotyfy(HttpServletRequest httpServletRequest) {
        return iVcOrderOrderService.zjsDeliverNotyfy(httpServletRequest);
    }


    /**
     * 设置订单号
     *
     * @return
     */
    @PostMapping("/set-tracking-no")
    @RequiresPermissions({"operateData:order:set-tracking-no"})
    public CommonResult setTrackingNo(@RequestBody @Validated({OrderParam.SetTrackingNo.class}) OrderParam orderParam) {
        VcOrderOrder vcOrderOrder = iVcOrderOrderService.getById(orderParam.getOrderId());
        if (vcOrderOrder == null || vcOrderOrder.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "订单不存在或已删除");
        }
        if (!vcOrderOrder.getStatus().equals(OrderConstant.ORDER_STATUS_PAY)) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "该订单尚未支付");
        }


        orderParam.setVcOrderOrder(vcOrderOrder);
        return iVcOrderOrderService.deliverGoods(orderParam);
    }


    @GetMapping("/order_logistics_info")

    public CommonResult orderLogisticsInfo(@RequestParam(value = "id") Integer id) {

        OrderVo orderVo = vcOrderOrderMapper.selectByOrderId(id);
        if (orderVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "订单不存在或已删除");
        }
//        if (orderVo.getStatus() != 3 && orderVo.getStatus() != 4) {
//            return CommonResult.error(HttpStatus.FORBIDDEN,"该订单还未发货");
//        }

        return CommonResult.success(iVcOrderOrderService.logisticsInfo(orderVo.getTrackingNo()));
    }

    @PostMapping("/complete_receipt")

    public CommonResult completeReceipt(@RequestBody @Validated({OrderParam.ConfirmReceipt.class}) OrderParam orderParam) {
        VcOrderOrder orderOrder = iVcOrderOrderService.getById(orderParam.getOrderId());
        if (orderOrder == null || orderOrder.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.FORBIDDEN, "该订单不存在或已删除");
        }

        return CommonResult.success(iVcOrderOrderService.completeReceipt(orderParam.getOrderId(), 1));
    }


    @PostMapping("/reject/refund")
    public CommonResult rejectRefund(@RequestBody @Validated RejectRefundDto dto) {
        iVcOrderOrderService.lambdaUpdate()
                .set(VcOrderOrder::getRejectReason, dto.getRejectReason())
                .set(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_REJECT_REFUND)
                .eq(VcOrderOrder::getId, dto.getOrderId())
                .update();
        return CommonResult.success();
    }

    public Integer getAgentId() {
        String agentId = httpServletRequest.getHeader(SysConstant.AGENT_ID);
        if (agentId == null)
            return null;
        try {
            return Integer.parseInt(agentId);
        } catch (Exception e) {
            return null;
        }
    }

}

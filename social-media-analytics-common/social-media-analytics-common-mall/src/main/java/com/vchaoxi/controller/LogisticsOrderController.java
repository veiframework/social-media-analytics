package com.vchaoxi.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.common.core.constant.HttpStatus;
import com.chargehub.common.core.web.controller.BaseController;
import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.common.security.annotation.Debounce;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.utils.SecurityUtils;
import com.chargehub.thirdparty.api.RemoteWxLogisticsService;
import com.chargehub.thirdparty.api.domain.dto.logistics.LoOrderAddParam;
import com.chargehub.thirdparty.api.domain.dto.logistics.LoOrderCancelParam;
import com.chargehub.thirdparty.api.domain.dto.logistics.LoOrderGetParam;
import com.chargehub.thirdparty.api.domain.dto.logistics.LoPathGetParam;
import com.chargehub.thirdparty.api.domain.vo.logistics.LoOrderAddVo;
import com.chargehub.thirdparty.api.domain.vo.logistics.LoOrderCancelVo;
import com.chargehub.thirdparty.api.domain.vo.logistics.LoOrderGetVo;
import com.chargehub.thirdparty.api.domain.vo.logistics.LoPathGetVo;
import com.google.common.collect.Lists;
import com.vchaoxi.constant.OrderConstant;
import com.vchaoxi.entity.VcOrderOrder;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.entity.WinesReceiverAddress;
import com.vchaoxi.logistic.LogisticsConstant;
import com.vchaoxi.logistic.domain.ShopRelativeId;
import com.vchaoxi.logistic.domain.VcLogisticsAccount;
import com.vchaoxi.logistic.domain.VcLogisticsDelivery;
import com.vchaoxi.logistic.domain.VcLogisticsOrder;
import com.vchaoxi.logistic.mapper.VcLogisticsOrderMapper;
import com.vchaoxi.logistic.param.CreateLogisticsOrderParam;
import com.vchaoxi.logistic.param.LogisticsOrderParam;
import com.vchaoxi.logistic.service.IVcLogisticsAccountService;
import com.vchaoxi.logistic.service.IVcLogisticsDeliveryService;
import com.vchaoxi.logistic.service.IVcLogisticsOrderService;
import com.vchaoxi.logistic.service.ShopExtendService;
import com.vchaoxi.logistic.vo.LogisticsOrderVo;
import com.vchaoxi.mapper.WinesReceiverAddressMapper;
import com.vchaoxi.service.IVcOrderOrderService;
import com.vchaoxi.service.IVcUserUserService;
import com.vchaoxi.service.IWinesReceiverAddressService;
import com.vchaoxi.vo.CommonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * 物流订单相关接口
 */
@Slf4j
@RestController
@RequestMapping("/logistics-order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LogisticsOrderController extends BaseController {


    @Autowired
    private IVcOrderOrderService vcOrderOrderService;

    @Autowired
    private IVcLogisticsDeliveryService vcLogisticsDeliveryService;
    @Autowired
    private IVcLogisticsAccountService vcLogisticsAccountService;
    @Autowired
    private RemoteWxLogisticsService wxOpenLogisticsApiClient;
    @Autowired
    private IVcUserUserService vcUserUserService;
    @Autowired
    private IVcLogisticsOrderService vcLogisticsOrderService;
    @Autowired
    private IWinesReceiverAddressService vcUserReceivingAddressService;

    private final ShopExtendService shopExtendService;

    private final VcLogisticsOrderMapper vcLogisticsOrderMapper;
    private final WinesReceiverAddressMapper vcUserReceivingAddressMapper;


    /**
     * 创建物流订单
     *
     * @param logisticsOrderParam
     * @return
     */
    @Debounce
    @PostMapping("/create")
    @RequiresPermissions({"operateData:logistics:order:create"})
    @Transactional
    public Object create(@RequestBody @Validated({LogisticsOrderParam.Create.class}) LogisticsOrderParam logisticsOrderParam) {
        LoginUser loginUser = SecurityUtils.getLoginUser();

        //查询订单信息
        VcOrderOrder vcOrderOrder = vcOrderOrderService.lambdaQuery().eq(VcOrderOrder::getId, logisticsOrderParam.getOrderId()).one();
        if (vcOrderOrder == null || vcOrderOrder.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "订单不存在");
        }


        //判断订单状态是否有误
        CommonResult commonResult = checkParams(logisticsOrderParam, vcOrderOrder);
        if (commonResult != null) {
            return commonResult;
        }
        //请求微信创建订单接口
        CreateLogisticsOrderParam createLogisticsOrderParam = createLogisticsOrder(logisticsOrderParam, vcOrderOrder);
        if (createLogisticsOrderParam.getCommonResult() != null) {
            return createLogisticsOrderParam.getCommonResult();
        }
        //添加订单到数据库内
        saveOrder(createLogisticsOrderParam.getLoOrderAddParam(), createLogisticsOrderParam.getLoOrderAddVo(), vcOrderOrder, logisticsOrderParam);
        //修改业务订单内的物流订单状态
        if (logisticsOrderParam.getOrderType() == 1) {
            vcOrderOrderService.lambdaUpdate().eq(VcOrderOrder::getId, vcOrderOrder.getId())
                    .set(VcOrderOrder::getStatus, OrderConstant.ORDER_INFO_STATUS_201)
                    .set(VcOrderOrder::getPickTrackingNo, createLogisticsOrderParam.getLoOrderAddVo().getWaybill_id())
                    .set(VcOrderOrder::getPickingLogistics, 1).update();
        } else {
            vcOrderOrderService.lambdaUpdate().eq(VcOrderOrder::getId, vcOrderOrder.getId())
                    .set(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_SHIPPED)
                    .set(VcOrderOrder::getTrackingNo, createLogisticsOrderParam.getLoOrderAddVo().getWaybill_id())
                    .set(VcOrderOrder::getDeliveryLogistics, 1).update();
        }
        //为用户添加地址信息
        addUserAddress(vcOrderOrder.getUserId(), logisticsOrderParam);
        return CommonResult.success();
    }


    /**
     * 取消订单
     *
     * @param logisticsOrderParam
     * @return
     */
    @Debounce
    @PostMapping("/cancel")
    @RequiresPermissions({"operateData:logistics:order:cancel"})
    @Transactional
    public CommonResult cancel(@RequestBody @Validated({LogisticsOrderParam.Cancel.class}) LogisticsOrderParam logisticsOrderParam) {

        //查询当前物流订单
        VcLogisticsOrder vcLogisticsOrder = vcLogisticsOrderService.lambdaQuery().eq(VcLogisticsOrder::getId, logisticsOrderParam.getId()).one();
        if (vcLogisticsOrder == null || vcLogisticsOrder.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "物流订单不存在");
        }
        if (vcLogisticsOrder.getStatus() != 1) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "当前订单状态已取消，无法重复取消");
        }


        //请求微信取消订单接口
        LoOrderCancelParam loOrderCancelParam = new LoOrderCancelParam();
        loOrderCancelParam.setOpenid(vcLogisticsOrder.getOpenid());
        loOrderCancelParam.setDelivery_id(vcLogisticsOrder.getDeliveryId());
        loOrderCancelParam.setWaybill_id(vcLogisticsOrder.getWaybillId());
        loOrderCancelParam.setOrder_id(vcLogisticsOrder.getOrderNo());
        LoOrderCancelVo loOrderCancelVo = wxOpenLogisticsApiClient.orderCancel(vcLogisticsOrder.getWxAppid(), loOrderCancelParam);
        if (loOrderCancelVo == null || loOrderCancelVo.getErrcode() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "物流订单取消失败" +
                    (loOrderCancelVo == null ? "" : "，错误码：" + loOrderCancelVo.getErrcode() + "，错误信息：" + loOrderCancelVo.getErrmsg()));
        }

        //更新的当前物流订单状态为已取消
        vcLogisticsOrderService.lambdaUpdate().eq(VcLogisticsOrder::getId, vcLogisticsOrder.getId()).set(VcLogisticsOrder::getStatus, 0).update();
        //修改业务订单内的物流订单状态为已取消
        if (vcLogisticsOrder.getOrderType() == 1) {
            vcOrderOrderService.lambdaUpdate().eq(VcOrderOrder::getId, vcLogisticsOrder.getOrderId()).set(VcOrderOrder::getPickingLogistics, 0).update();
        } else {
            vcOrderOrderService.lambdaUpdate().eq(VcOrderOrder::getId, vcLogisticsOrder.getOrderId()).set(VcOrderOrder::getDeliveryLogistics, 0).update();
        }
        return CommonResult.success();
    }


    /**
     * 获取物流账号列表
     *
     * @param pageNum
     * @param pageSize
     * @param shopId
     * @return
     */
    @GetMapping("/list")
    @RequiresPermissions({"operateData:logistics:order:list"})
    @Transactional
    public CommonResult list(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, @RequestParam(defaultValue = "10", value = "pageSize") Integer pageSize,
                             @RequestParam(defaultValue = "", value = "shopId") Integer shopId, @RequestParam(defaultValue = "", value = "orderNo") String orderNo,
                             @RequestParam(defaultValue = "", value = "waybillId") String waybillId, @RequestParam(defaultValue = "", value = "status") Integer status) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (shopId == null) {
            shopId = loginUser.getShopId();
        }
        ShopRelativeId shopRelativeId = shopExtendService.getMaAppId(shopId);
        Page<LogisticsOrderVo> page = new Page<>(pageNum, pageSize);
        List<LogisticsOrderVo> list = vcLogisticsOrderMapper.adminSelectList(page, shopRelativeId.getAgentId(), shopId, orderNo, waybillId, status);
        for (LogisticsOrderVo logisticsOrderVo : list) {
            logisticsOrderVo.setCargoDetailList(JSONArray.parseArray(logisticsOrderVo.getCargoDetailList().toString()));
            logisticsOrderVo.setShopDetailList(JSONArray.parseArray(logisticsOrderVo.getShopDetailList().toString()));
        }
        return CommonResult.success(page.setRecords(list));
    }


    /**
     * 查询物流轨迹
     *
     * @param id
     * @return
     */
    @GetMapping("/get-path")
    @RequiresPermissions({"operateData:logistics:order:getPath"})
    @Transactional
    public CommonResult getPath(@RequestParam("id") Integer id) {
        //查询当前物流订单记录
        VcLogisticsOrder vcLogisticsOrder = vcLogisticsOrderService.lambdaQuery().eq(VcLogisticsOrder::getId, id).one();
        if (vcLogisticsOrder == null || vcLogisticsOrder.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "物流订单不存在");
        }
        if (vcLogisticsOrder.getStatus() != 1) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "当前订单状态已取消，无法查询物流轨迹信息");
        }


        LoPathGetParam loPathGetParam = new LoPathGetParam();
        loPathGetParam.setOpenid(vcLogisticsOrder.getOpenid());
        loPathGetParam.setDelivery_id(vcLogisticsOrder.getDeliveryId());
        loPathGetParam.setWaybill_id(vcLogisticsOrder.getWaybillId());
        LoPathGetVo loPathGetVo = wxOpenLogisticsApiClient.pathGet(vcLogisticsOrder.getWxAppid(), loPathGetParam);
        if (loPathGetVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "物流订单获取轨迹信息失败");
        }
        return CommonResult.success(loPathGetVo);
    }


    /**
     * 获取物流面单信息
     *
     * @param id
     * @return
     */
    @GetMapping("/get-order")
    @RequiresPermissions({"operateData:logistics:order:getOrder"})
    @Transactional
    public CommonResult getOrder(@RequestParam("id") Integer id) {
        //查询当前物流订单记录
        VcLogisticsOrder vcLogisticsOrder = vcLogisticsOrderService.lambdaQuery().eq(VcLogisticsOrder::getId, id).one();
        if (vcLogisticsOrder == null || vcLogisticsOrder.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "物流订单不存在");
        }


        LoOrderGetParam loOrderGetParam = new LoOrderGetParam();
        loOrderGetParam.setOrder_id(vcLogisticsOrder.getOrderNo());
        loOrderGetParam.setOpenid(vcLogisticsOrder.getOpenid());
        loOrderGetParam.setDelivery_id(vcLogisticsOrder.getDeliveryId());
        loOrderGetParam.setWaybill_id(vcLogisticsOrder.getWaybillId());
        loOrderGetParam.setPrint_type(0);
        loOrderGetParam.setCustom_remark(vcLogisticsOrder.getCustomRemark());
        LoOrderGetVo loOrderGetVo = wxOpenLogisticsApiClient.orderGet(vcLogisticsOrder.getWxAppid(), loOrderGetParam);
        if (loOrderGetVo == null || loOrderGetVo.getErrcode() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "物流面单信息获取失败" +
                    (loOrderGetVo == null ? "" : "，错误码：" + loOrderGetVo.getErrcode() + "，错误信息：" + loOrderGetVo.getErrmsg()));
        }
        return CommonResult.success(loOrderGetVo);
    }


    /**
     * 查询订单揽件物流信息
     *
     * @param orderId
     * @return
     */
    @GetMapping("/order-picking-info")
    @RequiresLogin
    @Transactional
    public CommonResult orderPickingInfo(@RequestParam("orderId") Integer orderId) {
        //查询当前物流订单记录
        LogisticsOrderVo logisticsOrderVo = vcLogisticsOrderMapper.selectByOrderIdAndType(orderId, 1);
        if (logisticsOrderVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "物流订单不存在");
        }

        return CommonResult.success(logisticsOrderVo);
    }


    /**
     * 保存物流订单信息
     *
     * @param loOrderAddParam
     * @param loOrderAddVo
     * @param vcOrderOrder
     * @param logisticsOrderParam
     * @return
     */
    private VcLogisticsOrder saveOrder(LoOrderAddParam loOrderAddParam, LoOrderAddVo loOrderAddVo, VcOrderOrder vcOrderOrder,
                                       LogisticsOrderParam logisticsOrderParam) {
        VcLogisticsOrder vcLogisticsOrder = new VcLogisticsOrder();
        vcLogisticsOrder.setAgentId(vcOrderOrder.getAgentId());
        vcLogisticsOrder.setShopId(vcOrderOrder.getShopId());
        vcLogisticsOrder.setOrderId(vcOrderOrder.getId());
        vcLogisticsOrder.setOrderType(logisticsOrderParam.getOrderType());
        vcLogisticsOrder.setOrderNo(vcOrderOrder.getOrderNo());
        vcLogisticsOrder.setWxOrderId(loOrderAddParam.getOrder_id());
        vcLogisticsOrder.setWaybillId(loOrderAddVo.getWaybill_id());
        vcLogisticsOrder.setOpenid(loOrderAddParam.getOpenid());
        vcLogisticsOrder.setDeliveryId(loOrderAddParam.getDelivery_id());
        vcLogisticsOrder.setDeliveryName(logisticsOrderParam.getDeliveryName());
        vcLogisticsOrder.setBizId(loOrderAddParam.getBiz_id());
        vcLogisticsOrder.setCustomRemark(loOrderAddParam.getCustom_remark());
        vcLogisticsOrder.setTagid(loOrderAddParam.getTagid());
        vcLogisticsOrder.setAddSource(loOrderAddParam.getAdd_source());
        vcLogisticsOrder.setWxAppid(loOrderAddParam.getWx_appid());
        vcLogisticsOrder.setSenderName(logisticsOrderParam.getSenderName());
        vcLogisticsOrder.setSenderTel(logisticsOrderParam.getSenderTel());
        vcLogisticsOrder.setSenderMobile(logisticsOrderParam.getSenderMobile());
        vcLogisticsOrder.setSenderCompany(logisticsOrderParam.getSenderCompany());
        vcLogisticsOrder.setSenderPostCode(logisticsOrderParam.getSenderPostCode());
        vcLogisticsOrder.setSenderCountry(logisticsOrderParam.getSenderCountry());
        vcLogisticsOrder.setSenderProvince(logisticsOrderParam.getSenderProvince());
        vcLogisticsOrder.setSenderProvinceCode(logisticsOrderParam.getSenderProvinceCode());
        vcLogisticsOrder.setSenderCity(logisticsOrderParam.getSenderCity());
        vcLogisticsOrder.setSenderCityCode(logisticsOrderParam.getSenderCityCode());
        vcLogisticsOrder.setSenderArea(logisticsOrderParam.getSenderArea());
        vcLogisticsOrder.setSenderAreaCode(logisticsOrderParam.getSenderAreaCode());
        vcLogisticsOrder.setSenderAddress(logisticsOrderParam.getSenderAddress());
        vcLogisticsOrder.setReceiverName(logisticsOrderParam.getReceiverName());
        vcLogisticsOrder.setReceiverTel(logisticsOrderParam.getReceiverTel());
        vcLogisticsOrder.setReceiverMobile(logisticsOrderParam.getReceiverMobile());
        vcLogisticsOrder.setReceiverCompany(logisticsOrderParam.getReceiverCompany());
        vcLogisticsOrder.setReceiverPostCode(logisticsOrderParam.getReceiverPostCode());
        vcLogisticsOrder.setReceiverCountry(logisticsOrderParam.getReceiverCountry());
        vcLogisticsOrder.setReceiverProvince(logisticsOrderParam.getReceiverProvince());
        vcLogisticsOrder.setReceiverProvinceCode(logisticsOrderParam.getReceiverProvinceCode());
        vcLogisticsOrder.setReceiverCity(logisticsOrderParam.getReceiverCity());
        vcLogisticsOrder.setReceiverCityCode(logisticsOrderParam.getReceiverCityCode());
        vcLogisticsOrder.setReceiverArea(logisticsOrderParam.getReceiverArea());
        vcLogisticsOrder.setReceiverAreaCode(logisticsOrderParam.getReceiverAreaCode());
        vcLogisticsOrder.setReceiverAddress(logisticsOrderParam.getReceiverAddress());
        vcLogisticsOrder.setCargoCount(logisticsOrderParam.getCargoCount());
        vcLogisticsOrder.setCargoWeight(logisticsOrderParam.getCargoWeight());
        vcLogisticsOrder.setCargoSpaceX(logisticsOrderParam.getCargoSpaceX());
        vcLogisticsOrder.setCargoSpaceY(logisticsOrderParam.getCargoSpaceY());
        vcLogisticsOrder.setCargoSpaceZ(logisticsOrderParam.getCargoSpaceZ());
        vcLogisticsOrder.setCargoDetailList(JSONObject.toJSONString(loOrderAddParam.getCargo().getDetail_list()));
        vcLogisticsOrder.setShopWxaPath(loOrderAddParam.getShop().getWxa_path());
        vcLogisticsOrder.setShopImgUrl(null);
        vcLogisticsOrder.setShopGoodsName(null);
        vcLogisticsOrder.setShopGoodsCount(null);
        vcLogisticsOrder.setShopDetailList(JSONObject.toJSONString(loOrderAddParam.getShop().getDetail_list()));
        vcLogisticsOrder.setUseInsured(logisticsOrderParam.getUseInsured());
        vcLogisticsOrder.setInsuredValue(logisticsOrderParam.getInsuredValue());
        vcLogisticsOrder.setServiceType(logisticsOrderParam.getServiceType());
        vcLogisticsOrder.setServiceName(logisticsOrderParam.getServiceName());
        vcLogisticsOrder.setExpectTime(logisticsOrderParam.getExpectTime());
        vcLogisticsOrder.setTakeMode(logisticsOrderParam.getTakeMode());
        vcLogisticsOrder.setStatus(1);
        vcLogisticsOrderService.save(vcLogisticsOrder);
        return vcLogisticsOrder;
    }


    /**
     * 创建物流订单
     *
     * @param logisticsOrderParam
     * @param vcOrderOrder
     * @return
     */
    private CreateLogisticsOrderParam createLogisticsOrder(LogisticsOrderParam logisticsOrderParam, VcOrderOrder vcOrderOrder) {
        ShopRelativeId shopRelativeId = shopExtendService.getMaAppId(vcOrderOrder.getShopId());


        //生成唯一订单id
        String wxOrderId = IdWorker.getIdStr();
        VcUserUser vcUserUser = vcUserUserService.lambdaQuery().eq(VcUserUser::getId, vcOrderOrder.getUserId()).one();

        LoOrderAddParam loOrderAddParam = new LoOrderAddParam();
        loOrderAddParam.setOrder_id(wxOrderId);
        loOrderAddParam.setOpenid(vcUserUser.getMaOpenid());
        loOrderAddParam.setDelivery_id(logisticsOrderParam.getDeliveryId());
        loOrderAddParam.setBiz_id(logisticsOrderParam.getBizId());
        loOrderAddParam.setCustom_remark(logisticsOrderParam.getCustomRemark());
        loOrderAddParam.setTagid(logisticsOrderParam.getTagid());
        loOrderAddParam.setAdd_source(0);
        loOrderAddParam.setWx_appid(shopRelativeId.getMaAppId());
        loOrderAddParam.setExpect_time(logisticsOrderParam.getExpectTime());
        loOrderAddParam.setTake_mode(logisticsOrderParam.getTakeMode());

        //给发件人信息赋值
        LoOrderAddParam.Sender sender = new LoOrderAddParam.Sender();
        sender.setName(logisticsOrderParam.getSenderName());
        sender.setTel(logisticsOrderParam.getSenderTel());
        sender.setMobile(logisticsOrderParam.getSenderMobile());
        sender.setCompany(logisticsOrderParam.getSenderCompany());
        sender.setPost_code(logisticsOrderParam.getSenderPostCode());
        sender.setCountry(logisticsOrderParam.getSenderCountry());
        sender.setProvince(logisticsOrderParam.getSenderProvince());
        sender.setCity(logisticsOrderParam.getSenderCity());
        sender.setArea(logisticsOrderParam.getSenderArea());
        sender.setAddress(logisticsOrderParam.getSenderAddress());
        loOrderAddParam.setSender(sender);

        //给收件人信息赋值
        LoOrderAddParam.Receiver receiver = new LoOrderAddParam.Receiver();
        receiver.setName(logisticsOrderParam.getReceiverName());
        receiver.setTel(logisticsOrderParam.getReceiverTel());
        receiver.setMobile(logisticsOrderParam.getReceiverMobile());
        receiver.setCompany(logisticsOrderParam.getReceiverCompany());
        receiver.setPost_code(logisticsOrderParam.getReceiverPostCode());
        receiver.setCountry(logisticsOrderParam.getReceiverCountry());
        receiver.setProvince(logisticsOrderParam.getReceiverProvince());
        receiver.setCity(logisticsOrderParam.getReceiverCity());
        receiver.setArea(logisticsOrderParam.getReceiverArea());
        receiver.setAddress(logisticsOrderParam.getReceiverAddress());
        loOrderAddParam.setReceiver(receiver);

        //设置包裹信息
        LoOrderAddParam.Cargo cargo = new LoOrderAddParam.Cargo();
        cargo.setCount(logisticsOrderParam.getCargoCount());
        cargo.setWeight(logisticsOrderParam.getCargoWeight());
        cargo.setSpace_x(logisticsOrderParam.getCargoSpaceX());
        cargo.setSpace_y(logisticsOrderParam.getCargoSpaceY());
        cargo.setSpace_z(logisticsOrderParam.getCargoSpaceZ());

        List<LogisticsOrderParam.CargoDetail> cargoDetailList = logisticsOrderParam.getCargoDetailList();
        List<LoOrderAddParam.Cargo.CargoDetail> cargo_detail_list = new ArrayList<>();
        for (LogisticsOrderParam.CargoDetail cargoDetail : cargoDetailList) {
            LoOrderAddParam.Cargo.CargoDetail cargoDetailVo = new LoOrderAddParam.Cargo.CargoDetail();
            cargoDetailVo.setName(cargoDetail.getName());
            cargoDetailVo.setCount(cargoDetail.getCount());
            cargo_detail_list.add(cargoDetailVo);
        }
        cargo.setDetail_list(cargo_detail_list);
        loOrderAddParam.setCargo(cargo);

        //设置物流商品信息
        LoOrderAddParam.Shop shop = new LoOrderAddParam.Shop();
        shop.setWxa_path(null);
        shop.setDetail_list(Lists.newArrayList());
        List<LogisticsOrderParam.ShopDetail> shopDetailList = logisticsOrderParam.getShopDetailList();
        List<LoOrderAddParam.Shop.ShopDetail> detail_list = new ArrayList<>();
        for (LogisticsOrderParam.ShopDetail shopDetail : shopDetailList) {
            LoOrderAddParam.Shop.ShopDetail shopDetailVo = new LoOrderAddParam.Shop.ShopDetail();
            shopDetailVo.setGoods_img_url(shopDetail.getShopImgUrl());
            shopDetailVo.setGoods_name(shopDetail.getShopGoodsName());
            shopDetailVo.setGoods_count(shopDetail.getShopGoodsCount());
            detail_list.add(shopDetailVo);
        }
        shop.setDetail_list(detail_list);
        loOrderAddParam.setShop(shop);

        //设置增值信息
        LoOrderAddParam.Insured insured = new LoOrderAddParam.Insured();
        insured.setUse_insured(logisticsOrderParam.getUseInsured());
        insured.setInsured_value(logisticsOrderParam.getInsuredValue());
        loOrderAddParam.setInsured(insured);

        //设置服务类型
        LoOrderAddParam.Service service = new LoOrderAddParam.Service();
        service.setService_type(logisticsOrderParam.getServiceType());
        service.setService_name(logisticsOrderParam.getServiceName());
        loOrderAddParam.setService(service);

        LoOrderAddVo loOrderAddVo = wxOpenLogisticsApiClient.orderAdd(shopRelativeId.getMaAppId(), loOrderAddParam);
        if (loOrderAddVo == null || loOrderAddVo.getErrcode() != 0 || loOrderAddVo.getDelivery_resultcode() != 0) {
            return new CreateLogisticsOrderParam(AjaxResult.error(HttpStatus.BAD_REQUEST, "物流订单创建失败" +
                    (loOrderAddVo == null ? "" : "，失败结果：" + JSONObject.toJSONString(loOrderAddVo))));
        }
        return new CreateLogisticsOrderParam(loOrderAddParam, loOrderAddVo);
    }


    /**
     * 判断订单状态是否有误
     *
     * @param logisticsOrderParam
     * @param vcOrderOrder
     * @return
     */
    private CommonResult checkParams(LogisticsOrderParam logisticsOrderParam, VcOrderOrder vcOrderOrder) {
        //只允许为门店和预约订单创建物流订单
        if (!OrderConstant.ORDER_TYPE_APPOINTMENT.equals(vcOrderOrder.getOrderType()) && !OrderConstant.ORDER_TYPE_STORE.equals(vcOrderOrder.getOrderType())) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "只有预约订单和门店订单允许创建物流订单");
        }

        //根据本次创建订单类型 判断当前订单是否允许本操作
        if (LogisticsConstant.PICKING_ORDER.equals(logisticsOrderParam.getOrderType())) {
            if (!OrderConstant.ORDER_INFO_STATUS_102.equals(vcOrderOrder.getStatus())) {
                return CommonResult.error(HttpStatus.BAD_REQUEST, "订单状态不允许本操作");
            }
            //判断当前订单是否已经有揽件物流订单
            if (vcOrderOrder.getPickingLogistics() != 0) {
                return CommonResult.error(HttpStatus.BAD_REQUEST, "订单已有物流揽件订单");
            }
        } else {
//            if (!(OrderConstant.ORDER_INFO_STATUS_201.equals(vcOrderOrder.getStatus()) && OrderConstant.ORDER_PAY_STATUS_PAY.equals(vcOrderOrder.getStatus()))) {
//                return CommonResult.error(HttpStatus.BAD_REQUEST, "订单状态不允许本操作");
//            }
            //判断当前订单是否已经有揽件物流订单
            if (vcOrderOrder.getDeliveryLogistics() != 0) {
                return CommonResult.error(HttpStatus.BAD_REQUEST, "订单已有物流派件订单");
            }
        }

        //判断选择的快递公司实付有误
        VcLogisticsDelivery vcLogisticsDelivery = vcLogisticsDeliveryService.lambdaQuery().eq(VcLogisticsDelivery::getIsDelete, 0)
                .eq(VcLogisticsDelivery::getDeliveryId, logisticsOrderParam.getDeliveryId()).one();
        if (vcLogisticsDelivery == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "快递公司不存在");
        }
        if (!vcLogisticsDelivery.getDeliveryName().equals(logisticsOrderParam.getDeliveryName())) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "快递公司名称有误");
        }

        //判断客户编码是否有误
        Long count = vcLogisticsAccountService.lambdaQuery().eq(VcLogisticsAccount::getIsDelete, 0).eq(VcLogisticsAccount::getShopId, vcOrderOrder.getShopId())
                .eq(VcLogisticsAccount::getBizId, logisticsOrderParam.getBizId()).count();
        if (count == 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "客户编码有误");
        }

        //验证收发件人联系电话是否有误
        if (StringUtils.isEmpty(logisticsOrderParam.getSenderTel()) && StringUtils.isEmpty(logisticsOrderParam.getSenderMobile())) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "发件人座机号码与电话号码不能为空");
        }
        if (StringUtils.isEmpty(logisticsOrderParam.getReceiverTel()) && StringUtils.isEmpty(logisticsOrderParam.getReceiverMobile())) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "收件人座机号码与电话号码不能为空");
        }

        return null;
    }


    /**
     * 添加用户地址
     *
     * @param userId
     * @param logisticsOrderParam
     */
    private void addUserAddress(Integer userId, LogisticsOrderParam logisticsOrderParam) {
        new Thread(() -> {
            //查询当前用户的全部地址信息
            List<String> list = vcUserReceivingAddressMapper.selectStrByUserId(userId);
            String complete = null;
            if (LogisticsConstant.PICKING_ORDER.equals(logisticsOrderParam.getOrderType())) {
                complete = logisticsOrderParam.getSenderName() + logisticsOrderParam.getSenderMobile() +
                        logisticsOrderParam.getSenderProvinceCode() + logisticsOrderParam.getSenderCityCode() +
                        logisticsOrderParam.getSenderAreaCode() + logisticsOrderParam.getSenderAddress();

                if (list.contains(complete)) {
                    return;
                }

                WinesReceiverAddress vcUserReceivingAddress = new WinesReceiverAddress();
                vcUserReceivingAddress.setUserId(userId);
                vcUserReceivingAddress.setUserName(logisticsOrderParam.getSenderName());
                vcUserReceivingAddress.setTelNumber(logisticsOrderParam.getSenderMobile());
                vcUserReceivingAddress.setProvinceName(logisticsOrderParam.getSenderProvince());
                vcUserReceivingAddress.setProvinceCode(logisticsOrderParam.getSenderProvinceCode());
                vcUserReceivingAddress.setCityName(logisticsOrderParam.getSenderCity());
                vcUserReceivingAddress.setCityCode(logisticsOrderParam.getSenderCityCode());
                vcUserReceivingAddress.setCountyName(logisticsOrderParam.getSenderArea());
                vcUserReceivingAddress.setCountyCode(logisticsOrderParam.getSenderAreaCode());
                vcUserReceivingAddress.setDetailInfo(logisticsOrderParam.getSenderAddress());
                vcUserReceivingAddress.setDetailInfoNew(logisticsOrderParam.getSenderAddress());
                vcUserReceivingAddress.setIsDefault(0);
                vcUserReceivingAddressService.save(vcUserReceivingAddress);
            } else {
                complete = logisticsOrderParam.getReceiverName() + logisticsOrderParam.getReceiverMobile() +
                        logisticsOrderParam.getReceiverProvinceCode() + logisticsOrderParam.getReceiverCityCode() +
                        logisticsOrderParam.getReceiverAreaCode() + logisticsOrderParam.getReceiverAddress();

                if (list.contains(complete)) {
                    return;
                }

                WinesReceiverAddress vcUserReceivingAddress = new WinesReceiverAddress();
                vcUserReceivingAddress.setUserId(userId);
                vcUserReceivingAddress.setUserName(logisticsOrderParam.getReceiverName());
                vcUserReceivingAddress.setTelNumber(logisticsOrderParam.getReceiverMobile());
                vcUserReceivingAddress.setProvinceName(logisticsOrderParam.getReceiverProvince());
                vcUserReceivingAddress.setProvinceCode(logisticsOrderParam.getReceiverProvinceCode());
                vcUserReceivingAddress.setCityName(logisticsOrderParam.getReceiverCity());
                vcUserReceivingAddress.setCityCode(logisticsOrderParam.getReceiverCityCode());
                vcUserReceivingAddress.setCountyName(logisticsOrderParam.getReceiverArea());
                vcUserReceivingAddress.setCountyCode(logisticsOrderParam.getReceiverAreaCode());
                vcUserReceivingAddress.setDetailInfo(logisticsOrderParam.getReceiverAddress());
                vcUserReceivingAddress.setDetailInfoNew(logisticsOrderParam.getSenderAddress());
                vcUserReceivingAddress.setIsDefault(0);
                vcUserReceivingAddressService.save(vcUserReceivingAddress);
            }
        }).start();
    }

}

package com.vchaoxi.service.impl;

import java.math.RoundingMode;
import java.util.Date;

import com.google.common.collect.Sets;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chargehub.common.core.constant.HttpStatus;
import com.chargehub.common.redis.service.RedisService;
import com.vchaoxi.constant.OrderConstant;
import com.vchaoxi.constant.RedisKeyConstant;
import com.vchaoxi.constant.SysConstant;
import com.vchaoxi.entity.*;
import com.vchaoxi.mapper.CommissionRecordMapper;
import com.vchaoxi.mapper.VcOrderInfoMapper;
import com.vchaoxi.mapper.VcOrderOrderMapper;
import com.vchaoxi.mapper.VcUserUserMapper;
import com.vchaoxi.param.OrderParam;
import com.vchaoxi.param.ZjsLogisticsOrderParam;
import com.vchaoxi.param.ZjsLogisticsParam;
import com.vchaoxi.service.*;
import com.vchaoxi.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单相关 - 订单信息 服务实现类
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VcOrderOrderServiceImpl extends ServiceImpl<VcOrderOrderMapper, VcOrderOrder> implements IVcOrderOrderService {


    @Autowired
    private IVcOrderInfoService vcOrderInfoService;
    @Autowired
    private IVcGoodsGoodsService vcGoodsGoodsService;
    @Autowired
    private IVcGoodsTypeService vcGoodsTypeService;
    @Autowired
    private IVcOrderPayRecordService vcOrderPayRecordService;
    @Autowired
    private IVcUserPayRecordService vcUserPayRecordService;
    @Autowired
    private IVcShopWxService vcUserPayRecord;

    @Autowired
    private IVcShopWxService vcShopWxService;
    @Autowired
    private RedisService redisUtils;
    @Autowired
    private IWinesCarService iWinesCarService;

    @Autowired
    private IWineUserRelationService iWineUserRelationService;
    @Autowired
    private IVcUserUserService iVcUserUserService;
    @Autowired
    private IWineBaseConfigService iWineBaseConfigService;
    @Autowired
    private IWinesGoodsSpecificationService iWinesGoodsSpecificationService;
    @Autowired
    private IWinesReceiverAddressService receiverAddressService;


    private final VcOrderOrderMapper vcOrderOrderMapper;
    private final VcOrderInfoMapper vcOrderInfoMapper;
    private final VcUserUserMapper vcUserUserMapper;

    @Autowired
    private CommissionRecordMapper commissionRecordMapper;

    /**
     * 创建订单
     *
     * @param orderParam
     * @return
     */
    @Override
    @Transactional
    public CommonResult createOrder(OrderParam orderParam) {
        VcGoodsGoods goodsGoods = vcGoodsGoodsService.getById(orderParam.getGoodsId());
        if (goodsGoods == null || goodsGoods.getIsDelete() != 0 || goodsGoods.getStatus() != 1) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "当前商品不存在或已下架");
        }
        WinesGoodsSpecification goodsSpecification = iWinesGoodsSpecificationService.lambdaQuery().eq(WinesGoodsSpecification::getIsDelete, 0).eq(WinesGoodsSpecification::getId, orderParam.getSpecificationId()).one();
        if (goodsSpecification == null || goodsSpecification.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "未查询到该规格的商品");
        }
        BigDecimal freight = goodsGoods.getFreight();
        VcGoodsType vcGoodsType = vcGoodsTypeService.lambdaQuery().eq(VcGoodsType::getId, goodsGoods.getType()).one();
        BigDecimal unitPrice = goodsSpecification.getAmount();
        if (orderParam.getCurLoginUser().getMemGrade() == 2) {
            unitPrice = goodsSpecification.getMemAmount();
        }
        if (orderParam.getCurLoginUser().getMemGrade() == 3) {
            unitPrice = goodsSpecification.getVipAmount();
        }
        if (orderParam.getCurLoginUser().getMemGrade() == 4) {
            freight = BigDecimal.ZERO;
        }
        if (unitPrice == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "该规格下价格不存在");
        }

        BigDecimal totalPrice = unitPrice.multiply(new BigDecimal(orderParam.getGoodsNum())).add(freight);


//        //添加订单信息
        VcOrderOrder vcOrderOrder = new VcOrderOrder();
        vcOrderOrder.setMemGrade(orderParam.getCurLoginUser().getMemGrade());
        vcOrderOrder.setAgentId(goodsGoods.getAgentId());
        vcOrderOrder.setShopId(goodsGoods.getShopId());
        vcOrderOrder.setOrderNo(IdWorker.getIdStr());
        vcOrderOrder.setUserId(orderParam.getCurLoginUser().getId());
        vcOrderOrder.setStatus(OrderConstant.ORDER_STATUS_NOT_PAY);
        vcOrderOrder.setReceiverAddressId(orderParam.getReceiveAddressId());
        vcOrderOrder.setAmount(totalPrice);
        vcOrderOrder.setFreight(freight);
        vcOrderOrder.setOrderType(orderParam.getOrderType());
        vcOrderOrder.setPickTimeRange(orderParam.getPickTimeRange());
        save(vcOrderOrder);

        //添加订单详情
        VcOrderInfo vcOrderInfo = new VcOrderInfo();
        vcOrderInfo.setAgentId(vcOrderOrder.getAgentId());
        vcOrderInfo.setShopId(vcOrderOrder.getShopId());
        vcOrderInfo.setOrderId(vcOrderOrder.getId());
        vcOrderInfo.setOrderNo(IdWorker.getIdStr());
        vcOrderInfo.setUserId(vcOrderOrder.getUserId());
        vcOrderInfo.setGoodsId(goodsGoods.getId());
        vcOrderInfo.setSpecifications(goodsSpecification.getValue());
        vcOrderInfo.setGoodsName(goodsGoods.getName());
        vcOrderInfo.setGoodsNo(goodsGoods.getGoodsNo());
        vcOrderInfo.setGoodsImg(goodsGoods.getImg());
        vcOrderInfo.setGoodsTypeId(vcGoodsType.getId());
        vcOrderInfo.setGoodsTypeName(vcGoodsType.getName());
        vcOrderInfo.setStatus(OrderConstant.ORDER_DETAIL_STATUS_NOT_PAY);
        vcOrderInfo.setAmount(totalPrice);
        vcOrderInfo.setUnitPrice(unitPrice);
        vcOrderInfo.setGoodsNum(orderParam.getGoodsNum());
        if (!CollectionUtils.isEmpty(orderParam.getUserImages())) {
            vcOrderInfo.setUserImages(String.join(",", orderParam.getUserImages()));
        }
        vcOrderInfoService.save(vcOrderInfo);


        //返回订单信息
        OrderVo orderVo = vcOrderOrderMapper.selectByOrderId(vcOrderOrder.getId());
        orderVo.setOrderInfoVos(vcOrderInfoMapper.selectByOrderId(vcOrderOrder.getId()));
        Map<String, Object> map = new HashMap<>();
        map.put("orderVo", orderVo);
        return CommonResult.success(map);
    }

    @Override
    @Transactional
    public CommonResult createOrderByCar(VcUserUser curLoginUser, Integer receiveAddressId, OrderParam orderParam) {
        String group = orderParam.getGroup();
        List<WinesCar> winesCarList = iWinesCarService.lambdaQuery()
                .eq(WinesCar::getIsDelete, 0)
                .eq(WinesCar::getChecked, 1)
                .eq(WinesCar::getUserId, curLoginUser.getId())
                .eq(StringUtils.isNotBlank(group), WinesCar::getGoodsGroup, group)
                .list();
        if (CollectionUtils.isEmpty(winesCarList)) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "至少选择一件商品购买");
        }
        boolean isFreeFreight = curLoginUser.getMemGrade() == 4;
        BigDecimal freight = BigDecimal.ZERO;

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<VcOrderInfo> orderInfoList = new ArrayList<>();
        for (WinesCar winesCar : winesCarList) {
            VcGoodsGoods goodsGoods = vcGoodsGoodsService.getById(winesCar.getGoodsId());
            if (goodsGoods == null || goodsGoods.getIsDelete() != 0 || goodsGoods.getStatus() != 1) {
                return CommonResult.error(HttpStatus.BAD_REQUEST, "您选择的商品不存在或已下架");
            }

            WinesGoodsSpecification goodsSpecification = iWinesGoodsSpecificationService.lambdaQuery()
                    .eq(WinesGoodsSpecification::getIsDelete, 0)
                    .eq(WinesGoodsSpecification::getId, winesCar.getSpecificationId()).one();
            if (goodsSpecification == null || goodsSpecification.getIsDelete() != 0) {
                return CommonResult.error(HttpStatus.BAD_REQUEST, "存在未查询到规格的商品");
            }


            VcGoodsType vcGoodsType = vcGoodsTypeService.lambdaQuery().eq(VcGoodsType::getId, goodsGoods.getType()).one();
            BigDecimal curCarGoodsPrice = winesCar.getPrice().multiply(new BigDecimal(winesCar.getNumber()));
            if (!isFreeFreight) {
                curCarGoodsPrice = curCarGoodsPrice.add(goodsGoods.getFreight());
                freight = freight.add(goodsGoods.getFreight());
            }
            totalPrice = totalPrice.add(curCarGoodsPrice);

            VcOrderInfo vcOrderInfo = new VcOrderInfo();
            vcOrderInfo.setAgentId(goodsGoods.getAgentId());
            vcOrderInfo.setShopId(goodsGoods.getShopId());
            vcOrderInfo.setOrderNo(IdWorker.getIdStr());
            vcOrderInfo.setUserId(curLoginUser.getId());
            vcOrderInfo.setGoodsId(goodsGoods.getId());
            vcOrderInfo.setGoodsNo(goodsGoods.getGoodsNo());
            vcOrderInfo.setGoodsName(goodsGoods.getName());
            vcOrderInfo.setSpecificationId(goodsSpecification.getId());
            vcOrderInfo.setSpecifications(goodsSpecification.getValue());
            vcOrderInfo.setGoodsImg(goodsGoods.getImg());
            vcOrderInfo.setGoodsTypeId(vcGoodsType.getId());
            vcOrderInfo.setGoodsTypeName(vcGoodsType.getName());
            vcOrderInfo.setStatus(OrderConstant.ORDER_DETAIL_STATUS_NOT_PAY);
            vcOrderInfo.setAmount(curCarGoodsPrice);
            vcOrderInfo.setUnitPrice(winesCar.getPrice());
            vcOrderInfo.setGoodsNum(winesCar.getNumber());
            orderInfoList.add(vcOrderInfo);
        }

        VcOrderInfo oldOrderInfo = orderInfoList.get(0);

        VcOrderOrder vcOrderOrder = new VcOrderOrder();
        vcOrderOrder.setAgentId(oldOrderInfo.getAgentId());
        vcOrderOrder.setShopId(oldOrderInfo.getShopId());
        vcOrderOrder.setOrderNo(IdWorker.getIdStr());
        vcOrderOrder.setUserId(curLoginUser.getId());
        vcOrderOrder.setMemGrade(curLoginUser.getMemGrade());
        vcOrderOrder.setFreight(freight);
        vcOrderOrder.setReceiverAddressId(receiveAddressId);
        vcOrderOrder.setStatus(OrderConstant.ORDER_STATUS_NOT_PAY);
        vcOrderOrder.setAmount(totalPrice);
        vcOrderOrder.setOrderType(orderParam.getOrderType());

        if (StringUtils.isNotBlank(orderParam.getPickTimeRange())) {
            vcOrderOrder.setPickTimeRange(orderParam.getPickTimeRange());
        }
        save(vcOrderOrder);
        for (VcOrderInfo orderInfo : orderInfoList) {
            orderInfo.setOrderId(vcOrderOrder.getId());
        }
        vcOrderInfoService.saveBatch(orderInfoList);


        for (WinesCar winesCar : winesCarList) {
            winesCar.setIsDelete(1);
        }
        iWinesCarService.updateBatchById(winesCarList);
        OrderVo orderVo = vcOrderOrderMapper.selectByOrderId(vcOrderOrder.getId());
        orderVo.setOrderInfoVos(vcOrderInfoMapper.selectByOrderId(vcOrderOrder.getId()));
        Map<String, Object> map = new HashMap<>();
        map.put("orderVo", orderVo);
        return CommonResult.success(map);
    }


    /**
     * 支付订单
     *
     * @param orderParam
     * @return
     */
    @Override
    public VcUserPayRecord payOrder(OrderParam orderParam) {
        VcOrderOrder vcOrderOrder = orderParam.getVcOrderOrder();

        //删除老的订单支付记录
        vcOrderPayRecordService.lambdaUpdate().eq(VcOrderPayRecord::getIsDelete, 0).eq(VcOrderPayRecord::getOrderId, vcOrderOrder.getId())
                .eq(VcOrderPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_NOT_PAY)
                .set(VcOrderPayRecord::getIsDelete, 1)
                .set(VcOrderPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_TIMEOUT).update();

        vcUserPayRecordService.lambdaUpdate().eq(VcUserPayRecord::getIsDelete, 0).eq(VcUserPayRecord::getRecordId, vcOrderOrder.getId())
                .eq(VcUserPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_NOT_PAY)
                .set(VcUserPayRecord::getIsDelete, 1)
                .set(VcUserPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_TIMEOUT).update();

        //添加订单支付记录
        VcOrderPayRecord vcOrderPayRecord = new VcOrderPayRecord();
        vcOrderPayRecord.setAgentId(vcOrderOrder.getAgentId());
        vcOrderPayRecord.setShopId(vcOrderOrder.getShopId());
        vcOrderPayRecord.setOrderId(vcOrderOrder.getId());
        vcOrderPayRecord.setRecordType(OrderConstant.ORDER_PAY_RECORD_TYPE_PATY);
        vcOrderPayRecord.setStatus(OrderConstant.ORDER_PAY_STATUS_NOT_PAY);
        vcOrderPayRecord.setAmount(vcOrderOrder.getAmount());
        vcOrderPayRecordService.save(vcOrderPayRecord);

        //查询商家配置
        VcShopWx vcShopWx = vcUserPayRecord.lambdaQuery().eq(VcShopWx::getIsDelete, 0).eq(VcShopWx::getShopId, vcOrderOrder.getShopId()).one();

        //添加用户支付记录
        VcUserPayRecord vcUserPayRecord = new VcUserPayRecord();
        vcUserPayRecord.setOrderNo(IdWorker.getIdStr());
        vcUserPayRecord.setUserId(vcOrderOrder.getUserId());
        vcUserPayRecord.setAgentId(vcOrderOrder.getAgentId());
        vcUserPayRecord.setShopId(vcOrderOrder.getShopId());
        vcUserPayRecord.setRecordType(OrderConstant.ORDER_PAY_RECORD_TYPE_PATY);
        vcUserPayRecord.setAmount(vcOrderOrder.getAmount());
        vcUserPayRecord.setStatus(OrderConstant.ORDER_PAY_STATUS_NOT_PAY);
        vcUserPayRecord.setMchId(vcShopWx.getMchId());
        vcUserPayRecord.setRecordId(vcOrderOrder.getId());
        vcUserPayRecordService.save(vcUserPayRecord);

        log.info("微信开始支付的vcShopWx：{} id:{},appId：{}", vcShopWx.getShopId(), vcShopWx.getId(), vcShopWx.getMaAppid());
        return vcUserPayRecord;
    }

    /**
     * 线下支付订单
     *
     * @param orderParam
     * @return
     */
    @Override
    @Transactional
    public CommonResult payOrderOffline(OrderParam orderParam) {
        VcOrderOrder vcOrderOrder = orderParam.getVcOrderOrder();

        //删除老的订单支付记录
        vcOrderPayRecordService.lambdaUpdate().eq(VcOrderPayRecord::getIsDelete, 0).eq(VcOrderPayRecord::getOrderId, vcOrderOrder.getId())
                .eq(VcOrderPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_NOT_PAY)
                .set(VcOrderPayRecord::getIsDelete, 1)
                .set(VcOrderPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_TIMEOUT).update();

        vcUserPayRecordService.lambdaUpdate().eq(VcUserPayRecord::getIsDelete, 0).eq(VcUserPayRecord::getRecordId, vcOrderOrder.getId())
                .eq(VcUserPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_NOT_PAY)
                .set(VcUserPayRecord::getIsDelete, 1)
                .set(VcUserPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_TIMEOUT).update();

        //添加订单支付记录
        VcOrderPayRecord vcOrderPayRecord = new VcOrderPayRecord();
        vcOrderPayRecord.setAgentId(vcOrderOrder.getAgentId());
        vcOrderPayRecord.setShopId(vcOrderOrder.getShopId());
        vcOrderPayRecord.setOrderId(vcOrderOrder.getId());
        vcOrderPayRecord.setRecordType(OrderConstant.ORDER_PAY_RECORD_TYPE_PATY);
        vcOrderPayRecord.setStatus(OrderConstant.ORDER_PAY_STATUS_PAY);
        vcOrderPayRecord.setAmount(vcOrderOrder.getAmount());
        vcOrderPayRecord.setPayWay(OrderConstant.PAY_WAY_OFFLINE);
        vcOrderPayRecordService.save(vcOrderPayRecord);

        //查询商家配置
        VcShopWx vcShopWx = vcUserPayRecord.lambdaQuery().eq(VcShopWx::getIsDelete, 0).eq(VcShopWx::getShopId, vcOrderOrder.getShopId()).one();

        //添加用户支付记录
        VcUserPayRecord vcUserPayRecord = new VcUserPayRecord();
        vcUserPayRecord.setOrderNo(IdWorker.getIdStr());
        vcUserPayRecord.setUserId(vcOrderOrder.getUserId());
        vcUserPayRecord.setAgentId(vcOrderOrder.getAgentId());
        vcUserPayRecord.setShopId(vcOrderOrder.getShopId());
        vcUserPayRecord.setRecordType(OrderConstant.ORDER_PAY_RECORD_TYPE_PATY);
        vcUserPayRecord.setAmount(vcOrderOrder.getAmount());
        vcUserPayRecord.setStatus(OrderConstant.ORDER_PAY_STATUS_PAY);
        vcUserPayRecord.setPayWay(OrderConstant.PAY_WAY_OFFLINE);
        vcUserPayRecord.setMchId(vcShopWx.getMchId());
        vcUserPayRecord.setRecordId(vcOrderOrder.getId());
        vcUserPayRecordService.save(vcUserPayRecord);
        //向Redis内存放支付成功的结果
        redisUtils.setCacheObject(RedisKeyConstant.RESULT_ORDER_PAY + vcUserPayRecord.getRecordId(), 1, 20L, TimeUnit.MINUTES);

        //修改订单为已支付,并且为设置为线下支付的订单
        lambdaUpdate().eq(VcOrderOrder::getId, vcOrderOrder.getId())
                .set(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_PAY)
                .set(VcOrderOrder::getPayWay, OrderConstant.PAY_WAY_OFFLINE)
                .set(VcOrderOrder::getPayTime, LocalDateTime.now()).update();

        vcOrderInfoService.lambdaUpdate().eq(VcOrderInfo::getIsDelete, 0).eq(VcOrderInfo::getOrderId, vcOrderOrder.getId())
                .set(VcOrderInfo::getStatus, OrderConstant.ORDER_DETAIL_STATUS_PAY).set(VcOrderInfo::getPayTime, LocalDateTime.now()).update();
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
            log.info("线下支付增加产品销量的操作orderId:{}", vcOrderOrder.getId());
        }
        return CommonResult.success();
    }

    /**
     * 退款
     *
     * @param orderParam
     * @return
     */
    @Override
    @Transactional
    public CommonResult refundOrder(OrderParam orderParam) {
        VcOrderOrder vcOrderOrder = orderParam.getVcOrderOrder();
        VcShopWx vcShopWx = vcUserPayRecord.lambdaQuery().eq(VcShopWx::getIsDelete, 0).eq(VcShopWx::getShopId, vcOrderOrder.getShopId()).one();

        //添加退款记录
        VcOrderPayRecord vcOrderPayRecord = new VcOrderPayRecord();
        vcOrderPayRecord.setAgentId(vcOrderOrder.getAgentId());
        vcOrderPayRecord.setShopId(vcOrderOrder.getShopId());
        vcOrderPayRecord.setOrderId(vcOrderOrder.getId());
        vcOrderPayRecord.setRecordType(OrderConstant.ORDER_PAY_RECORD_TYPE_REFUND);
        vcOrderPayRecord.setStatus(OrderConstant.ORDER_PAY_STATUS_RETURNING);
        vcOrderPayRecord.setAmount(vcOrderOrder.getAmount());//退款的金额
        vcOrderPayRecord.setGoodsNum(orderParam.getGoodsNum());
        vcOrderPayRecordService.save(vcOrderPayRecord);

        //添加用户退款记录
        VcUserPayRecord vcUserPayRecord = new VcUserPayRecord();
        vcUserPayRecord.setOrderNo(IdWorker.getIdStr());
        vcUserPayRecord.setUserId(vcOrderOrder.getUserId());
        vcUserPayRecord.setAgentId(vcOrderOrder.getAgentId());
        vcUserPayRecord.setShopId(vcOrderOrder.getShopId());
        vcUserPayRecord.setRecordType(OrderConstant.ORDER_PAY_RECORD_TYPE_REFUND);
        vcUserPayRecord.setAmount(vcOrderOrder.getAmount());
        vcUserPayRecord.setGoodsNum(orderParam.getGoodsNum());
        vcUserPayRecord.setStatus(OrderConstant.ORDER_PAY_STATUS_RETURNING);
        vcUserPayRecord.setMchId(vcShopWx.getMchId());
        vcUserPayRecord.setRecordId(vcOrderOrder.getId());
        vcUserPayRecordService.save(vcUserPayRecord);

        return CommonResult.success(vcUserPayRecord);
    }

    /**
     * 发货
     *
     * @param orderParam
     * @return
     */
    @Override
    @Transactional
    public CommonResult deliverGoods(OrderParam orderParam) {
        VcOrderOrder vcOrderOrder = this.getById(orderParam.getOrderId());
        vcOrderOrder.setTrackingNo(orderParam.getTrackingNo());
        vcOrderOrder.setDeliveryTime(LocalDateTime.now());
        vcOrderOrder.setStatus(OrderConstant.ORDER_STATUS_SHIPPED);//设置成已发货状态
        vcOrderOrder.setDeliveryStatus(OrderConstant.DELIVERY_STATUS_SHIPPED);//将物流状态设置为已发货
        this.updateById(vcOrderOrder);
        vcOrderInfoService.lambdaUpdate().eq(VcOrderInfo::getOrderId, orderParam.getOrderId())
                .set(VcOrderInfo::getStatus, OrderConstant.ORDER_DETAIL_STATUS_SHIPPED).update();

        //支付记录改为已发货状态
        vcOrderPayRecordService.lambdaUpdate().eq(VcOrderPayRecord::getIsDelete, 0).eq(VcOrderPayRecord::getRecordType, OrderConstant.ORDER_PAY_RECORD_TYPE_PATY)
                .eq(VcOrderPayRecord::getOrderId, vcOrderOrder.getId())
                .set(VcOrderPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_SHIPPED).update();

        vcUserPayRecordService.lambdaUpdate().eq(VcUserPayRecord::getIsDelete, 0).eq(VcUserPayRecord::getRecordType, OrderConstant.ORDER_PAY_RECORD_TYPE_PATY)
                .eq(VcUserPayRecord::getRecordId, vcOrderOrder.getId())
                .set(VcUserPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_SHIPPED).update();

//        OrderTimeoutParam orderTimeoutParam = new OrderTimeoutParam();
//        orderTimeoutParam.setType(2);
//        orderTimeoutParam.setOrderId(vcOrderOrder.getId());
//        orderTimeoutParam.setDuration(10080);//发货后7天自动设置为已完成收货
//        vcMqClient.orderTimeoutTask(orderTimeoutParam);

        OrderVo orderVo = vcOrderOrderMapper.selectByOrderId(vcOrderOrder.getId());
        orderVo.setOrderInfoVos(vcOrderInfoMapper.selectByOrderId(vcOrderOrder.getId()));
        /**
         * 通知微信已发货
         */
        if (orderVo.getPayWay() == 1) {//只有微信支付的时候才会告知微信发货
            uploadShippingInfo(orderParam.getOrderId());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("orderVo", orderVo);
        return CommonResult.success(map);
    }


    @Override
    @Transactional
    public Map<String, String> zjsDeliverNotyfy(HttpServletRequest httpServletRequest) {
        Map<String, String> outMap = new HashMap<>();
        try {
            String jsonResult = IOUtils.toString(httpServletRequest.getInputStream(), httpServletRequest.getCharacterEncoding());
            Map<String, String> map = JSON.parseObject(jsonResult, Map.class);
            String orderNo = map.get("orderNo");
            String trackingNo = map.get("trackingNo");
            log.info("收到宅急送的回调：{}", map);
            if (!StringUtils.isEmpty(orderNo) && !StringUtils.isEmpty(trackingNo)) {
                VcOrderOrder vcOrderOrder = this.lambdaQuery().eq(VcOrderOrder::getIsDelete, 0).eq(VcOrderOrder::getOrderNo, map.get("orderNo")).one();
                if (vcOrderOrder == null) {
                    outMap.put("status", "0001");
                    outMap.put("describe", "未查询到该订单");
                    return outMap;
                }
                if (!vcOrderOrder.getStatus().equals(OrderConstant.ORDER_STATUS_PAY)) {
                    outMap.put("status", "0001");
                    outMap.put("describe", "该订单尚未支付");
                    return outMap;
                }
                OrderParam orderParam = new OrderParam();
                orderParam.setOrderId(vcOrderOrder.getId());
                orderParam.setTrackingNo(trackingNo);
                //发货成功，设置订单号
                deliverGoods(orderParam);
                log.info("宅急送回调单号运单号设置成功：{}", map);
                outMap.put("status", "0000");
                outMap.put("describe", "成功");
                return outMap;
            } else {
                outMap.put("status", "0001");
                outMap.put("describe", "缺少orderNo或者trackingNo");
                return outMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            outMap.put("status", "0001");
            outMap.put("describe", e.getMessage());
            return outMap;
        }
    }


    /**
     * 微信支付订单回调通知
     *
     * @param httpServletRequest
     * @return
     */
    @Override
    @Transactional
    public String wxPayNotify(HttpServletRequest httpServletRequest) {
//        try {
//            String xmlResult = IOUtils.toString(httpServletRequest.getInputStream(), httpServletRequest.getCharacterEncoding());
//            Map<String, String> map = XmlAndMapUtil.xmlToMap(xmlResult);
//            log.info("微信支付返回参数：{}", map);
//
//            if (map == null) {
//                return WxPayNotifyResponseVo.success("支付失败通知");
//            }
//            VcUserPayRecord vcUserPayRecord = vcUserPayRecordService.lambdaQuery().eq(VcUserPayRecord::getIsDelete, 0).eq(VcUserPayRecord::getOrderNo, map.get("out_trade_no")).one();
//            if (vcUserPayRecord == null) {
//                log.error("当前订单支付记录不存在 orderNo=" + map.get("out_trade_no"));
//                return WxPayNotifyResponseVo.success("接收通知成功");
//            }
//
//            VcShopWx vcShopWx = vcShopWxService.lambdaQuery().eq(VcShopWx::getIsDelete, 0).eq(VcShopWx::getShopId, vcUserPayRecord.getShopId()).one();
//            WxPayOrderNotifyResult wxPayOrderNotifyResult = wxPayClient.payOrderNotifyResult(xmlResult, vcShopWx.getMchId(), "ma");
//
//            if (!"SUCCESS".equals(wxPayOrderNotifyResult.getReturnCode())) {
//                log.error(xmlResult);
//                return WxPayNotifyResponseVo.success("已接收发起支付失败通知");
//            }
//            if (!"SUCCESS".equals(wxPayOrderNotifyResult.getResultCode())) {
//                log.error(xmlResult);
//                return WxPayNotifyResponseVo.success("已接收用户支付失败通知");
//            }
//            log.info("接收到的支付成功通知内容：" + wxPayOrderNotifyResult);
//
//            String orderNo = wxPayOrderNotifyResult.getOutTradeNo();
//            String totalFee = BigDecimal.valueOf(Double.valueOf((double) wxPayOrderNotifyResult.getTotalFee()) / 100.0D).setScale(2, 4).toPlainString();
//
//            // 检查这个订单是否已经处理过
//            if (vcUserPayRecord.getStatus() != 0 && vcUserPayRecord.getStatus() != 3) {
//                log.error("当前订单已经处理过 orderNo=" + orderNo);
//                return WxPayNotifyResponseVo.success("接收通知成功");
//            }
//            if (!totalFee.equals(vcUserPayRecord.getAmount().toString())) {
//                log.error(vcUserPayRecord.getOrderNo() + " : 支付金额不符合 totalFee=" + totalFee);
//                return WxPayNotifyResponseVo.success("接收通知成功");
//            }
//
//            //修改订单为已支付
//            vcUserPayRecordService.lambdaUpdate().eq(VcUserPayRecord::getId, vcUserPayRecord.getId())
//                    .set(VcUserPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_PAY).update();
//
//            finishOrder(vcUserPayRecord);
//            return WxPayNotifyResponseVo.success("接收通知成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return WxPayNotifyResponseVo.fail(e.getMessage());
//        }
        return null;
    }


    @Override
    @Transactional
    public String wxRefundNotify(HttpServletRequest httpServletRequest) {
//        try {
//            String xmlResult = IOUtils.toString(httpServletRequest.getInputStream(), httpServletRequest.getCharacterEncoding());
//            Map<String, String> map = XmlAndMapUtil.xmlToMap(xmlResult);
//            log.info("微信退款返回参数：{}", map);
//            return "退款成功";
//        } catch (Exception e) {
//            return "退款失败";
//        }
        return null;
    }


    /**
     * 完成订单支付
     *
     * @param orderId
     */
    public void finishOrder(String orderId) {
        //启动新线程 ，处理订单业务逻辑
        VcUserPayRecord vcUserPayRecord = vcUserPayRecordService.lambdaQuery().eq(VcUserPayRecord::getIsDelete, 0).eq(VcUserPayRecord::getOrderNo, orderId).one();

        //向Redis内存放支付成功的结果
        redisUtils.setCacheObject(RedisKeyConstant.RESULT_ORDER_PAY + vcUserPayRecord.getRecordId(), 1, 20L, TimeUnit.MINUTES);

        VcOrderOrder vcOrderOrder = lambdaQuery().eq(VcOrderOrder::getId, vcUserPayRecord.getRecordId()).one();
        //修改订单为已支付
        lambdaUpdate().eq(VcOrderOrder::getId, vcOrderOrder.getId()).set(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_PAY)
                .set(VcOrderOrder::getPayTime, LocalDateTime.now()).update();

        vcOrderInfoService.lambdaUpdate().eq(VcOrderInfo::getIsDelete, 0).eq(VcOrderInfo::getOrderId, vcOrderOrder.getId())
                .set(VcOrderInfo::getStatus, OrderConstant.ORDER_DETAIL_STATUS_PAY).set(VcOrderInfo::getPayTime, LocalDateTime.now()).update();


        vcOrderPayRecordService.lambdaUpdate().eq(VcOrderPayRecord::getRecordType, OrderConstant.ORDER_PAY_RECORD_TYPE_PATY)
                .eq(VcOrderPayRecord::getOrderId, vcOrderOrder.getId()).eq(VcOrderPayRecord::getIsDelete, 0)
                .set(VcOrderPayRecord::getPayTime, LocalDateTime.now()).set(VcOrderPayRecord::getPayWay, OrderConstant.PAY_WAY_WECHAT)
                .set(VcOrderPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_PAY).update();

        //修改支付记录为已完成
        vcUserPayRecordService.lambdaUpdate().eq(VcUserPayRecord::getId, vcUserPayRecord.getId())
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
    @Transactional
    public CommonResult completeReceipt(Integer orderId, Integer handleType) {
        log.info("开始处理订单完成收货的操作orderId:{},处理方式：{}", orderId, handleType);
        VcOrderOrder vcOrderOrder = lambdaQuery().eq(VcOrderOrder::getId, orderId).one();
        if (vcOrderOrder == null || vcOrderOrder.getIsDelete() != 0) {
            log.info("【处理发货中的订单】当前订单不存在或已删除........");
            return CommonResult.error(HttpStatus.BAD_REQUEST, "当前订单不存在或已删除");
        }

        //判断当前订单是否为发货中的状态
        if (!vcOrderOrder.getStatus().equals(OrderConstant.ORDER_STATUS_SHIPPED)) {
            log.info("【处理发货中的订单】当前订单非发货中状态........");
            return CommonResult.error(HttpStatus.BAD_REQUEST, "当前订单非发货中状态");
        }
        Boolean boo = lambdaUpdate().eq(VcOrderOrder::getId, orderId)
                .eq(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_SHIPPED)
                .set(VcOrderOrder::getReceivingTime, LocalDateTime.now())
                .set(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_RECEIVED).update();
        //修改为已收货状态
        if (boo) {
            vcOrderInfoService.lambdaUpdate().eq(VcOrderInfo::getIsDelete, 0).eq(VcOrderInfo::getOrderId, orderId)
                    .set(VcOrderInfo::getStatus, OrderConstant.ORDER_DETAIL_STATUS_RECEIVED).update();

            //该笔订单的支付记录也设置成完成
            vcOrderPayRecordService.lambdaUpdate().eq(VcOrderPayRecord::getIsDelete, 0).eq(VcOrderPayRecord::getRecordType, OrderConstant.ORDER_PAY_RECORD_TYPE_PATY)
                    .eq(VcOrderPayRecord::getOrderId, vcOrderOrder.getId())
                    .set(VcOrderPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_RECEIVED).update();

            vcUserPayRecordService.lambdaUpdate().eq(VcUserPayRecord::getIsDelete, 0).eq(VcUserPayRecord::getRecordType, OrderConstant.ORDER_PAY_RECORD_TYPE_PATY)
                    .eq(VcUserPayRecord::getRecordId, vcOrderOrder.getId())
                    .set(VcUserPayRecord::getStatus, OrderConstant.ORDER_PAY_STATUS_RECEIVED).update();

        }
        log.info("结束处理订单完成收货的操作orderId:{},处理方式：{}", orderId, handleType);


        log.info("处理订单积分操作orderId:{},处理方式：{}", orderId, handleType);

        List<WineBaseConfig> wineBaseConfigs = iWineBaseConfigService.lambdaQuery().eq(WineBaseConfig::getIsDelete, 0).list();
        if (CollectionUtils.isEmpty(wineBaseConfigs)) {
            return CommonResult.success();
        }

        VcUserUser vcUserUser = this.iVcUserUserService.getById(vcOrderOrder.getUserId());
        Assert.notNull(vcUserUser, "用户信息不存在");
        if (StringUtils.isBlank(vcUserUser.getInviteLoginId())) {
            return CommonResult.success();
        }
        //判断有没有买过会员
        if (vcUserUser.getMemberExpireDate() == null) {
            return CommonResult.success();
        }
        //发放提成
        BigDecimal commissionRate = wineBaseConfigs.get(0).getCommissionRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal commissionAmount = vcOrderOrder.getAmount().multiply(commissionRate).setScale(2, RoundingMode.HALF_UP);
        CommissionRecord commissionRecord = new CommissionRecord();
        commissionRecord.setLoginId(vcUserUser.getInviteLoginId());
        commissionRecord.setSourceLoginId(vcUserUser.getLoginId());
        commissionRecord.setPhone(vcUserUser.getPhone());
        commissionRecord.setType(SysConstant.COMMISSION_RECORD_COMMISSION);
        commissionRecord.setAmount(commissionAmount);
        commissionRecord.setStatus(SysConstant.COMMISSION_RECORD_RECEIVED);
        commissionRecord.setOrderId(vcOrderOrder.getOrderNo());
        List<VcOrderInfo> list = this.vcOrderInfoService.lambdaQuery().eq(VcOrderInfo::getOrderId, vcOrderOrder.getId()).list();
        List<String> goodsInfo = list.stream().map(i -> i.getGoodsName() + "*" + i.getGoodsNum()).collect(Collectors.toList());
        commissionRecord.setGoodsInfo(goodsInfo);

        commissionRecordMapper.insert(commissionRecord);
        return CommonResult.success();
    }

    @Override
    public ZjsLogisticsInfoVo logisticsInfo(String mailNo) {
        ZjsLogisticsOrderParam logisticsOrderParam = new ZjsLogisticsOrderParam();
        List<ZjsLogisticsParam> orders = new ArrayList<>();
        ZjsLogisticsParam logisticsParam = new ZjsLogisticsParam();
        logisticsParam.setMailNo(mailNo);
        orders.add(logisticsParam);
        logisticsOrderParam.setOrders(orders);
//        ZjsLogisticsInfoVo logisticsInfoVo = zjsClient.logisticsInfo(logisticsOrderParam);
        return null;
    }


    /**
     * 微信端发货信息提交
     *
     * @return
     */
    @Override
    public WxDeliverUploadShippingResultVo uploadShippingInfo(Integer orderId) {

        VcUserPayRecord vcUserPayRecord = vcUserPayRecordService.lambdaQuery().eq(VcUserPayRecord::getIsDelete, 0).eq(VcUserPayRecord::getRecordType, OrderConstant.ORDER_PAY_RECORD_TYPE_PATY).eq(VcUserPayRecord::getRecordId, orderId).one();
        if (vcUserPayRecord == null) {
            log.error("当前订单支付记录不存在 orderId:{}", orderId);
            return null;
        }
        VcShopWx vcShopWx = vcShopWxService.lambdaQuery().eq(VcShopWx::getIsDelete, 0).eq(VcShopWx::getShopId, vcUserPayRecord.getShopId()).one();
        VcUserUser userUser = iVcUserUserService.getById(vcUserPayRecord.getUserId());

        List<OrderInfoVo> orderInfoVos = vcOrderInfoMapper.selectByOrderId(orderId);
        StringBuilder orderInfosBuilder = new StringBuilder();
        for (OrderInfoVo infoVo : orderInfoVos) {
            if (orderInfosBuilder.length() > 0) {
                orderInfosBuilder.append(", "); // 添加分隔符，例如逗号
            }
            orderInfosBuilder.append(infoVo.getGoodsName())
                    .append("*")
                    .append(infoVo.getGoodsNum().toString());
        }
        String orderInfos = orderInfosBuilder.toString();


        return null;
    }

    /**
     * 删除订单
     *
     * @param orderParam
     * @return
     */
    @Override
    public CommonResult delOrderById(OrderParam orderParam) {
        VcOrderOrder vcOrderOrder = vcOrderOrderMapper.selectOne(new LambdaQueryWrapper<VcOrderOrder>()
                .eq(VcOrderOrder::getIsDelete, 0)
                .eq(VcOrderOrder::getId, orderParam.getOrderId()));
        if (Objects.isNull(vcOrderOrder)) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "当前订单记录不存在");
        }

        if (OrderConstant.PAY_WAY_WECHAT.equals(vcOrderOrder.getPayWay()) && !OrderConstant.ORDER_STATUS_TIMEOUT.equals(vcOrderOrder.getStatus())) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "当前订单状态不允许删除");
        }

        if (OrderConstant.PAY_WAY_OFFLINE.equals(vcOrderOrder.getPayWay()) && vcOrderOrder.getDeliveryStatus() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "当前订单状态不允许删除");
        }
        vcOrderOrder.setIsDelete(1);
        vcOrderOrderMapper.updateById(vcOrderOrder);

        vcOrderInfoService.lambdaUpdate().eq(VcOrderInfo::getOrderId, orderParam.getOrderId())
                .set(VcOrderInfo::getIsDelete, 1)
                .update();

        return CommonResult.success();
    }
}

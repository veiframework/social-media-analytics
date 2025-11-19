package com.chargehub.payment.wechat;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chargehub.payment.*;
import com.chargehub.payment.bean.*;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Zhanghaowei
 * @date 2025/04/02 17:53
 */
@Slf4j
public class WechatPaymentCommandHandler implements PaymentCommandHandler {

    private final PaymentConfigManager paymentConfigManager;


    public WechatPaymentCommandHandler(PaymentConfigManager paymentConfigManager) {
        this.paymentConfigManager = paymentConfigManager;
    }


    @Override
    public PaymentResult createOrder(PaymentParam param, PaymentChannel paymentChannel) {
        WechatPaymentResult wechatPaymentResult = new WechatPaymentResult();
        WechatPaymentParam wechatPaymentParam = (WechatPaymentParam) param;
        String spbillCreateIp = wechatPaymentParam.getSpbillCreateIp();
        if (StringUtils.isBlank(spbillCreateIp)) {
            wechatPaymentParam.setSpbillCreateIp(IpUtils.getIpAddr());
        }
        BigDecimal multiply = new BigDecimal(wechatPaymentParam.getTotalFee()).movePointRight(NumberUtils.INTEGER_TWO);
        wechatPaymentParam.setTotalFee(multiply.toString());
        log.info(" [微信统一下单请求参数]: {}", wechatPaymentParam);
        try {
            String tradeType = wechatPaymentParam.getTradeType();
            Integer timeExpire = wechatPaymentParam.getTimeExpire();
            wechatPaymentResult.setTradeType(tradeType);
            wechatPaymentResult.setPaymentChannel(paymentChannel);
            WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = BeanUtil.copyProperties(wechatPaymentParam, WxPayUnifiedOrderRequest.class);
            if (timeExpire != null) {
                LocalDateTime localDateTime = LocalDateTime.now().plus(timeExpire, ChronoUnit.MINUTES);
                wxPayUnifiedOrderRequest.setTimeExpire(localDateTime.format(DatePattern.PURE_DATETIME_FORMATTER));
            }
            //创建订单信息
            WechatPaymentProperties config = paymentConfigManager.getConfig(WechatPaymentProperties.class, param.getExtendParam());
            WxPayService wxPayService = this.getWxPayService(tradeType, config);
            if (config.isProxyPayment()) {
                wxPayUnifiedOrderRequest.setSubOpenid(wechatPaymentParam.getOpenid());
                wxPayUnifiedOrderRequest.setOpenid(null);
            }
            Map<String, String> attach = new HashMap<>(16);
            attach.put(PaymentConstant.TRADE_TYPE, tradeType);
            wxPayUnifiedOrderRequest.setAttach(JSON.toJSONString(attach));
            wxPayUnifiedOrderRequest.setNotifyUrl(config.getNotifyUrl(config.getId(), param.getBusinessTypeCode()));
            wxPayUnifiedOrderRequest.setTimeStart(LocalDateTime.now().format(DatePattern.PURE_DATETIME_FORMATTER));
            if (timeExpire == null) {
                wxPayUnifiedOrderRequest.setTimeExpire(LocalDateTime.now().plusSeconds(config.getExpireSeconds()).format(DatePattern.PURE_DATETIME_FORMATTER));
            }
            Object result = wxPayService.createOrder(wxPayUnifiedOrderRequest);
            BeanUtil.copyProperties(result, wechatPaymentResult);
            //设置订单创建结果为成功
            wechatPaymentResult.setResult(true);
            wechatPaymentResult.setMsg(PaymentConstant.OK);
            wechatPaymentResult.setOutTradeNo(param.getOutTradeNo());
            wechatPaymentResult.setTotalFee(multiply);
            wechatPaymentResult.setExtendParam(param.getExtendParam());
        } catch (Exception e) {
            log.error(" [微信支付失败-{}]: {}", wechatPaymentParam.getOutTradeNo(), e.getMessage());
            //设置订单创建结果为失败
            wechatPaymentResult.setMsg(e.getMessage());
            wechatPaymentResult.setResult(false);
            wechatPaymentResult.setOutTradeNo(param.getOutTradeNo());
            wechatPaymentResult.setTotalFee(multiply);
            wechatPaymentResult.setExtendParam(param.getExtendParam());
        }
        log.info(" [微信创建订单返回值]: {}", wechatPaymentResult);
        return wechatPaymentResult;
    }


    @Override
    public PaymentRefundResult createRefund(PaymentRefundParam param, PaymentChannel paymentChannel) {
        log.info(" [微信退款请求参数]: {}", param);
        PaymentRefundResult paymentRefundResult = BeanUtil.copyProperties(param, PaymentRefundResult.class);
        try {
            String tradeType = param.getTradeType();
            WechatPaymentProperties config = paymentConfigManager.getConfig(WechatPaymentProperties.class, param.getExtendParams());
            WxPayService wxPayService = this.getWxPayService(tradeType, config);
            WxPayRefundRequest wxPayRefundRequest = BeanUtil.copyProperties(param, WxPayRefundRequest.class);
            wxPayRefundRequest.setNotifyUrl(config.getRefundNotifyUrl(config.getId(), param.getBusinessTypeCode()));
            WxPayRefundResult wxPayRefundResult = wxPayService.refund(wxPayRefundRequest);
            log.info(" [微信退款结果]: {}", wxPayRefundResult);

            //如果返回为成功则直接返回退款结果
            if (PaymentConstant.SUCCESS.equals(wxPayRefundResult.getResultCode())) {
                paymentRefundResult.setRefundResult(true);
                paymentRefundResult.setMessage(PaymentConstant.OK);
            } else {
                paymentRefundResult.setRefundResult(false);
                paymentRefundResult.setMessage(wxPayRefundResult.getReturnMsg());
            }
        } catch (WxPayException e) {
            log.error(" [微信退款失败-{}]: ", param.getOutRefundNo(), e);
            paymentRefundResult.setRefundResult(false);
            paymentRefundResult.setMessage(e.getMessage());
        }
        return paymentRefundResult;
    }

    @Override
    public String paymentNotify(PaymentNotifyParam param, PaymentChannel paymentChannel, Consumer<PaymentNotifyParam> consumer) {
        try {
            log.info(" [微信支付回调参数]: {}", param);
            String xml = (String) param.getRawData();
            WxPayOrderNotifyResult wxPayOrderNotifyResult = WxPayOrderNotifyResult.fromXML(xml);
            String attach = wxPayOrderNotifyResult.getAttach();
            JSONObject attachMap = JSON.parseObject(attach);
            WechatPaymentProperties config = paymentConfigManager.getConfig(WechatPaymentProperties.class, param.getPaymentConfigId());
            WxPayService wxPayService = this.getWxPayService(attachMap.getString(PaymentConstant.TRADE_TYPE), config);
            // 校验请求是否合法
            wxPayOrderNotifyResult.checkResult(wxPayService, wxPayService.getConfig().getSignType(), false);
            param.setOutTradeNo(wxPayOrderNotifyResult.getOutTradeNo());
            param.setTransactionId(wxPayOrderNotifyResult.getTransactionId());
            param.setSuccess(PaymentConstant.SUCCESS.equals(wxPayOrderNotifyResult.getResultCode()));
            param.setMessage(wxPayOrderNotifyResult.getResultCode());
            consumer.accept(param);
            return WxPayNotifyResponse.success(PaymentConstant.SUCCESS);
        } catch (Exception e) {
            log.error(" [微信支付回调失败-{}]: {}", param, e.getMessage());
            param.setMessage(e.getMessage());
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }

    @Override
    public String refundNotify(PaymentRefundNotifyParam param, PaymentChannel paymentChannel, Consumer<PaymentRefundNotifyParam> consumer) {
        try {
            log.info(" [微信退款回调参数]: {}", param);
            String xml = (String) param.getRawData();
            WechatPaymentProperties config = this.paymentConfigManager.getConfig(WechatPaymentProperties.class, param.getPaymentConfigId());
            WxPayService wxPayService = this.getWxPayService(null, config);
            WxPayRefundNotifyResult result = wxPayService.parseRefundNotifyResult(xml);
            WxPayRefundNotifyResult.ReqInfo reqInfo = result.getReqInfo();
            Assert.notNull(reqInfo, "refund notify content can not be null");
            param.setOutTradeNo(reqInfo.getOutTradeNo());
            param.setOutRefundNo(reqInfo.getOutRefundNo());
            param.setTransactionId(reqInfo.getTransactionId());
            param.setSuccess(PaymentConstant.SUCCESS.equals(reqInfo.getRefundStatus()));
            param.setMessage(reqInfo.getRefundStatus());
            consumer.accept(param);
            return WxPayNotifyResponse.success(PaymentConstant.SUCCESS);
        } catch (Exception e) {
            log.error(" [微信退款回调失败-{}]: {}", param, e.getMessage());
            param.setMessage(e.getMessage());
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }

    @Override
    public PaymentChannel channel() {
        return PaymentChannel.WECHAT;
    }


    public WxPayService getWxPayService(String tradeType, WechatPaymentProperties config) {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(WxPayConstants.TradeType.MWEB.equals(tradeType) ? config.getMpAppId() : config.getAppId());
        payConfig.setMchId(config.getMchId());
        payConfig.setMchKey(config.getMchKey());
        byte[] decodeKeyBytes = Base64.decode(config.getKeyContent());
        payConfig.setKeyContent(decodeKeyBytes);
        if (config.isProxyPayment()) {
            payConfig.setSubAppId(config.getSubAppId());
            payConfig.setSubMchId(config.getSubMchId());
        }
        WxPayService payService = new WxPayServiceImpl();
        payService.setConfig(payConfig);
        return payService;
    }


}

package com.chargehub.payment.alipay;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeCreateModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.chargehub.payment.*;
import com.chargehub.payment.bean.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


/**
 * @author Zhanghaowei
 * @date 2025/04/03 14:58
 */
@Slf4j
public class AliPaymentCommandHandler implements PaymentCommandHandler {

    private final PaymentConfigManager paymentConfigManager;


    public AliPaymentCommandHandler(PaymentConfigManager paymentConfigManager) {
        this.paymentConfigManager = paymentConfigManager;
    }


    @Override
    public PaymentResult createOrder(PaymentParam param, PaymentChannel paymentChannel) {
        AliPaymentParam aliPaymentParam = (AliPaymentParam) param;
        AliPaymentResult aliPaymentResult = new AliPaymentResult();
        BigDecimal multiply = new BigDecimal(aliPaymentParam.getTotalAmount()).movePointRight(NumberUtils.INTEGER_TWO);
        log.info(" [支付宝统一下单请求参数]: {}", aliPaymentParam);
        try {
            aliPaymentResult.setPaymentChannel(paymentChannel);
            aliPaymentResult.setTradeType("JSAPI_PAY");
            AliPaymentProperties alipayConfig = this.paymentConfigManager.getConfig(AliPaymentProperties.class, param.getExtendParam());
            AlipayClient alipayClient = this.alipayClient(alipayConfig);
            AlipayTradeCreateModel model = BeanUtil.copyProperties(aliPaymentParam, AlipayTradeCreateModel.class);
            AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
            if (alipayConfig.isProxyPayment()) {
                model.setSellerId(alipayConfig.getSellerId());
                request.putOtherTextParam(AlipayConstants.APP_AUTH_TOKEN, aliPaymentParam.getAppAuthToken());
            }
            model.setTimeExpire(LocalDateTime.now().plusSeconds(alipayConfig.getExpireSeconds()).format(DatePattern.NORM_DATETIME_FORMATTER));
            request.setNotifyUrl(alipayConfig.getNotifyUrl(alipayConfig.getId(), param.getBusinessTypeCode()));
            request.setBizModel(model);

            log.info(" [支付宝创建订单请求参数]: {}", JSONObject.toJSONString(request));
            AlipayTradeCreateResponse response = alipayClient.execute(request);
            log.info(" [支付宝创建订单返回结果]: {}", JSONObject.toJSONString(response));
            if (response.isSuccess()) {
                aliPaymentResult.setResult(true);
                aliPaymentResult.setMsg(PaymentConstant.OK);
                aliPaymentResult.setTotalFee(multiply);
                aliPaymentResult.setOutTradeNo(response.getOutTradeNo());
                aliPaymentResult.setTradeNo(response.getTradeNo());
                aliPaymentResult.setExtendParam(param.getExtendParam());
            } else {
                aliPaymentResult.setResult(false);
                aliPaymentResult.setTotalFee(multiply);
                aliPaymentResult.setOutTradeNo(param.getOutTradeNo());
                aliPaymentResult.setMsg(response.getMsg());
                aliPaymentResult.setExtendParam(param.getExtendParam());
            }
        } catch (Exception e) {
            log.error(" [支付宝支付失败-{}]: {}", aliPaymentParam.getOutTradeNo(), e.getMessage());
            aliPaymentResult.setResult(false);
            aliPaymentResult.setOutTradeNo(param.getOutTradeNo());
            aliPaymentResult.setTotalFee(multiply);
            aliPaymentResult.setMsg(e.getMessage());
            aliPaymentResult.setExtendParam(param.getExtendParam());
        }
        log.info(" [支付宝创建订单返回值]: {}", aliPaymentResult);
        return aliPaymentResult;
    }

    @Override
    public PaymentRefundResult createRefund(PaymentRefundParam param, PaymentChannel paymentChannel) {
        log.info(" [支付宝退款请求参数]: {}", param);
        AliPaymentRefundParam aliPaymentRefundParam = (AliPaymentRefundParam) param;
        String refundFee = param.getRefundFee();
        PaymentRefundResult paymentRefundResult = BeanUtil.copyProperties(aliPaymentRefundParam, PaymentRefundResult.class);
        try {
            AliPaymentProperties alipayConfig = this.paymentConfigManager.getConfig(AliPaymentProperties.class, aliPaymentRefundParam.getExtendParams());
            param.getExtendParams().put(PaymentConstant.PAYMENT_CONFIG_ID, alipayConfig.getId());
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(aliPaymentRefundParam.getOutTradeNo());
            model.setRefundAmount(new BigDecimal(refundFee).movePointLeft(NumberUtils.INTEGER_TWO).toString());
            model.setOutRequestNo(aliPaymentRefundParam.getOutRefundNo());
            model.setRefundReason(aliPaymentRefundParam.getReason());
            if (StringUtils.isNotBlank(aliPaymentRefundParam.getTransactionId())) {
                model.setTradeNo(aliPaymentRefundParam.getTransactionId());
            }
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            request.setBizModel(model);
            if (alipayConfig.isProxyPayment()) {
                request.putOtherTextParam(AlipayConstants.APP_AUTH_TOKEN, aliPaymentRefundParam.getAppAuthToken());
            }
            AlipayClient alipayClient = this.alipayClient(alipayConfig);
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            log.info(" [支付宝退款返回结果]: {}", JSONObject.toJSONString(response));
            if (response.isSuccess() && PaymentConstant.Y.equals(response.getFundChange())) {
                paymentRefundResult.setRefundResult(true);
                paymentRefundResult.setMessage(PaymentConstant.OK);
                paymentRefundResult.setTransactionId(response.getTradeNo());
                paymentRefundResult.setOutTradeNo(response.getOutTradeNo());
                param.setTransactionId(response.getTradeNo());
                param.setOutTradeNo(response.getOutTradeNo());
                param.getExtendParams().put(PaymentConstant.TOTAL_REFUND_FEE, response.getRefundFee());
            } else {
                paymentRefundResult.setRefundResult(false);
                paymentRefundResult.setMessage(response.getMsg());
            }
        } catch (Exception e) {
            log.error(" [支付宝退款失败-{}]: {}", param.getOutRefundNo(), e.getMessage());
            paymentRefundResult.setRefundResult(false);
            paymentRefundResult.setMessage(e.getMessage());
        }
        return paymentRefundResult;
    }


    @SuppressWarnings("unchecked")
    @Override
    public String paymentNotify(PaymentNotifyParam param, PaymentChannel paymentChannel, Consumer<PaymentNotifyParam> consumer) {
        try {
            log.info(" [支付宝支付回调参数]: {}", param);
            Map<String, String> rawData = (Map<String, String>) param.getRawData();
            AliPaymentProperties config = paymentConfigManager.getConfig(AliPaymentProperties.class, param.getPaymentConfigId());
            boolean isSign = AlipaySignature.rsaCheckV1(rawData, config.getAlipayPublicKey(), config.getCharset(), config.getSignType());
            Assert.isTrue(isSign, "aliPayment notify sign failed!");
            param.setOutTradeNo(rawData.get(PaymentConstant.OUT_TRADE_NO));
            param.setTransactionId(rawData.get(PaymentConstant.TRADE_NO));
            param.setSuccess(true);
            param.setMessage(PaymentConstant.OK);
            consumer.accept(param);
            return PaymentConstant.SUCCESS.toLowerCase();
        } catch (Exception e) {
            log.error(" [支付宝支付回调失败-{}]: {}", param, e.getMessage());
            param.setMessage(e.getMessage());
            return PaymentConstant.FAIL.toLowerCase();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public String refundNotify(PaymentRefundNotifyParam param, PaymentChannel paymentChannel, Consumer<PaymentRefundNotifyParam> consumer) {
        log.info(" [支付宝退款回调参数]: {}", param);
        Map<String, String> rawData = (Map<String, String>) param.getRawData();
        param.setOutTradeNo(rawData.get(PaymentConstant.OUT_TRADE_NO));
        param.setTransactionId(rawData.get(PaymentConstant.TRADE_NO));
        param.setOutRefundNo(rawData.get(PaymentConstant.OUT_BIZ_NO));
        try {
            if (!rawData.containsKey(PaymentConstant.COMPLETE_REFUND)) {
                AliPaymentProperties config = paymentConfigManager.getConfig(AliPaymentProperties.class, param.getPaymentConfigId());
                boolean isSign = AlipaySignature.rsaCheckV1(rawData, config.getAlipayPublicKey(), config.getCharset(), config.getSignType());
                Assert.isTrue(isSign, "aliPayment notify sign failed!");
            }
            param.setSuccess(PaymentConstant.TRADE_SUCCESS.equals(rawData.getOrDefault(PaymentConstant.TRADE_STATUS, "")));
            param.setMessage(PaymentConstant.OK);
            consumer.accept(param);
            return PaymentConstant.SUCCESS.toLowerCase();
        } catch (Exception e) {
            log.error(" [支付宝退款回调失败-{}]: {}", param, e.getMessage());
            param.setMessage(e.getMessage());
            return PaymentConstant.FAIL.toLowerCase();
        }
    }


    @Override
    public PaymentChannel channel() {
        return PaymentChannel.ALIPAY;
    }

    public DefaultAlipayClient alipayClient(AliPaymentProperties alipayConfig) {
        return new DefaultAlipayClient(alipayConfig.getServerUrl(),
                alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(),
                alipayConfig.getFormat(),
                alipayConfig.getCharset(),
                alipayConfig.getAlipayPublicKey(),
                alipayConfig.getSignType());
    }

    public static void syncRefundNotify(PaymentRefundParam param,
                                        PaymentRefundResult refundResult,
                                        PaymentCommand paymentCommand) {
        boolean refundSuccess = BooleanUtils.isTrue(refundResult.getRefundResult());
        if (!refundSuccess) {
            return;
        }
        BigDecimal totalFee = new BigDecimal(param.getTotalFee());
        BigDecimal refundFee = new BigDecimal(param.getRefundFee());
        String totalRefundFeeStr = param.getExtendParams().get(PaymentConstant.TOTAL_REFUND_FEE);
        BigDecimal totalRefundFee = new BigDecimal(totalRefundFeeStr).movePointRight(NumberUtils.INTEGER_TWO);
        boolean fullMoneyRefund = totalFee.compareTo(refundFee) == 0;
        boolean partRefundComplete = totalFee.compareTo(totalRefundFee) == 0;
        //如果不是全额退款,或者部分退款完结, 否则不执行
        if (!fullMoneyRefund && !partRefundComplete) {
            return;
        }
        //全额退款 或者 部分退款等于总金额时触发立即回调通知
        PaymentRefundNotifyParam refundNotifyParam = new PaymentRefundNotifyParam();
        String businessTypeCode = param.getBusinessTypeCode();
        String configId = param.getExtendParams().get(PaymentConstant.PAYMENT_CONFIG_ID);
        refundNotifyParam.setBusinessTypeCode(businessTypeCode);
        refundNotifyParam.setPaymentConfigId(configId);
        Map<String, String> map = new HashMap<>(16);
        map.put(PaymentConstant.OUT_TRADE_NO, param.getOutTradeNo());
        map.put(PaymentConstant.TRADE_NO, param.getTransactionId());
        map.put(PaymentConstant.OUT_BIZ_NO, param.getOutRefundNo());
        map.put(PaymentConstant.COMPLETE_REFUND, PaymentConstant.TRADE_SUCCESS);
        map.put(PaymentConstant.TRADE_STATUS, PaymentConstant.TRADE_SUCCESS);
        refundNotifyParam.setRawData(map);
        paymentCommand.refundNotify(refundNotifyParam, PaymentChannel.ALIPAY);
    }

}

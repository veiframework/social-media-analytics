package com.chargehub.payment.allinpay;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chargehub.payment.*;
import com.chargehub.payment.bean.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;


/**
 * @author Zhanghaowei
 * @date 2025/04/03 14:58
 */
@Slf4j
public class AllInPaymentCommandHandler implements PaymentCommandHandler {

    private final PaymentConfigManager paymentConfigManager;

    public AllInPaymentCommandHandler(PaymentConfigManager paymentConfigManager) {
        this.paymentConfigManager = paymentConfigManager;
    }


    @Override
    public PaymentResult createOrder(PaymentParam param, PaymentChannel paymentChannel) {
        AllInPaymentParam allInPaymentParam = (AllInPaymentParam) param;
        BigDecimal multiply = new BigDecimal(allInPaymentParam.getTotalFee()).movePointRight(NumberUtils.INTEGER_TWO);
        allInPaymentParam.setTotalFee(multiply.toString());
        AllInPaymentProperties config = paymentConfigManager.getConfig(AllInPaymentProperties.class, param.getExtendParam());
        log.info(" [通联统一下单请求参数]: {}", allInPaymentParam);
        try {
            TreeMap<String, String> params = new TreeMap<>();
            params.put("cusid", config.getCusId());
            params.put("appid", config.getAppId());
            params.put("version", config.getVersion());
            params.put("sub_appid", config.getSubAppId());
            params.put("signtype", config.getSignType());
            params.put("trxamt", allInPaymentParam.getTotalFee());
            params.put("reqsn", allInPaymentParam.getOutTradeNo());
            params.put("paytype", allInPaymentParam.getTradeType());
            params.put("expiretime", LocalDateTime.now().plusSeconds(config.getExpireSeconds()).format(DatePattern.PURE_DATETIME_FORMATTER));
            params.put("randomstr", AllInSignUtil.getValidatecode(8));
            if (StringUtils.hasText(allInPaymentParam.getOpenid())) {
                params.put("acct", allInPaymentParam.getOpenid());
            }
            params.put("notify_url", config.getNotifyUrl(config.getId(), param.getBusinessTypeCode()));
            params.put("body", allInPaymentParam.getBody());
            params.put("sign", AllInSignUtil.unionSign(params, config.getMd5AppKey(), config.getSignType()));

            //调用通联统一下单接口 创建订单
            String result = HttpUtil.createPost(config.getApiUrl() + "/pay").formStr(params).execute().body();

            log.info(" [通联统一下单接口返回值]: {}", result);
            JSONObject resultMap = AllInSignUtil.validSign(result, config.getMd5AppKey(), config.getSignType());

            //TODO 尚未实现代理商支付方式
            if ("W06".equals(allInPaymentParam.getTradeType()) || "W02".equals(allInPaymentParam.getTradeType())) {
                JSONObject payInfoMap = JSON.parseObject(resultMap.getString("payinfo"));
                AllInPaymentResult paymentResult = payInfoMap.toJavaObject(AllInPaymentResult.class);
                paymentResult.setMwebUrl(null);
                paymentResult.setCodeUrl(null);
                paymentResult.setResult(true);
                paymentResult.setPackageValue(payInfoMap.getString("package"));
                paymentResult.setOutTradeNo(param.getOutTradeNo());
                paymentResult.setTotalFee(multiply);
                paymentResult.setExtendParam(param.getExtendParam());
                paymentResult.setMsg(PaymentConstant.OK);
                paymentResult.setPaymentChannel(PaymentChannel.WECHAT);
                paymentResult.setTradeType(allInPaymentParam.getTradeType());
                return paymentResult;
            }
            if ("A02".equals(allInPaymentParam.getTradeType())) {
                AllInPaymentResult paymentResult = new AllInPaymentResult();
                paymentResult.setResult(true);
                paymentResult.setMsg(PaymentConstant.OK);
                paymentResult.setOutTradeNo(allInPaymentParam.getOutTradeNo());
                paymentResult.setTradeNo(resultMap.getString("payinfo"));
                paymentResult.setTotalFee(multiply);
                paymentResult.setExtendParam(param.getExtendParam());
                paymentResult.setPaymentChannel(PaymentChannel.ALIPAY);
                paymentResult.setTradeType(allInPaymentParam.getTradeType());
                return paymentResult;
            }
            throw new IllegalArgumentException("allin payment failed, not support trade type " + allInPaymentParam.getTradeType());
        } catch (Exception e) {
            log.error(" [通联支付失败-{}]: {}", allInPaymentParam.getOutTradeNo(), e.getMessage());
            AllInPaymentResult paymentResult = new AllInPaymentResult();
            paymentResult.setResult(false);
            paymentResult.setMsg(e.getMessage());
            paymentResult.setOutTradeNo(allInPaymentParam.getOutTradeNo());
            paymentResult.setTotalFee(multiply);
            paymentResult.setExtendParam(param.getExtendParam());
            return paymentResult;
        }
    }

    @Override
    public PaymentRefundResult createRefund(PaymentRefundParam param, PaymentChannel paymentChannel) {
        log.info(" [通联退款请求参数]: {}", param);
        PaymentRefundResult payRefundVo = BeanUtil.copyProperties(param, PaymentRefundResult.class);
        try {
            AllInPaymentProperties allinPayConf = paymentConfigManager.getConfig(AllInPaymentProperties.class, param.getExtendParams());
            param.getExtendParams().put(PaymentConstant.PAYMENT_CONFIG_ID, allinPayConf.getId());
            TreeMap<String, String> params = new TreeMap<>();
            params.put("cusid", allinPayConf.getCusId());
            params.put("appid", allinPayConf.getAppId());
            params.put("version", allinPayConf.getVersion());
            params.put("trxamt", param.getRefundFee());
            params.put("reqsn", param.getOutRefundNo());
            params.put("oldreqsn", param.getOutTradeNo());
            if (StringUtils.hasText(param.getTransactionId())) {
                params.put("oldtrxid", param.getTransactionId());
            }
            params.put("randomstr", AllInSignUtil.getValidatecode(8));
            params.put("signtype", allinPayConf.getSignType());
            // 暂时不支持回调通知, 设置回调地址会报错提示sign错误
//            params.put("notify_url", allinPayConf.getRefundNotifyUrl(allinPayConf.getId(), param.getBusinessTypeCode()));
            params.put("sign", AllInSignUtil.unionSign(params, allinPayConf.getMd5AppKey(), allinPayConf.getSignType()));

            String result = HttpUtil.createPost(allinPayConf.getApiUrl() + "/refund").formStr(params).execute().body();

            //TODO 尚未实现代理商退款
            log.info(" [通联退款接口返回值]: {}", result);
            JSONObject resultMap = AllInSignUtil.validSign(result, allinPayConf.getMd5AppKey(), allinPayConf.getSignType());

            if ("0000".equals(resultMap.get(PaymentConstant.TRX_STATUS))) {
                payRefundVo.setRefundResult(true);
                payRefundVo.setMessage(PaymentConstant.OK);
                String trxId = resultMap.getString(PaymentConstant.TRX_ID);
                payRefundVo.setTransactionId(trxId);
                param.setTransactionId(trxId);
            } else {
                payRefundVo.setRefundResult(false);
                payRefundVo.setMessage(result);
            }
            return payRefundVo;
        } catch (Exception e) {
            log.error(" [通联退款失败-{}]: {}", param.getOutRefundNo(), e.getMessage());
            payRefundVo.setRefundResult(false);
            payRefundVo.setMessage(e.getMessage());
            return payRefundVo;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public String paymentNotify(PaymentNotifyParam param, PaymentChannel paymentChannel, Consumer<PaymentNotifyParam> consumer) {
        log.info(" [通联支付回调参数]: {}", param);
        TreeMap<String, String> rawData = new TreeMap<>((Map<String, String>) param.getRawData());
        AllInPaymentProperties config = paymentConfigManager.getConfig(AllInPaymentProperties.class, param.getPaymentConfigId());
        try {
            boolean isSign = AllInSignUtil.validSign(rawData, config.getMd5AppKey(), config.getSignType());
            Assert.isTrue(isSign, "allInPayment notify sign failed!");
            param.setOutTradeNo(rawData.get(PaymentConstant.CUS_ORDER_ID));
            param.setTransactionId(PaymentConstant.TRX_ID);
            param.setSuccess(rawData.get(PaymentConstant.TRX_STATUS).equals(PaymentConstant.TRX_SUCCESS));
            param.setMessage(PaymentConstant.OK);
            consumer.accept(param);
            return PaymentConstant.SUCCESS.toLowerCase();
        } catch (Exception e) {
            log.error(" [通联支付回调失败-{}]: {}", param, e.getMessage());
            param.setMessage(e.getMessage());
            return PaymentConstant.FAIL.toLowerCase();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public String refundNotify(PaymentRefundNotifyParam param, PaymentChannel paymentChannel, Consumer<PaymentRefundNotifyParam> consumer) {
        log.info(" [通联退款回调参数]: {}", param);
        TreeMap<String, String> rawData = new TreeMap<>((Map<String, String>) param.getRawData());
        AllInPaymentProperties config = paymentConfigManager.getConfig(AllInPaymentProperties.class, param.getPaymentConfigId());
        try {
            if (!rawData.containsKey(PaymentConstant.COMPLETE_REFUND)) {
                boolean isSign = AllInSignUtil.validSign(rawData, config.getMd5AppKey(), config.getSignType());
                Assert.isTrue(isSign, "allInPayment notify sign failed!");
            }
            param.setOutTradeNo(rawData.get(PaymentConstant.CUS_ORDER_ID));
            param.setTransactionId(rawData.get(PaymentConstant.TRX_ID));
            param.setOutRefundNo(rawData.get(PaymentConstant.REQ_SN));
            param.setSuccess(rawData.get(PaymentConstant.TRX_STATUS).equals(PaymentConstant.TRX_SUCCESS));
            param.setMessage(rawData.getOrDefault(PaymentConstant.ERR_MSG, PaymentConstant.OK));
            consumer.accept(param);
            return PaymentConstant.SUCCESS.toLowerCase();
        } catch (Exception e) {
            log.error(" [通联退款回调失败-{}]: {}", param, e.getMessage());
            param.setMessage(e.getMessage());
            return PaymentConstant.FAIL.toLowerCase();
        }
    }


    @Override
    public PaymentChannel channel() {
        return PaymentChannel.ALLINPAY;
    }

    public static void syncRefundNotify(PaymentRefundParam param,
                                        PaymentRefundResult refundResult,
                                        PaymentCommand paymentCommand) {
        PaymentRefundNotifyParam refundNotifyParam = new PaymentRefundNotifyParam();
        String businessTypeCode = param.getBusinessTypeCode();
        String configId = param.getExtendParams().get(PaymentConstant.PAYMENT_CONFIG_ID);
        refundNotifyParam.setBusinessTypeCode(businessTypeCode);
        refundNotifyParam.setPaymentConfigId(configId);
        Map<String, String> map = new HashMap<>(16);
        map.put(PaymentConstant.CUS_ORDER_ID, param.getOutTradeNo());
        map.put(PaymentConstant.TRX_ID, param.getTransactionId());
        map.put(PaymentConstant.REQ_SN, param.getOutRefundNo());
        map.put(PaymentConstant.TRX_STATUS, BooleanUtils.isTrue(refundResult.getRefundResult()) ? PaymentConstant.TRX_SUCCESS : "3089");
        map.put(PaymentConstant.COMPLETE_REFUND, PaymentConstant.TRADE_SUCCESS);
        map.put(PaymentConstant.ERR_MSG, BooleanUtils.isTrue(refundResult.getRefundResult()) ? PaymentConstant.OK : refundResult.getMessage());
        refundNotifyParam.setRawData(map);
        paymentCommand.refundNotify(refundNotifyParam, PaymentChannel.ALLINPAY);
    }

}

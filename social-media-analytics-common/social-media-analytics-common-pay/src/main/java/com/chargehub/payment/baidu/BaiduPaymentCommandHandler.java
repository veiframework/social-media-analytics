package com.chargehub.payment.baidu;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.chargehub.payment.PaymentChannel;
import com.chargehub.payment.PaymentCommandHandler;
import com.chargehub.payment.PaymentConfigManager;
import com.chargehub.payment.bean.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;


/**
 * TODO 待实现
 *
 * @author Zhanghaowei
 * @date 2025/04/03 10:54
 */
@Slf4j
public class BaiduPaymentCommandHandler implements PaymentCommandHandler {

    private final PaymentConfigManager paymentConfigManager;


    public BaiduPaymentCommandHandler(PaymentConfigManager paymentConfigManager) {
        this.paymentConfigManager = paymentConfigManager;
    }


    @Override
    public PaymentResult createOrder(PaymentParam param, PaymentChannel paymentChannel) {
        BaiduPaymentProperties config = paymentConfigManager.getConfig(BaiduPaymentProperties.class, param.getExtendParam());
        BaiduPaymentParam baiduPaymentParam = (BaiduPaymentParam) param;
        BaiduPaymentResult paymentResult = new BaiduPaymentResult();
        paymentResult.setAppKey(config.getAppKey());
        paymentResult.setDealId(config.getDealId());
        paymentResult.setTpOrderId(baiduPaymentParam.getOutTradeNo());
//        paymentResult.setTotalAmount(String.valueOf(baiduPaymentParam.getTotalFee()));
//        paymentResult.setNotifyUrl(baiduPaymentParam.getNotifyUrl());
        paymentResult.setDealTitle("百度收银台支付");
        paymentResult.setSignFieldsRange(1);
        String signResult = paymentResult.rsaSignResult(config);
        if (StringUtils.isBlank(signResult)) {
            return null;
        }
        paymentResult.setRsaSign(signResult);
        paymentResult.setResult(true);
        paymentResult.setMsg("OK");
        return paymentResult;
    }

    @Override
    public PaymentRefundResult createRefund(PaymentRefundParam paymentRefundParam, PaymentChannel paymentChannel) {
        log.info(" [baidu refund]: {}", paymentRefundParam);
        BaiduPaymentProperties config = paymentConfigManager.getConfig(BaiduPaymentProperties.class, paymentRefundParam.getExtendParams());
        BaiduPaymentRefundParam param = (BaiduPaymentRefundParam) paymentRefundParam;
        // 开发者在此设置请求参数，实际参数请参考文档说明填写
        // 如果开发者不想传非必需参数，可以将设置该参数的行注释

        // 文档中对应字段：applyRefundMoney，实际使用时请替换成真实参数
//        param.setApplyRefundMoney(paymentRefundParam.getRefundFee());
        // 文档中对应字段：bizRefundBatchId，实际使用时请替换成真实参数
        param.setBizRefundBatchId(paymentRefundParam.getOutRefundNo());
        // 0不跳过/1跳过
        param.setIsSkipAudit(1);

        // 文档中对应字段：orderId，实际使用时请替换成真实参数
//        param.setOrderId(Long.parseLong(paymentRefundParam.getTradeNo()));
        // 文档中对应字段：refundReason，实际使用时请替换成真实参数
        param.setRefundReason(paymentRefundParam.getReason());
        // 退款类型 1：用户发起退款；2：开发者业务方客服退款；3：开发者服务异常退款。
        param.setRefundType(1);
        //param.setUserId(Integer.valueOf(payRefundDto.getOpUserId())); // 文档中对应字段：userId，实际使用时请替换成真实参数

        //TODO 测试，百度支付退款回调使用固定值
        // 文档中对应字段：refundNotifyUrl，实际使用时请替换成真实参数
        param.setRefundNotifyUrl(config.getRefundNotifyUrl());
        // 文档中对应字段：pmAppKey，实际使用时请替换成真实参数
        param.setPmAppKey(config.getAppKey());

        PaymentRefundResult refundVo = new PaymentRefundResult();

        String body = HttpUtil.createPost(config.getUrl() + "/rest/2.0/smartapp/pay/paymentservice/applyOrderRefund")
                .body(JSON.toJSONString(param)).execute().body();
        BaiduPaymentRefundResult applyOrderRefundResponse = JSON.parseObject(body, BaiduPaymentRefundResult.class);
        if (0 == applyOrderRefundResponse.getErrno()) {
            refundVo.setRefundResult(true);
        } else {
            refundVo.setRefundResult(false);
        }
        refundVo.setMessage(applyOrderRefundResponse.getMsg());
        refundVo.setOutRefundNo(applyOrderRefundResponse.getData().getRefundBatchId());
        //TODO 这里是已退金额还是可退金额，不明确
        refundVo.setRefundFee(applyOrderRefundResponse.getData().getRefundPayMoney());
//        refundVo.setTotalFee(paymentRefundParam.getTotalFee());
        return refundVo;
    }

    @Override
    public String paymentNotify(PaymentNotifyParam param, PaymentChannel paymentChannel, Consumer<PaymentNotifyParam> consumer) {
        return null;
    }

    @Override
    public String refundNotify(PaymentRefundNotifyParam param, PaymentChannel paymentChannel, Consumer<PaymentRefundNotifyParam> consumer) {
        return null;
    }


    @Override
    public PaymentChannel channel() {
        return PaymentChannel.BAIDU;
    }


}

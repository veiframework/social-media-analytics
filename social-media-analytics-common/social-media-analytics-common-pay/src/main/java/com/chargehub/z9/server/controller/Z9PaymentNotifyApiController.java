package com.chargehub.z9.server.controller;

import com.chargehub.payment.PaymentChannel;
import com.chargehub.payment.PaymentCommand;
import com.chargehub.payment.bean.PaymentNotifyParam;
import com.chargehub.payment.bean.PaymentRefundNotifyParam;
import com.chargehub.z9.server.Z9PaymentUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2025/04/23 16:59
 */
@Tag(name = "支付回调API", description = "支付回调API")
@RestController
@RequestMapping("/payment")
public class Z9PaymentNotifyApiController {

    private final PaymentCommand paymentCommand;

    public Z9PaymentNotifyApiController(PaymentCommand paymentCommand) {
        this.paymentCommand = paymentCommand;
    }

    @Operation(summary = "微信支付回调")
    @PostMapping(value = "/notify/wechat/config/{configId}/business/{businessTypeCode}", produces = "text/xml")
    public String notifyWechat(@RequestBody String param,
                               @PathVariable String configId,
                               @PathVariable String businessTypeCode) {
        PaymentNotifyParam paymentNotifyParam = Z9PaymentUtil.buildPaymentNotifyParam(param, configId, businessTypeCode);
        return paymentCommand.paymentNotify(paymentNotifyParam, PaymentChannel.WECHAT);
    }

    @Operation(summary = "支付宝支付或退款回调")
    @PostMapping(value = "/notify/alipay/config/{configId}/business/{businessTypeCode}")
    public String notifyAliPay(@RequestParam Map<String, String> param,
                               @PathVariable String configId,
                               @PathVariable String businessTypeCode) {
        String refundFee = param.get("refund_fee");
        if (StringUtils.isNotBlank(refundFee) && new BigDecimal(refundFee).compareTo(BigDecimal.ZERO) > 0) {
            PaymentRefundNotifyParam refundNotifyParam = Z9PaymentUtil.buildPaymentRefundNotifyParam(param, configId, businessTypeCode);
            return paymentCommand.refundNotify(refundNotifyParam, PaymentChannel.ALIPAY);
        }
        PaymentNotifyParam paymentNotifyParam = Z9PaymentUtil.buildPaymentNotifyParam(param, configId, businessTypeCode);
        return paymentCommand.paymentNotify(paymentNotifyParam, PaymentChannel.ALIPAY);
    }

    @Operation(summary = "通联支付回调")
    @PostMapping(value = "/notify/allinpay/config/{configId}/business/{businessTypeCode}")
    public String notifyAllInPay(@RequestParam Map<String, String> param,
                                 @PathVariable String configId,
                                 @PathVariable String businessTypeCode) {
        PaymentNotifyParam paymentNotifyParam = Z9PaymentUtil.buildPaymentNotifyParam(param, configId, businessTypeCode);
        return paymentCommand.paymentNotify(paymentNotifyParam, PaymentChannel.ALLINPAY);
    }


}

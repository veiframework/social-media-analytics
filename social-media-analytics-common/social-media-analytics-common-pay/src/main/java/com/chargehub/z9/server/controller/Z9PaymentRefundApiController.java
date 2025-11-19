package com.chargehub.z9.server.controller;

import com.chargehub.payment.PaymentChannel;
import com.chargehub.payment.PaymentCommand;
import com.chargehub.payment.alipay.AliPaymentCommandHandler;
import com.chargehub.payment.alipay.AliPaymentRefundParam;
import com.chargehub.payment.allinpay.AllInPaymentCommandHandler;
import com.chargehub.payment.bean.PaymentRefundParam;
import com.chargehub.payment.bean.PaymentRefundResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhanghaowei
 * @date 2025/04/24 10:16
 */
@Tag(name = "支付退款API", description = "支付退款API")
@RestController
@RequestMapping("/payment")
public class Z9PaymentRefundApiController {

    private final PaymentCommand paymentCommand;

    public Z9PaymentRefundApiController(PaymentCommand paymentCommand) {
        this.paymentCommand = paymentCommand;
    }


    @Operation(summary = "微信统一退款")
    @PostMapping("/refund/wechat")
    public PaymentRefundResult createRefundWechat(@RequestBody @Validated PaymentRefundParam param) {
        return paymentCommand.createRefund(param, PaymentChannel.WECHAT);
    }

    @Operation(summary = "支付宝统一退款")
    @PostMapping("/refund/alipay")
    public PaymentRefundResult createRefundAliPay(@RequestBody @Validated AliPaymentRefundParam param) {
        PaymentRefundResult refundResult = paymentCommand.createRefund(param, PaymentChannel.ALIPAY);
        //全额退款无需等待回调立即执行
        AliPaymentCommandHandler.syncRefundNotify(param, refundResult, paymentCommand);
        return refundResult;
    }

    @Operation(summary = "通联统一退款")
    @PostMapping("/refund/allinpay")
    public PaymentRefundResult createRefundAllInPay(@RequestBody @Validated PaymentRefundParam param) {
        PaymentRefundResult refundResult = paymentCommand.createRefund(param, PaymentChannel.ALLINPAY);
        //TODO 通联退款暂时不支持回调通知
        AllInPaymentCommandHandler.syncRefundNotify(param, refundResult, paymentCommand);
        return refundResult;
    }
}

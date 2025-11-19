package com.chargehub.z9.server.controller;

import com.chargehub.payment.PaymentChannel;
import com.chargehub.payment.PaymentCommand;
import com.chargehub.payment.alipay.AliPaymentParam;
import com.chargehub.payment.alipay.AliPaymentResult;
import com.chargehub.payment.allinpay.AllInPaymentParam;
import com.chargehub.payment.allinpay.AllInPaymentResult;
import com.chargehub.payment.wechat.WechatPaymentParam;
import com.chargehub.payment.wechat.WechatPaymentResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhanghaowei
 * @date 2025/04/03 15:17
 */
@Tag(name = "支付API", description = "支付API")
@RestController
@RequestMapping("/payment")
public class Z9PaymentApiController {

    private final PaymentCommand paymentCommand;

    public Z9PaymentApiController(PaymentCommand paymentCommand) {
        this.paymentCommand = paymentCommand;
    }

    @Operation(summary = "微信统一支付")
    @PostMapping("/wechat")
    public WechatPaymentResult createOrder(@RequestBody @Validated WechatPaymentParam param) {
        return (WechatPaymentResult) paymentCommand.createOrder(param, PaymentChannel.WECHAT);
    }

    @Operation(summary = "支付宝统一支付")
    @PostMapping("/alipay")
    public AliPaymentResult createOrder(@RequestBody @Validated AliPaymentParam param) {
        return (AliPaymentResult) paymentCommand.createOrder(param, PaymentChannel.ALIPAY);
    }

    @Operation(summary = "通联统一支付")
    @PostMapping("/allinpay")
    public AllInPaymentResult createOrder(@RequestBody @Validated AllInPaymentParam param) {
        return (AllInPaymentResult) paymentCommand.createOrder(param, PaymentChannel.ALLINPAY);
    }


}

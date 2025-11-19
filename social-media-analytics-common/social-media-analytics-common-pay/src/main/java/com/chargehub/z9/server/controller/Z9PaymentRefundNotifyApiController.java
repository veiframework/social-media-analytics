package com.chargehub.z9.server.controller;

import com.chargehub.payment.PaymentChannel;
import com.chargehub.payment.PaymentCommand;
import com.chargehub.payment.bean.PaymentRefundNotifyParam;
import com.chargehub.z9.server.Z9PaymentUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * @author Zhanghaowei
 * @date 2025/04/23 17:03
 */
@Tag(name = "退款回调API", description = "退款回调API")
@RestController
@RequestMapping("/payment")
public class Z9PaymentRefundNotifyApiController {

    private final PaymentCommand paymentCommand;

    public Z9PaymentRefundNotifyApiController(PaymentCommand paymentCommand) {
        this.paymentCommand = paymentCommand;
    }


    @Operation(summary = "微信退款回调")
    @PostMapping(value = "/refund/notify/wechat/config/{configId}/business/{businessTypeCode}", produces = "text/xml")
    public String refundNotifyWechat(@RequestBody String param,
                                     @PathVariable String configId,
                                     @PathVariable String businessTypeCode) {
        PaymentRefundNotifyParam refundNotifyParam = Z9PaymentUtil.buildPaymentRefundNotifyParam(param, configId, businessTypeCode);
        return paymentCommand.refundNotify(refundNotifyParam, PaymentChannel.WECHAT);
    }



}

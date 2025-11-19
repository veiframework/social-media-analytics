package com.chargehub.thirdparty.api;

import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.thirdparty.api.domain.dto.alipay.AliPayPhoneNoDto;
import com.chargehub.thirdparty.api.domain.vo.alipay.AlipayPhoneNoVo;
import com.chargehub.thirdparty.api.domain.vo.alipay.AlipaySystemOauthTokenVo;
import com.chargehub.thirdparty.api.factory.RemoteAlipayMaFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 18:08
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api
 * @Filename：RemoteAlipayMaService
 */
@FeignClient(contextId = "remoteAlipayMaService", value = ServiceNameConstants.THIRD_PARTY_SERVICE, fallbackFactory = RemoteAlipayMaFallbackFactory.class)
public interface RemoteAlipayMaService {


    /**
     * 支付宝授权登录
     * @param code
     * @return
     */
    @GetMapping("/alipay-ma/auth")
    public AlipaySystemOauthTokenVo auth(@RequestParam(value="code", required = true) String code);


    /**
     * 获取用户手机号
     * @param phoneNoDto
     * @return
     */
    @PostMapping("/alipay-ma/phone-no")
    public AlipayPhoneNoVo decryptionPhoneNo(@RequestBody AliPayPhoneNoDto phoneNoDto);
}

package com.chargehub.thirdparty.api.factory;

import com.chargehub.thirdparty.api.RemoteAlipayMaService;
import com.chargehub.thirdparty.api.domain.dto.alipay.AliPayPhoneNoDto;
import com.chargehub.thirdparty.api.domain.vo.alipay.AlipayPhoneNoVo;
import com.chargehub.thirdparty.api.domain.vo.alipay.AlipaySystemOauthTokenVo;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 18:10
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.factory
 * @Filename：RemoteAlipayMaFallbackFactory
 */
public class RemoteAlipayMaFallbackFactory  implements FallbackFactory<RemoteAlipayMaService> {
    @Override
    public RemoteAlipayMaService create(Throwable cause) {
        return new RemoteAlipayMaService() {

            @Override
            public AlipaySystemOauthTokenVo auth(String code) {
                return null;
            }

            @Override
            public AlipayPhoneNoVo decryptionPhoneNo(AliPayPhoneNoDto phoneNoDto) {
                return null;
            }
        };
    }
}

package com.chargehub.thirdparty.api.factory;

import com.chargehub.thirdparty.api.RemoteWxMpService;
import com.chargehub.thirdparty.api.domain.dto.MpTemplateMessageDto;
import com.chargehub.thirdparty.api.domain.dto.WxMpCheckTokenDto;
import com.chargehub.thirdparty.api.domain.vo.wx.mp.WxMpConfigInfoVo;
import com.chargehub.thirdparty.api.domain.vo.wx.mp.WxMpOAuth2AccessTokenVo;
import com.chargehub.thirdparty.api.domain.vo.wx.mp.WxMpUserInfoVo;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 15:05
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.factory
 * @Filename：RemoteWxMpFallbackFactory
 */
@Component
public class RemoteWxMpFallbackFactory implements FallbackFactory<RemoteWxMpService> {
    @Override
    public RemoteWxMpService create(Throwable cause) {
        return new RemoteWxMpService() {
            @Override
            public WxMpOAuth2AccessTokenVo getAccessToken(String appId, String code) {
                return null;
            }

            @Override
            public Boolean checkSignature(String appId, String timestamp, String nonce, String signature) {
                return null;
            }

            @Override
            public WxMpUserInfoVo getUserInfoByOpenId(String appId, String openid) {
                return null;
            }

            @Override
            public Boolean sendMpTemplateMessage(MpTemplateMessageDto mpTemplateMessageDto) {
                return null;
            }

            @Override
            public WxMpConfigInfoVo configInfo(String appId, String url) {
                return null;
            }

            @Override
            public Boolean checkToken(WxMpCheckTokenDto wxMpCheckTokenDto) {
                return null;
            }
        };
    }
}

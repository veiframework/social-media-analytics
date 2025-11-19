package com.chargehub.thirdparty.api.factory;

import com.chargehub.thirdparty.api.RemoteWxMaService;
import com.chargehub.thirdparty.api.domain.vo.wx.ma.WxMaJscode2SessionResultVo;
import com.chargehub.thirdparty.api.domain.vo.wx.ma.WxMaPhoneNumberInfoVo;
import com.chargehub.thirdparty.api.domain.vo.wx.ma.WxMaUserInfoVo;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 14:45
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.factory
 * @Filename：RemoteWxMaFallbackFactory
 */
@Component
public class RemoteWxMaFallbackFactory implements FallbackFactory<RemoteWxMaService> {
    @Override
    public RemoteWxMaService create(Throwable cause) {
        return new RemoteWxMaService() {
            @Override
            public WxMaJscode2SessionResultVo getSessionInfo(String appId, String code) {
                return null;
            }

            @Override
            public WxMaUserInfoVo getUserInfo(String appId, String sessionKey, String encryptedData, String ivStr) {
                return null;
            }

            @Override
            public WxMaPhoneNumberInfoVo getPhoneNo(String appId, String sessionKey, String encryptedData, String ivStr) {
                return null;
            }
        };
    }
}

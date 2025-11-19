package com.chargehub.thirdparty.api.factory;

import com.chargehub.thirdparty.api.RemoteWxOpenService;
import com.chargehub.thirdparty.api.domain.dto.WxOpenMaUnLimitCodeDto;
import com.chargehub.thirdparty.api.domain.vo.wx.open.*;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 15:58
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.factory
 * @Filename：RemoteWxOpenFallbackFactory
 */
@Component
public class RemoteWxOpenFallbackFactory implements FallbackFactory<RemoteWxOpenService> {

    @Override
    public RemoteWxOpenService create(Throwable cause) {
        return new RemoteWxOpenService() {
            @Override
            public WxOpenOAuth2AccessTokenVo getAccessTokenByCode(String appId, String code) {
                return null;
            }

            @Override
            public WxOpenUserInfoVo getUserByOpenId(String appId, String openid) {
                return null;
            }

            @Override
            public WxOpenUserInfoVo getUserInfoByOpenId(String appId, String openid, String accessToken) {
                return null;
            }

            @Override
            public String receiveTicket(String requestBody, String timestamp, String nonce, String signature, String encType, String msgSignature) {
                return null;
            }

//            @Override
//            public String callback(String requestBody, String appId, String signature, String timestamp, String nonce, String openid, String encType, String msgSignature, HttpServletRequest request) {
//                return null;
//            }

            @Override
            public WxOpenAuthUrlVo getOpenAuthUrl(String redirectUri, String authType) {
                return null;
            }

            @Override
            public WxOpenConfigVo getConfigInfoByAppId(String appId, String url) {
                return null;
            }

            @Override
            public WxOpenMaJscode2SessionVo getMaSessionByCode(String appId, String code) {
                return null;
            }

            @Override
            public String getMaAccessTokenByAppId(String appId) {
                return null;
            }

            @Override
            public String getComponentAccessToken() {
                return null;
            }

            @Override
            public byte[] getMaCodeUnlimit(String appId, WxOpenMaUnLimitCodeDto wxOpenMaUnLimitCodeDto) {
                return new byte[0];
            }

            @Override
            public WxOpenMaUserPhoneVo getUserPhone(String appId, String sessionKey, String encryptedData, String ivStr) {
                return null;
            }

            @Override
            public String getComponentVerifyTicket() {
                return null;
            }

            @Override
            public LatestAuditstatusVo getLatestAuditStatus(String appId) {
                return null;
            }

            @Override
            public MaVersionInfoVo getMaVersionInfo(String appId) {
                return null;
            }
        };
    }
}

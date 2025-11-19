package com.chargehub.thirdparty.api;

import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.thirdparty.api.domain.dto.WxOpenMaUnLimitCodeDto;
import com.chargehub.thirdparty.api.domain.vo.wx.open.*;
import com.chargehub.thirdparty.api.factory.RemoteWxOpenFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 15:57
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api
 * @Filename：RemoteWxOpenService
 */
@FeignClient(contextId = "remoteWxOpenService", value = ServiceNameConstants.THIRD_PARTY_SERVICE, fallbackFactory = RemoteWxOpenFallbackFactory.class)
public interface RemoteWxOpenService {


    /**
     * 通过授权码获取用户信息
     * @param appId
     * @param code
     * @return
     */
    @GetMapping("/wx-open/{appId}/access-token")
    public WxOpenOAuth2AccessTokenVo getAccessTokenByCode(@PathVariable("appId") String appId, @RequestParam(value = "code", required = true) String code);


    /**
     * 通过用户openId查询用户
     *
     * @param appId
     * @param openid
     * @return
     */
    @GetMapping("/wx-open/{appId}/user")
    public WxOpenUserInfoVo getUserByOpenId(@PathVariable("appId") String appId, @RequestParam("openid") String openid);


    /**
     * 通过用户openId查询用户信息
     *
     * @param appId
     * @param openid
     * @param accessToken
     * @return
     */
    @GetMapping("/wx-open/{appId}/user-info")
    public WxOpenUserInfoVo getUserInfoByOpenId(@PathVariable("appId") String appId, @RequestParam("openid") String openid, @RequestParam("accessToken") String accessToken);


    /**
     * 服务商接收 ticket
     *
     * @param requestBody
     * @param timestamp
     * @param nonce
     * @param signature
     * @param encType
     * @param msgSignature
     * @return
     */
    @PostMapping("/wx-open/ticket")
    public String receiveTicket(@RequestBody(required = false) String requestBody, @RequestParam("timestamp") String timestamp,
                                @RequestParam("nonce") String nonce, @RequestParam("signature") String signature,
                                @RequestParam(name = "encrypt_type", required = false) String encType,
                                @RequestParam(name = "msg_signature", required = false) String msgSignature);


    /**
     * 获取微信开放平台回调
     *
     * @param requestBody
     * @param appId
     * @param signature
     * @param timestamp
     * @param nonce
     * @param openid
     * @param encType
     * @param msgSignature
     * @param request
     * @return
     */
//    @PostMapping("/wx-open/{appId}/callback")
//    public String callback(@RequestBody(required = false) String requestBody, @PathVariable("appId") String appId,
//                           @RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp,
//                           @RequestParam("nonce") String nonce, @RequestParam("openid") String openid,
//                           @RequestParam("encrypt_type") String encType, @RequestParam("msg_signature") String msgSignature, HttpServletRequest request);


    /**
     * 获取微信开放平台授权跳转地址
     *
     * @param redirectUri
     * @param authType
     * @return
     */
    @GetMapping("/wx-open/auth-url")
    public WxOpenAuthUrlVo getOpenAuthUrl(@RequestParam("redirectUri") String redirectUri, @RequestParam("authType") String authType);


    /**
     * 根据appid获取微信配置信息
     * @param appId
     * @param url
     * @return
     */
    @GetMapping("/wx-open/{appId}/config")
    public WxOpenConfigVo getConfigInfoByAppId(@PathVariable("appId") String appId, @RequestParam("url") String url);


    /**
     * 根据小程序授权码获取用户session信息
     * @param appId
     * @param code
     * @return
     */
    @GetMapping("/wx-open/{appId}/ma-session")
    public WxOpenMaJscode2SessionVo getMaSessionByCode(@PathVariable("appId") String appId, @RequestParam(value = "code", required = true) String code);


    /**
     * 根据小程序appid获取accessToken
     * @param appId
     * @return
     */
    @GetMapping("/wx-open/{appId}/ma-access-token")
    public String getMaAccessTokenByAppId(@PathVariable("appId") String appId);


    /**
     * 获取微信服务商access-token
     * @return
     */
    @GetMapping("/wx-open/component-access-token")
    public String getComponentAccessToken();


    /**
     * 生成小程序无限二维码
     * @param appId
     * @param wxOpenMaUnLimitCodeDto
     * @return
     */
    @PostMapping("/wx-open/{appId}/ma-code-unlimit")
    public byte[] getMaCodeUnlimit(@PathVariable("appId") String appId, @RequestBody WxOpenMaUnLimitCodeDto wxOpenMaUnLimitCodeDto);


    /**
     * 获取用户小程序手机号
     * @param appId
     * @param sessionKey
     * @param encryptedData
     * @param ivStr
     * @return
     */
    @GetMapping("/wx-open/{appId}/ma-user-phone")
    public WxOpenMaUserPhoneVo getUserPhone(@PathVariable("appId") String appId, @RequestParam(value = "sessionKey", required = true) String sessionKey,
                                            @RequestParam(value = "encryptedData", required = true) String encryptedData, @RequestParam(value = "ivStr", required = true) String ivStr);


    /**
     * 获取服务商验证票据
     * @return
     */
    @GetMapping("/wx-open/component-verify-ticket")
    public String getComponentVerifyTicket();


    /**
     * 获取小程序最后一次审核状态
     * @param appId
     * @return
     */
    @GetMapping("/wx-open/{appId}/latest-audit-status")
    public LatestAuditstatusVo getLatestAuditStatus(@PathVariable("appId") String appId);


    /**
     * 获取小程序当前的版本信息
     * @param appId
     * @return
     */
    @GetMapping("/wx-open/{appId}/get-ma-version-info")
    public MaVersionInfoVo getMaVersionInfo(@PathVariable("appId") String appId);

}
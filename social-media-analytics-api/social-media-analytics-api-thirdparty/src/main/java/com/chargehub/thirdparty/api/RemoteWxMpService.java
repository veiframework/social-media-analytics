package com.chargehub.thirdparty.api;

import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.thirdparty.api.domain.dto.MpTemplateMessageDto;
import com.chargehub.thirdparty.api.domain.dto.WxMpCheckTokenDto;
import com.chargehub.thirdparty.api.domain.vo.wx.mp.WxMpConfigInfoVo;
import com.chargehub.thirdparty.api.domain.vo.wx.mp.WxMpOAuth2AccessTokenVo;
import com.chargehub.thirdparty.api.domain.vo.wx.mp.WxMpUserInfoVo;
import com.chargehub.thirdparty.api.factory.RemoteWxMpFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 15:01
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api
 * @Filename：RemoteWxMpService
 */
@FeignClient(contextId = "remoteWxMpService", value = ServiceNameConstants.THIRD_PARTY_SERVICE, fallbackFactory = RemoteWxMpFallbackFactory.class)
public interface RemoteWxMpService {

    /**
     * 获取微信公众号授权token
     * @param appId
     * @param code
     * @return
     */
    @GetMapping("/wx-mp/access-token/{appId}")
    public WxMpOAuth2AccessTokenVo getAccessToken(@PathVariable("appId") String appId, @RequestParam(value="code", required = true) String code);


    /**
     * 验证微信回调服务器签名
     * @param appId
     * @param timestamp
     * @param nonce
     * @param signature
     * @return
     */
    @GetMapping("/wx-mp/check-signature/{appId}")
    public Boolean checkSignature(@PathVariable("appId") String appId,@RequestParam("timestamp") String timestamp,
                                  @RequestParam("nonce") String nonce,@RequestParam("signature") String signature);


    /**
     * 通过公众号openId获取用户信息
     * @param appId
     * @param openid
     * @return
     */
    @GetMapping("/wx-mp/user-info/{appId}")
    public WxMpUserInfoVo getUserInfoByOpenId(@PathVariable("appId") String appId, @RequestParam("openid") String openid);



    /**
     * 发送公众号模版消息
     * @param mpTemplateMessageDto
     * @return
     */
    @PostMapping("/wx-mp/template-message")
    public Boolean sendMpTemplateMessage(@RequestBody MpTemplateMessageDto mpTemplateMessageDto);


    /**
     * 根据url获取jsapi配置信息
     * @param appId
     * @param url
     * @return
     */
    @GetMapping("/wx-mp/config-info/{appId}")
    public WxMpConfigInfoVo configInfo(@PathVariable("appId") String appId, @RequestParam("url") String url);


    /**
     * 验证微信回调地址token
     * @param wxMpCheckTokenDto
     * @return
     */
    @PostMapping("/wx-mp/check-token")
    public Boolean checkToken(@RequestBody WxMpCheckTokenDto wxMpCheckTokenDto);
}

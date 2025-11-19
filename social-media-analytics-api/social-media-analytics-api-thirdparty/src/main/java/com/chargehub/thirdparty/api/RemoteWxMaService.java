package com.chargehub.thirdparty.api;

import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.thirdparty.api.domain.vo.wx.ma.WxMaJscode2SessionResultVo;
import com.chargehub.thirdparty.api.domain.vo.wx.ma.WxMaPhoneNumberInfoVo;
import com.chargehub.thirdparty.api.domain.vo.wx.ma.WxMaUserInfoVo;
import com.chargehub.thirdparty.api.factory.RemoteWxMaFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 14:45
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api
 * @Filename：RemoteWxMaService
 */
@FeignClient(contextId = "remoteWxMaService", value = ServiceNameConstants.THIRD_PARTY_SERVICE, fallbackFactory = RemoteWxMaFallbackFactory.class)
public interface RemoteWxMaService {


    /**
     * 获取当前会话用户信息
     * @param appId
     * @param code
     * @return
     */
    @GetMapping("/wx-ma/session-info/{appId}")
    public WxMaJscode2SessionResultVo getSessionInfo(@PathVariable("appId") String appId, @RequestParam(value="code", required = true) String code);


    /**
     * 解析用户微信信息
     * @param appId
     * @param sessionKey
     * @param encryptedData
     * @param ivStr
     * @return
     */
    @GetMapping("/wx-ma/user-info/{appId}")
    public WxMaUserInfoVo getUserInfo(@PathVariable("appId") String appId, @RequestParam(value="sessionKey", required = true)String sessionKey,
                                      @RequestParam(value="encryptedData", required = true) String encryptedData, @RequestParam(value="ivStr", required = true)String ivStr);


    /**
     * 获取用户手机号信息
     * @param appId
     * @param sessionKey
     * @param encryptedData
     * @param ivStr
     * @return
     */
    @GetMapping("/wx-ma/phone-no/{appId}")
    public WxMaPhoneNumberInfoVo getPhoneNo(@PathVariable("appId") String appId, @RequestParam(value="sessionKey", required = true)String sessionKey,
                                            @RequestParam(value="encryptedData", required = true) String encryptedData, @RequestParam(value="ivStr", required = true)String ivStr);
}

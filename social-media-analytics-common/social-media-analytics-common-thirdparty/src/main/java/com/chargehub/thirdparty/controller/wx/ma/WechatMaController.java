package com.chargehub.thirdparty.controller.wx.ma;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.chargehub.common.core.web.controller.BaseController;
import com.chargehub.thirdparty.api.domain.vo.wx.ma.WxMaJscode2SessionResultVo;
import com.chargehub.thirdparty.api.domain.vo.wx.ma.WxMaPhoneNumberInfoVo;
import com.chargehub.thirdparty.api.domain.vo.wx.ma.WxMaUserInfoVo;
import com.chargehub.thirdparty.config.wx.ma.WxMaConfiguration;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 14:03
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.controller.wx.ma
 * @Filename：WechatMaController
 */
@RestController
@RequestMapping("/wx-ma")
@Tag(name = "微信小程序相关", description = "微信小程序相关")
public class WechatMaController extends BaseController {


    /**
     * 获取当前会话用户信息
     * @param appId
     * @param code
     * @return
     */
    @GetMapping("/session-info/{appId}")
    public WxMaJscode2SessionResultVo getSessionInfo(@PathVariable("appId") String appId, @RequestParam(value="code", required = true) String code) {
        try {
            WxMaJscode2SessionResult wxMaJscode2SessionResult = WxMaConfiguration.getMaService(appId).getUserService().getSessionInfo(code);
            WxMaJscode2SessionResultVo wxMaJscode2SessionResultVo = new WxMaJscode2SessionResultVo();
            BeanUtils.copyProperties(wxMaJscode2SessionResult,wxMaJscode2SessionResultVo);
            return wxMaJscode2SessionResultVo;
        } catch (WxErrorException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 解析用户微信信息
     * @param appId
     * @param sessionKey
     * @param encryptedData
     * @param ivStr
     * @return
     */
    @GetMapping("/user-info/{appId}")
    public WxMaUserInfoVo getUserInfo(@PathVariable("appId") String appId, @RequestParam(value="sessionKey", required = true)String sessionKey,
                                      @RequestParam(value="encryptedData", required = true) String encryptedData, @RequestParam(value="ivStr", required = true)String ivStr) {
        WxMaUserInfo wxMaUserInfo = WxMaConfiguration.getMaService(appId).getUserService().getUserInfo(sessionKey, encryptedData, ivStr);
        WxMaUserInfoVo wxMaUserInfoVo = new WxMaUserInfoVo();
        BeanUtils.copyProperties(wxMaUserInfo,wxMaUserInfoVo);
        return wxMaUserInfoVo;
    }


    /**
     * 获取用户手机号信息
     * @param appId
     * @param sessionKey
     * @param encryptedData
     * @param ivStr
     * @return
     */
    @GetMapping("/phone-no/{appId}")
    public WxMaPhoneNumberInfoVo getPhoneNo(@PathVariable("appId") String appId, @RequestParam(value="sessionKey", required = true)String sessionKey,
                                            @RequestParam(value="encryptedData", required = true) String encryptedData, @RequestParam(value="ivStr", required = true)String ivStr) {
        WxMaPhoneNumberInfo wxMaPhoneNumberInfox = WxMaConfiguration.getMaService(appId).getUserService().getPhoneNoInfo(sessionKey, encryptedData, ivStr);
        WxMaPhoneNumberInfoVo wxMaPhoneNumberInfoVo = new WxMaPhoneNumberInfoVo();
        BeanUtils.copyProperties(wxMaPhoneNumberInfox,wxMaPhoneNumberInfoVo);
        return wxMaPhoneNumberInfoVo;
    }
}

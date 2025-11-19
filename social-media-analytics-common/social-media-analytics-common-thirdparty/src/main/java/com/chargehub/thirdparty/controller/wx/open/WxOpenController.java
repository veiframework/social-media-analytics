package com.chargehub.thirdparty.controller.wx.open;

import cn.binarywang.wx.miniapp.bean.WxMaCodeLineColor;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.alibaba.fastjson.JSONObject;
import com.chargehub.thirdparty.api.domain.dto.WxOpenMaUnLimitCodeDto;
import com.chargehub.thirdparty.api.domain.vo.wx.open.*;
import com.chargehub.thirdparty.service.IWxOpenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.crypto.SHA1;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.open.api.WxOpenService;
import me.chanjar.weixin.open.bean.message.WxOpenXmlMessage;
import me.chanjar.weixin.open.util.WxOpenCryptUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 14:20
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.controller.wx.open
 * @Filename：WxOpenController
 */
@RestController
@RequestMapping("/wx-open")
@Tag(name = "微信开放平台相关", description = "微信开放平台相关接口")
public class WxOpenController {


    private static final Logger log = LoggerFactory.getLogger(WxOpenController.class);


    @Autowired
    private WxOpenService wxOpenService;
    @Autowired
    private IWxOpenService iWxOpenService;




    /**
     * 通过授权码获取用户信息
     * @param appId
     * @param code
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/{appId}/access-token")
    public WxOpenOAuth2AccessTokenVo getAccessTokenByCode(@PathVariable String appId, @RequestParam(value="code", required = true) String code) {
        WxOpenOAuth2AccessTokenVo wxOpenOAuth2AccessTokenVo = new WxOpenOAuth2AccessTokenVo();
        if(!StringUtils.isNotEmpty(appId)) {
            wxOpenOAuth2AccessTokenVo.setResult(false);
            wxOpenOAuth2AccessTokenVo.setMsg("公众号AppId不能为空");
            return wxOpenOAuth2AccessTokenVo;
        }

        try {
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxOpenService.getWxOpenComponentService().oauth2getAccessToken(appId, code);
            BeanUtils.copyProperties(wxMpOAuth2AccessToken,wxOpenOAuth2AccessTokenVo);
            wxOpenOAuth2AccessTokenVo.setResult(true);
            wxOpenOAuth2AccessTokenVo.setMsg("OK");
        } catch (Exception e){
            e.printStackTrace();
            wxOpenOAuth2AccessTokenVo.setResult(false);
            wxOpenOAuth2AccessTokenVo.setMsg(e.getMessage());
        }
        return wxOpenOAuth2AccessTokenVo;
    }



    /**
     * 通过用户openId查询用户
     * @param appId
     * @param openid
     * @return
     */
    @GetMapping("/{appId}/user")
    public WxOpenUserInfoVo getUserByOpenId(@PathVariable String appId, @RequestParam("openid") String openid) {
        WxOpenUserInfoVo wxOpenUserInfoVo = new WxOpenUserInfoVo();
        if(!StringUtils.isNotEmpty(appId)) {
            wxOpenUserInfoVo.setResult(false);
            wxOpenUserInfoVo.setMsg("公众号AppId不能为空");
            return wxOpenUserInfoVo;
        }

        try {
            WxMpUser wxMpUser = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appId).getUserService().userInfo(openid);
            BeanUtils.copyProperties(wxMpUser,wxOpenUserInfoVo);
            wxOpenUserInfoVo.setResult(true);
            wxOpenUserInfoVo.setMsg("OK");
        } catch (WxErrorException e) {
            e.printStackTrace();
            wxOpenUserInfoVo.setResult(false);
            wxOpenUserInfoVo.setMsg(e.getMessage());
        }
        return wxOpenUserInfoVo;
    }


    /**
     * 通过用户openId查询用户信息
     * @param appId
     * @param openid
     * @param accessToken
     * @return
     */
    @GetMapping("/{appId}/user-info")
    public WxOpenUserInfoVo getUserInfoByOpenId(@PathVariable String appId,@RequestParam("openid") String openid,@RequestParam("accessToken") String accessToken) {
        WxOpenUserInfoVo wxOpenUserInfoVo = new WxOpenUserInfoVo();
        if(!StringUtils.isNotEmpty(appId)) {
            wxOpenUserInfoVo.setResult(false);
            wxOpenUserInfoVo.setMsg("公众号AppId不能为空");
            return wxOpenUserInfoVo;
        }

        try {
            WxMpOAuth2AccessToken token = new WxMpOAuth2AccessToken();
            token.setOpenId(openid);
            token.setAccessToken(accessToken);
            WxMpUser wxMpUser = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appId).oauth2getUserInfo(token,null);
            BeanUtils.copyProperties(wxMpUser,wxOpenUserInfoVo);
            wxOpenUserInfoVo.setResult(true);
            wxOpenUserInfoVo.setMsg("OK");
        } catch (WxErrorException e) {
            e.printStackTrace();
            wxOpenUserInfoVo.setResult(false);
            wxOpenUserInfoVo.setMsg(e.getMessage());
        }
        return wxOpenUserInfoVo;
    }




    /**
     * 服务商接收 ticket
     * @param requestBody
     * @param timestamp
     * @param nonce
     * @param signature
     * @param encType
     * @param msgSignature
     * @return
     */
    @PostMapping("/ticket")
    public String receiveTicket(@RequestBody(required = false) String requestBody, @RequestParam("timestamp") String timestamp,
                                @RequestParam("nonce") String nonce, @RequestParam("signature") String signature,
                                @RequestParam(name = "encrypt_type", required = false) String encType,
                                @RequestParam(name = "msg_signature", required = false) String msgSignature) {

        log.info("接收微信请求：[signature=[{}], encType=[{}], msgSignature=[{}], timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                signature, encType, msgSignature, timestamp, nonce, requestBody);

        if (!StringUtils.equalsIgnoreCase("aes", encType) || !wxOpenService.getWxOpenComponentService().checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        WxOpenXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedXml(requestBody, wxOpenService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        log.debug("消息解密后内容为：\n{} ", inMessage.toString());
        String out = null;
        try {
            out = wxOpenService.getWxOpenComponentService().route(inMessage);
        } catch (WxErrorException e) {
            return "faile";
        }
        log.debug("\n组装回复信息：{}", out);
        return out;
    }


    /**
     *获取微信开放平台回调
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
    @PostMapping("{appId}/callback")
    public String callback(@RequestBody(required = false)String requestBody, @PathVariable("appId") String appId,
                           @RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp,
                           @RequestParam("nonce") String nonce, @RequestParam("openid") String openid,
                           @RequestParam("encrypt_type") String encType, @RequestParam("msg_signature") String msgSignature, HttpServletRequest request) {
        log.info("接收微信请求：[appId=[{}], openid=[{}], signature=[{}], encType=[{}], msgSignature=[{}],timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                appId, openid, signature, encType, msgSignature, timestamp, nonce, requestBody);
        log.info("query:" + request.getQueryString()+"\nbody:"+requestBody);
        if (!StringUtils.equalsIgnoreCase("aes", encType) || !wxOpenService.getWxOpenComponentService().checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        String out = "";
        // aes加密的消息
        WxMpXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedMpXml(requestBody, wxOpenService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        log.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
        // 全网发布测试用例
        if (StringUtils.equalsAnyIgnoreCase(appId, "wxd101a85aa106f53e", "wx570bc396a51b8ff8")) {
            try {
                if (StringUtils.equals(inMessage.getMsgType(), "text")) {
                    if (StringUtils.equals(inMessage.getContent(), "TESTCOMPONENT_MSG_TYPE_TEXT")) {
                        out = new WxOpenCryptUtil(wxOpenService.getWxOpenConfigStorage()).encrypt(
                                WxMpXmlOutMessage.TEXT().content("TESTCOMPONENT_MSG_TYPE_TEXT_callback")
                                        .fromUser(inMessage.getToUser())
                                        .toUser(inMessage.getFromUser())
                                        .build()
                                        .toXml()
                        );
                    } else if (StringUtils.startsWith(inMessage.getContent(), "QUERY_AUTH_CODE:")) {
                        String msg = inMessage.getContent().replace("QUERY_AUTH_CODE:", "") + "_from_api";
                        WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT().content(msg).toUser(inMessage.getFromUser()).build();
                        wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appId).getKefuService().sendKefuMessage(kefuMessage);
                    }
                } else if (StringUtils.equals(inMessage.getMsgType(), "event")) {
                    WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT().content(inMessage.getEvent() + "from_callback").toUser(inMessage.getFromUser()).build();
                    wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appId).getKefuService().sendKefuMessage(kefuMessage);
                }
            } catch (WxErrorException e) {
                log.error("callback", e);
            }
        }
        return out;
    }




    /**
     * 获取微信开放平台授权跳转地址
     * @param redirectUri
     * @param authType
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/auth-url")
    public WxOpenAuthUrlVo getOpenAuthUrl(@RequestParam("redirectUri")String redirectUri, @RequestParam("authType")String authType) {
        WxOpenAuthUrlVo wxOpenAuthUrlVo = new WxOpenAuthUrlVo();
        try {
            String url = wxOpenService.getWxOpenComponentService().getPreAuthUrl(redirectUri, authType, null);
            wxOpenAuthUrlVo.setAuthUrl(url);
            wxOpenAuthUrlVo.setResult(true);
            wxOpenAuthUrlVo.setMsg("OK");
        } catch (Exception e){
            e.printStackTrace();
            wxOpenAuthUrlVo.setResult(false);
            wxOpenAuthUrlVo.setMsg(e.getMessage());
        }
        return wxOpenAuthUrlVo;
    }




    /**
     * 根据appid获取微信配置信息
     * @param appId
     * @param url
     * @return
     */
    @GetMapping("/{appId}/config")
    public WxOpenConfigVo getConfigInfoByAppId(@PathVariable String appId, @RequestParam("url") String url) {
        WxOpenConfigVo wxOpenConfigVo = new WxOpenConfigVo();
        try {
            String ticket = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appId).getJsapiTicket();
            String nonceStr = RandomStringUtils.randomAlphanumeric(10);
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

            String str = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
            String signature  = SHA1.genWithAmple(str);

            wxOpenConfigVo.setResult(true);
            wxOpenConfigVo.setMsg("OK");
            wxOpenConfigVo.setAppId(appId);
            wxOpenConfigVo.setTimestamp(timestamp);
            wxOpenConfigVo.setNonceStr(nonceStr);
            wxOpenConfigVo.setSignature(signature);
        } catch (WxErrorException e) {
            e.printStackTrace();
            wxOpenConfigVo.setResult(false);
            wxOpenConfigVo.setMsg(e.getMessage());
        }
        return wxOpenConfigVo;
    }







    /**
     * 根据小程序授权码获取用户session信息
     * @param appId
     * @param code
     * @return
     */
    @GetMapping("/{appId}/ma-session")
    public WxOpenMaJscode2SessionVo getMaSessionByCode(@PathVariable String appId, @RequestParam(value="code", required = true) String code) {
        WxOpenMaJscode2SessionVo wxOpenMaJscode2SessionVo = new WxOpenMaJscode2SessionVo();
        try {
            WxMaJscode2SessionResult wxMaJscode2SessionResult = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(appId).getUserService().getSessionInfo(code);

            BeanUtils.copyProperties(wxMaJscode2SessionResult,wxOpenMaJscode2SessionVo);
            wxOpenMaJscode2SessionVo.setResult(true);
            wxOpenMaJscode2SessionVo.setMsg("OK");
        } catch (Exception e){
            e.printStackTrace();
            wxOpenMaJscode2SessionVo.setResult(false);
            wxOpenMaJscode2SessionVo.setMsg(e.getMessage());
        }
        return wxOpenMaJscode2SessionVo;
    }



    /**
     * 根据小程序appid获取accessToken
     * @param appId
     * @return
     */
    @GetMapping("/{appId}/ma-access-token")
    public String getMaAccessTokenByAppId(@PathVariable String appId) {
        try {
            return wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(appId).getAccessToken();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取微信服务商access-token
     * @return
     */
    @GetMapping("/component-access-token")
    public String getComponentAccessToken() {
        try {
            return wxOpenService.getWxOpenComponentService().getComponentAccessToken(false);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }




    /**
     * 生成小程序无限二维码
     * @param appId
     * @param wxOpenMaUnLimitCodeDto
     * @return
     */
    @PostMapping("/{appId}/ma-code-unlimit")
    public byte[] getMaCodeUnlimit(@PathVariable String appId,@RequestBody WxOpenMaUnLimitCodeDto wxOpenMaUnLimitCodeDto) {
        byte[] bytes = null;
        try {
            bytes = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(appId).getQrcodeService()
                    .createWxaCodeUnlimitBytes(wxOpenMaUnLimitCodeDto.getScene(), wxOpenMaUnLimitCodeDto.getPage(), wxOpenMaUnLimitCodeDto.getWidth(),
                            wxOpenMaUnLimitCodeDto.getAutoColor(), new WxMaCodeLineColor("0","0","0"), wxOpenMaUnLimitCodeDto.getIsHyaline());
            return bytes;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 获取用户小程序手机号
     * @param appId
     * @param sessionKey
     * @param encryptedData
     * @param ivStr
     * @return
     */
    @GetMapping("/{appId}/ma-user-phone")
    public WxOpenMaUserPhoneVo getUserPhone(@PathVariable String appId, @RequestParam(value="sessionKey", required = true)String sessionKey,
                                            @RequestParam(value="encryptedData", required = true) String encryptedData, @RequestParam(value="ivStr", required = true)String ivStr) {

        WxOpenMaUserPhoneVo getUserPhoneVo = new WxOpenMaUserPhoneVo();
        try {
            ivStr = URLEncoder.encode(ivStr,"UTF-8").replace("%3D","=").replace("%2F","/");
            encryptedData = URLEncoder.encode(encryptedData,"UTF-8").replace("%3D","=").replace("%2F","/");
            sessionKey = URLEncoder.encode(sessionKey,"UTF-8").replace("%3D","=").replace("%2F","/");

            WxMaPhoneNumberInfo wxMaPhoneNumberInfo = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(appId)
                    .getUserService().getPhoneNoInfo(sessionKey,encryptedData,ivStr);

            log.info("【wxMaPhoneNumberInfo】：{}", JSONObject.toJSONString(wxMaPhoneNumberInfo));

            BeanUtils.copyProperties(wxMaPhoneNumberInfo,getUserPhoneVo);
            getUserPhoneVo.setResult(true);
            getUserPhoneVo.setMsg("OK");
        } catch (Exception e){
            e.printStackTrace();
            getUserPhoneVo.setResult(false);
            getUserPhoneVo.setMsg(e.getMessage());
        }
        return getUserPhoneVo;
    }



    /**
     * 获取服务商验证票据
     * @return
     */
    @GetMapping("/component-verify-ticket")
    public String getComponentVerifyTicket() {
        try {
            return wxOpenService.getWxOpenComponentService().getWxOpenConfigStorage().getComponentVerifyTicket();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }




    /**
     * 获取小程序最后一次审核状态
     * @param appId
     * @return
     */
    @GetMapping("/{appId}/latest-audit-status")
    public LatestAuditstatusVo getLatestAuditStatus(@PathVariable String appId) {

        return iWxOpenService.getLatestAuditStatus(appId);
    }


    /**
     * 获取小程序当前的版本信息
     * @param appId
     * @return
     */
    @GetMapping("/{appId}/get-ma-version-info")
    public MaVersionInfoVo getMaVersionInfo(@PathVariable String appId) {

        return iWxOpenService.getMaVersionInfo(appId);
    }
}

package com.chargehub.thirdparty.controller.wx.mp;

import com.alibaba.fastjson.JSONObject;
import com.chargehub.common.core.web.controller.BaseController;
import com.chargehub.thirdparty.api.domain.dto.MpTemplateMessageDto;
import com.chargehub.thirdparty.api.domain.dto.WxMpCheckTokenDto;
import com.chargehub.thirdparty.api.domain.vo.wx.mp.WxMpConfigInfoVo;
import com.chargehub.thirdparty.api.domain.vo.wx.mp.WxMpOAuth2AccessTokenVo;
import com.chargehub.thirdparty.api.domain.vo.wx.mp.WxMpUserInfoVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.crypto.SHA1;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 14:18
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.controller.wx.mp
 * @Filename：WechatMpController
 */
@RestController
@RequestMapping("/wx-mp")
@Tag(name = "微信公众号相关", description = "微信公众号相关")
public class WechatMpController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(WechatMpController.class);


    @Autowired
    private WxMpService wxService;


    /**
     * 获取微信公众号授权token
     * @param appId
     * @param code
     * @return
     */
    @GetMapping("/access-token/{appId}")
    public WxMpOAuth2AccessTokenVo getAccessToken(@PathVariable("appId") String appId, @RequestParam(value="code", required = true) String code) {
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = null;
        try {
            wxMpOAuth2AccessToken = wxService.switchoverTo(appId).oauth2getAccessToken(code);
            WxMpOAuth2AccessTokenVo wxMpOAuth2AccessTokenVo = new WxMpOAuth2AccessTokenVo();
            BeanUtils.copyProperties(wxMpOAuth2AccessToken, wxMpOAuth2AccessTokenVo);
            return wxMpOAuth2AccessTokenVo;
        } catch (WxErrorException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 验证微信回调服务器签名
     * @param appId
     * @param timestamp
     * @param nonce
     * @param signature
     * @return
     */
    @GetMapping("/check-signature/{appId}")
    public Boolean checkSignature(@PathVariable("appId") String appId,@RequestParam("timestamp") String timestamp,
                                  @RequestParam("nonce") String nonce,@RequestParam("signature") String signature) {

        return wxService.switchoverTo(appId).checkSignature(timestamp,nonce,signature);
    }


    /**
     * 通过公众号openId获取用户信息
     * @param appId
     * @param openid
     * @return
     */
    @GetMapping("/user-info/{appId}")
    public WxMpUserInfoVo getUserInfoByOpenId(@PathVariable("appId") String appId, @RequestParam("openid") String openid) {
        try {
            WxMpUser wxMpUser = wxService.switchoverTo(appId).getUserService().userInfo(openid);
            WxMpUserInfoVo wxMpUserInfoVo = new WxMpUserInfoVo();
            BeanUtils.copyProperties(wxMpUser,wxMpUserInfoVo);
            return wxMpUserInfoVo;
        } catch (WxErrorException e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * 发送公众号模版消息
     * @param mpTemplateMessageDto
     * @return
     */
    @PostMapping("/template-message")
    public Boolean sendMpTemplateMessage(@RequestBody MpTemplateMessageDto mpTemplateMessageDto) {
        //实例化模板对象
        WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
        wxMpTemplateMessage.setTemplateId(mpTemplateMessageDto.getTemplateId());
        //设置发送给哪个用户
        wxMpTemplateMessage.setToUser(mpTemplateMessageDto.getOpenId());
        //设置模版消息跳转链接地址
        wxMpTemplateMessage.setUrl(mpTemplateMessageDto.getUrl());
        //构建消息格式
        List<WxMpTemplateData> list = setContent(mpTemplateMessageDto);
        //放进模板对象。准备发送
        wxMpTemplateMessage.setData(list);

        try {
            String str = wxService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
            log.info("模板消息推送完成，用户openId:" + mpTemplateMessageDto.getOpenId() + "，推送数据：" + JSONObject.toJSONString(mpTemplateMessageDto) +
                    "返回数据：" + str);
            return true;
        } catch (WxErrorException e) {
            e.printStackTrace();
            log.error("消息推送失败，用户openId:" + mpTemplateMessageDto.getOpenId() + "，推送数据：" + JSONObject.toJSONString(mpTemplateMessageDto) +
                    "失败原因：" + e.getMessage());
            return false;
        }
    }


    /**
     * 根据url获取jsapi配置信息
     * @param appId
     * @param url
     * @return
     */
    @GetMapping("/config-info/{appId}")
    public WxMpConfigInfoVo configInfo(@PathVariable("appId") String appId, @RequestParam("url") String url) {
        try {
            String ticket = wxService.switchoverTo(appId).getJsapiTicket();
            String nonceStr = RandomStringUtils.randomAlphanumeric(10);
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String str = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
            String signature  = SHA1.genWithAmple(str);

            WxMpConfigInfoVo wxMpConfigInfoVo = new WxMpConfigInfoVo();
            wxMpConfigInfoVo.setAppId(appId);
            wxMpConfigInfoVo.setNonceStr(nonceStr);
            wxMpConfigInfoVo.setTimestamp(timestamp);
            wxMpConfigInfoVo.setSignature(signature);
            return wxMpConfigInfoVo;
        } catch (WxErrorException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 验证微信回调地址token
     * @param wxMpCheckTokenDto
     * @return
     */
    @PostMapping("/check-token")
    public Boolean checkToken(@RequestBody WxMpCheckTokenDto wxMpCheckTokenDto) {
        try {
            return wxService.checkSignature(wxMpCheckTokenDto.getTimestamp(), wxMpCheckTokenDto.getNonce(), wxMpCheckTokenDto.getSignature());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }











    /**
     * 获取模板数据通过模板类型
     * @param mpTemplateMessageDto
     * @return
     */
    public List<WxMpTemplateData> setContent(MpTemplateMessageDto mpTemplateMessageDto){
        List<WxMpTemplateData> list = new ArrayList<>();
        list.add(new WxMpTemplateData("first", mpTemplateMessageDto.getFirst()));

        if(StringUtils.isNotEmpty(mpTemplateMessageDto.getKeyword1())) {
            list.add(new WxMpTemplateData(mpTemplateMessageDto.getKeyword1key(), mpTemplateMessageDto.getKeyword1()));
        }

        if(StringUtils.isNotEmpty(mpTemplateMessageDto.getKeyword2())) {
            list.add(new WxMpTemplateData(mpTemplateMessageDto.getKeyword2key(), mpTemplateMessageDto.getKeyword2()));
        }

        if(StringUtils.isNotEmpty(mpTemplateMessageDto.getKeyword3())) {
            list.add(new WxMpTemplateData(mpTemplateMessageDto.getKeyword3key(), mpTemplateMessageDto.getKeyword3()));
        }

        if(StringUtils.isNotEmpty(mpTemplateMessageDto.getKeyword4())) {
            list.add(new WxMpTemplateData(mpTemplateMessageDto.getKeyword4key(), mpTemplateMessageDto.getKeyword4()));
        }

        if(StringUtils.isNotEmpty(mpTemplateMessageDto.getKeyword5())) {
            list.add(new WxMpTemplateData(mpTemplateMessageDto.getKeyword5key(), mpTemplateMessageDto.getKeyword5()));
        }

        list.add(new WxMpTemplateData("remark", mpTemplateMessageDto.getRemark()));
        return list;
    }
}

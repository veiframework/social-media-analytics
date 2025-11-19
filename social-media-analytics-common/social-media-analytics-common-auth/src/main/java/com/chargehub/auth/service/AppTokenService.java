package com.chargehub.auth.service;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.chargehub.admin.api.RemoteAppUserService;
import com.chargehub.admin.api.domain.SysUser;
import com.chargehub.admin.api.domain.dto.WxLoginDto;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.admin.api.model.RemoteUserVo;
import com.chargehub.auth.domain.dto.WxMaLoginDto;
import com.chargehub.common.core.exception.ServiceException;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.common.security.service.TokenService;
import com.chargehub.thirdparty.api.RemoteWxMaService;
import com.chargehub.thirdparty.api.domain.vo.wx.ma.WxMaJscode2SessionResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2025/08/02 17:42
 */
@Service
public class AppTokenService {

    @Autowired
    private RemoteWxMaService remoteWxMaService;

    @Autowired
    private RemoteAppUserService remoteAppUserService;

    @Autowired
    private HubProperties hubProperties;

    @Autowired
    private TokenService tokenService;

    public Map<String, Object> loginByOpenId(WxMaLoginDto wxMaLoginDto) {
        WxMaJscode2SessionResultVo wxMaJscode2SessionResultVo = remoteWxMaService.getSessionInfo(wxMaLoginDto.getWxMaAppId(), wxMaLoginDto.getWxMaCode());
        if (wxMaJscode2SessionResultVo == null) {
            throw new ServiceException("微信小程序登录失败，code：" + wxMaLoginDto.getWxMaCode());
        }
        WxLoginDto wxLoginDto = new WxLoginDto();
        wxLoginDto.setOpenId(wxMaJscode2SessionResultVo.getOpenid());
        wxLoginDto.setUnionId(wxMaJscode2SessionResultVo.getUnionid());
        AjaxResult ajaxResult = remoteAppUserService.loginByOpenId(wxLoginDto);
        Assert.isTrue(ajaxResult.isSuccess(), ajaxResult.get(AjaxResult.MSG_TAG) + "");
        return createToken(ajaxResult);
    }

    private Map<String, Object> createToken(AjaxResult ajaxResult) {
        Object o = ajaxResult.get(AjaxResult.DATA_TAG);
        RemoteUserVo lockerRemoteUserVo = BeanUtil.toBean(o, RemoteUserVo.class);
        if (lockerRemoteUserVo == null) {
            return null;
        }
        LoginUser loginUser = new LoginUser();
        SysUser sysUser = new SysUser();
        sysUser.setUserId(Long.valueOf(lockerRemoteUserVo.getId()));
        sysUser.setUserName(lockerRemoteUserVo.getId());
        sysUser.setNickName(lockerRemoteUserVo.getNickname());
        sysUser.setPhonenumber(lockerRemoteUserVo.getPhone());
        loginUser.setExpireDuration(hubProperties.getAppExpireDuration());
        loginUser.setSysUser(sysUser);
        loginUser.setInfo(JSON.toJSONString(lockerRemoteUserVo));
        Map<String, Object> map = tokenService.createToken(loginUser);
        map.put("userInfo", lockerRemoteUserVo);
        return map;
    }


}

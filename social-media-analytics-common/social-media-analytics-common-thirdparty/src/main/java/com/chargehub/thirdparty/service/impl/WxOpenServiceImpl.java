package com.chargehub.thirdparty.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chargehub.thirdparty.api.domain.vo.wx.open.LatestAuditstatusVo;
import com.chargehub.thirdparty.api.domain.vo.wx.open.MaVersionInfoVo;
import com.chargehub.thirdparty.constant.WxOpenConstant;
import com.chargehub.thirdparty.service.IWxOpenService;
import com.chargehub.thirdparty.util.HttpClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.api.WxOpenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 15:30
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.service.impl
 * @Filename：WxOpenServiceImpl
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WxOpenServiceImpl implements IWxOpenService {

    @Value("${wechat.open.apiUrl}")
    private String apiUrl;


    @Autowired
    private WxOpenService wxOpenService;



    /**
     * 获取小程序版本信息
     * @return
     */
    @Override
    public LatestAuditstatusVo getLatestAuditStatus(String maAppid) {
        try {
            String accessToken = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(maAppid).getAccessToken();
            if(StringUtils.isEmpty(accessToken)) {
                return null;
            }

            String url = apiUrl + WxOpenConstant.MA_LAST_AUDIT_STATUS + "?access_token=" + accessToken;
            //获取小程序版本信息
            String strJson = HttpClientUtil.doGet(url);
            log.info("获取小程序最后一次审核结果返回结果：{}",strJson);
            LatestAuditstatusVo latestAuditstatusVo = JSONObject.parseObject(strJson, LatestAuditstatusVo.class);

            log.info("版本信息：" + JSONObject.toJSONString(latestAuditstatusVo));
            return latestAuditstatusVo;
        } catch (WxErrorException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取小程序版本信息
     * @param maAppid
     */
    @Override
    public MaVersionInfoVo getMaVersionInfo(String maAppid) {
        try {
            String accessToken = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(maAppid).getAccessToken();
            if(StringUtils.isEmpty(accessToken)) {
                return null;
            }

            String url = apiUrl + "?access_token=" + accessToken;
            Map<String,String> map = new HashMap<>();
            //获取小程序版本信息
            String strJson = HttpClientUtil.doPostJson(url, JSONObject.toJSONString(map));
            log.info("获取小程序版本信息返回结果：{}",strJson);
            MaVersionInfoVo maVersionInfoVo = JSONObject.parseObject(strJson, MaVersionInfoVo.class);

            log.info("版本信息：" + JSONObject.toJSONString(maVersionInfoVo));
            return maVersionInfoVo;
        } catch (WxErrorException e) {
            e.printStackTrace();
            return null;
        }
    }
}

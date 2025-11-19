package com.chargehub.thirdparty.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.chargehub.common.core.constant.CacheConstants;
import com.chargehub.common.redis.service.RedisService;
import com.chargehub.thirdparty.api.RemoteBaiduMpService;
import com.chargehub.thirdparty.api.domain.dto.baidu.*;
import com.chargehub.thirdparty.api.domain.vo.baidu.BaiduPhoneNoBizVO;
import com.chargehub.thirdparty.api.domain.vo.baidu.BaiduPhoneNoVO;
import com.chargehub.thirdparty.config.baidu.BaiduMPProperties;
import com.chargehub.thirdparty.service.BaiduMpService;
import com.chargehub.thirdparty.util.BaiduAESUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @description: 百度平台
 * @author: lfy
 * @create: 2024-09-09 10:39
 */
@Slf4j
@AllArgsConstructor
@Service
public class BaiduMpServiceImpl implements BaiduMpService {

    private final BaiduMPProperties baiduMpProperties;

    private final RemoteBaiduMpService remoteBaiduMpService;

    private final RedisService redisService;

    @Override
    public String getBaiduAccessToken() {

        String baiduAccessToken = redisService.getCacheObject(CacheConstants.BAIDU_ACCESS_TOKEN);
        if (StringUtils.isNotEmpty(baiduAccessToken)) {
            return baiduAccessToken;
        }
        return generateBaiduAccessTokenAndSetCache();
    }

    /**
     * 生成baidu access并存储缓存
     * @return
     */
    @Override
    public String generateBaiduAccessTokenAndSetCache() {
        GetAccessTokenResponse rsp = generateBaiduAccessToken();
        if (Objects.nonNull(rsp) && StringUtils.isNotEmpty(rsp.getAccessToken())) {
            redisService.setCacheObject(CacheConstants.BAIDU_ACCESS_TOKEN, rsp.getAccessToken(), baiduMpProperties.getAccessTokenExpire(), TimeUnit.DAYS);
        }
        return rsp.getAccessToken();
    }

    @Override
    public GetAccessTokenResponse generateBaiduAccessToken() {

        GetAccessTokenRequest params = new GetAccessTokenRequest();
        // 文档中对应字段：grant_type，实际使用时请替换成真实参数
        params.setGrantType("client_credentials");
        // 文档中对应字段：client_id，实际使用时请替换成真实参数
        params.setClientId(baiduMpProperties.getMpAppKey());
        // 文档中对应字段：client_secret，实际使用时请替换成真实参数
        params.setClientSecret(baiduMpProperties.getMpAppSecret());
        // 文档中对应字段：scope，实际使用时请替换成真实参数
        params.setScope("smartapp_snsapi_base");
        //Map<String, String> headers = new HashMap<>(2);
        //headers.put("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(params));
        log.info("reqAccessToken[{}]", map);
        GetAccessTokenResponse rsp = remoteBaiduMpService.reqAccessToken(URI.create(baiduMpProperties.getMpUrl()), map);
        log.info("rspAccessToken[{}]", rsp);
        return rsp;
    }

    @Override
    public GetSessionKeyV2Response handleSessionKey(String code) {

        GetSessionKeyV2Request params = new GetSessionKeyV2Request();
        //获取 BaiduAccessToken
        params.setAccessToken(getBaiduAccessToken());
        params.setCode(code);
        //params.setClientID(baiduProperties.getAppKey());
        //params.setSk(params.getSk());
        //params.("&sp_sdk_ver=" + "1.0.0");
        //params.("&sp_sdk_lang=" + "java");
        //Map<String, String> headers = new HashMap<>(2);
        //headers.put("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(params));
        log.info("reqSessionKey[{}]", map);
        GetSessionKeyV2Response rsp = remoteBaiduMpService.reqSessionKey(URI.create(baiduMpProperties.getMpUrl()), map);
        log.info("rspSessionKey[{}]", rsp);
        return rsp;
    }

    @Override
    public GetUnionIdResponse handleUnionId(String openId) {

        GetUnionIdRequest getUnionIdRequest = new GetUnionIdRequest();
        getUnionIdRequest.setOpenid(openId);
        //获取 BaiduAccessToken
        String baiduAccessToken = getBaiduAccessToken();
        log.info("reqUnionId[{}][{}]", baiduAccessToken, getUnionIdRequest);
        GetUnionIdResponse rsp = remoteBaiduMpService.reqUnionId(URI.create(baiduMpProperties.getMpUrl()), baiduAccessToken, getUnionIdRequest);
        log.info("rspUnionId[{}]",rsp);
        return rsp;
    }

    /**
     * 通过sessionKey 解密手机号
     * @param baiduPhoneNoDTO
     * @return
     */
    public BaiduPhoneNoVO decodeBaiduPhoneNo(BaiduPhoneNoDTO baiduPhoneNoDTO) {
        log.info("[{}]", baiduPhoneNoDTO);
        String decodeStr;
        try {
            decodeStr = BaiduAESUtils.decrypt(baiduPhoneNoDTO.getEncryptedData(), baiduPhoneNoDTO.getSessionKey(), baiduPhoneNoDTO.getIvStr());
            JSONObject decodeObj = JSONObject.parseObject(decodeStr);
            BaiduPhoneNoVO baiduPhoneNoVO = new BaiduPhoneNoVO();
            baiduPhoneNoVO.setPhoneNo(decodeObj.getString("mobile"));
            log.info("[{}]", baiduPhoneNoVO);
            return baiduPhoneNoVO;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * 通过code 解密手机号
     * @param baiduPhoneNoBizDTO
     * @return
     */
    public BaiduPhoneNoBizVO decodeBaiduPhoneNoBiz(BaiduPhoneNoBizDTO baiduPhoneNoBizDTO) {
        log.info("decodeBaiduPhoneNoBiz req[{}]", baiduPhoneNoBizDTO);
        GetSessionKeyV2Response getSessionKeyV2Response = handleSessionKey(baiduPhoneNoBizDTO.getCode());
        if (Objects.isNull(getSessionKeyV2Response) || Objects.isNull(getSessionKeyV2Response.getData())
            || Objects.isNull(getSessionKeyV2Response.getData().getSessionKey())) {
            return null;
        }
        BaiduPhoneNoDTO baiduPhoneNoDTO = new BaiduPhoneNoDTO();
        baiduPhoneNoDTO.setSessionKey(getSessionKeyV2Response.getData().getSessionKey());
        baiduPhoneNoDTO.setEncryptedData(baiduPhoneNoBizDTO.getEncryptedData());
        baiduPhoneNoDTO.setIvStr(baiduPhoneNoBizDTO.getIvStr());
        BaiduPhoneNoVO baiduPhoneNoVO = decodeBaiduPhoneNo(baiduPhoneNoDTO);
        if (Objects.isNull(baiduPhoneNoVO)) {
            log.info("decodeBaiduPhoneNoBiz rsp is null");
            return null;
        }
        BaiduPhoneNoBizVO baiduPhoneNoBizVO = new BaiduPhoneNoBizVO();
        baiduPhoneNoBizVO.setPhoneNo(baiduPhoneNoVO.getPhoneNo());
        baiduPhoneNoBizVO.setOpenId(getSessionKeyV2Response.getData().getOpenId());
        log.info("decodeBaiduPhoneNoBiz rsp[{}]", baiduPhoneNoBizVO);
        return baiduPhoneNoBizVO;
    }

}

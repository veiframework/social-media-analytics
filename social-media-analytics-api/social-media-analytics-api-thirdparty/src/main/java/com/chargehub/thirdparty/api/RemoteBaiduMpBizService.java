package com.chargehub.thirdparty.api;

import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.thirdparty.api.domain.dto.baidu.BaiduPhoneNoBizDTO;
import com.chargehub.thirdparty.api.domain.dto.baidu.BaiduPhoneNoDTO;
import com.chargehub.thirdparty.api.domain.dto.baidu.GetSessionKeyV2Response;
import com.chargehub.thirdparty.api.domain.vo.baidu.BaiduPhoneNoBizVO;
import com.chargehub.thirdparty.api.domain.vo.baidu.BaiduPhoneNoVO;
import com.chargehub.thirdparty.api.factory.RemoteBaiduMpBizServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description: 百度
 * @author: lfy
 * @create: 2024-09-09 10:02
 */
@FeignClient(contextId = "remoteBaiduMPBizService", name = ServiceNameConstants.THIRD_PARTY_SERVICE, fallbackFactory = RemoteBaiduMpBizServiceFallbackFactory.class)
public interface RemoteBaiduMpBizService {

    @RequestMapping(value = "/baiduMpBiz/generateBaiduAccessTokenAndSetCache", method = RequestMethod.GET)
    String generateBaiduAccessTokenAndSetCache();

    @RequestMapping(value = "/baiduMpBiz/handleSessionKey", method = RequestMethod.GET)
    GetSessionKeyV2Response handleSessionKey(@RequestParam("code") String code);

    @RequestMapping(value = "/baiduMpBiz/baidu-ma/resolvePhoneNo",  method = RequestMethod.GET)
    BaiduPhoneNoVO resolvePhoneNo(@SpringQueryMap BaiduPhoneNoDTO baiduPhoneNoDTO);

    @RequestMapping(value = "/baiduMpBiz/baidu-ma/resolvePhoneNoBiz",  method = RequestMethod.GET)
    BaiduPhoneNoBizVO resolvePhoneNoBiz(@SpringQueryMap BaiduPhoneNoBizDTO baiduPhoneNoBizDTO);



}

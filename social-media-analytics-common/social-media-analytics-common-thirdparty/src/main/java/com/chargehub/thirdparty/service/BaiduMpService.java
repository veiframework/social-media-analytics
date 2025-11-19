package com.chargehub.thirdparty.service;

import com.chargehub.thirdparty.api.domain.dto.baidu.*;
import com.chargehub.thirdparty.api.domain.vo.baidu.BaiduPhoneNoBizVO;
import com.chargehub.thirdparty.api.domain.vo.baidu.BaiduPhoneNoVO;

/**
 * @description: 百度
 * @author: lfy
 * @create: 2024-09-09 10:39
 */
public interface BaiduMpService {

    GetAccessTokenResponse generateBaiduAccessToken();

    String generateBaiduAccessTokenAndSetCache();

    String getBaiduAccessToken();

    GetSessionKeyV2Response handleSessionKey(String code);

    GetUnionIdResponse handleUnionId(String openId);

    BaiduPhoneNoVO decodeBaiduPhoneNo(BaiduPhoneNoDTO baiduPhoneNoDTO);

    BaiduPhoneNoBizVO decodeBaiduPhoneNoBiz(BaiduPhoneNoBizDTO baiduPhoneNoBizDTO);
}

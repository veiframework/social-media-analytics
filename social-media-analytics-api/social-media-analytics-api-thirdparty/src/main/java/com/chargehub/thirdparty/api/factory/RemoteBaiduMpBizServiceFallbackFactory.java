package com.chargehub.thirdparty.api.factory;

import com.chargehub.thirdparty.api.RemoteBaiduMpBizService;
import com.chargehub.thirdparty.api.domain.dto.baidu.BaiduPhoneNoBizDTO;
import com.chargehub.thirdparty.api.domain.dto.baidu.BaiduPhoneNoDTO;
import com.chargehub.thirdparty.api.domain.dto.baidu.GetSessionKeyV2Response;
import com.chargehub.thirdparty.api.domain.vo.baidu.BaiduPhoneNoBizVO;
import com.chargehub.thirdparty.api.domain.vo.baidu.BaiduPhoneNoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @description: 百度业务
 * @author: lfy
 * @create: 2024-09-09 10:07
 */
@Slf4j
public class RemoteBaiduMpBizServiceFallbackFactory implements FallbackFactory<RemoteBaiduMpBizService> {

    @Override
    public RemoteBaiduMpBizService create(Throwable cause) {
        log.error("百度业务:{}", cause.getMessage(), cause);
        return new RemoteBaiduMpBizService() {
            @Override
            public String generateBaiduAccessTokenAndSetCache() {
                return null;
            }

            @Override
            public GetSessionKeyV2Response handleSessionKey(String code) {
                return null;
            }

            @Override
            public BaiduPhoneNoVO resolvePhoneNo(BaiduPhoneNoDTO baiduPhoneNoDTO) {
                return null;
            }

            @Override
            public BaiduPhoneNoBizVO resolvePhoneNoBiz(BaiduPhoneNoBizDTO baiduPhoneNoBizDTO) {
                return null;
            }

        };
    }
}

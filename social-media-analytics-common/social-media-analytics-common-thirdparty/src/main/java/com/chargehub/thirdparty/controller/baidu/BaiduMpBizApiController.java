package com.chargehub.thirdparty.controller.baidu;

import com.chargehub.thirdparty.api.domain.dto.baidu.*;
import com.chargehub.thirdparty.api.domain.vo.baidu.BaiduPhoneNoBizVO;
import com.chargehub.thirdparty.api.domain.vo.baidu.BaiduPhoneNoVO;
import com.chargehub.thirdparty.service.BaiduMpService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/baiduMpBiz")
@Api(tags = "百度小程序业务API接口", description = "百度小程序业务API接口")
public class BaiduMpBizApiController {

    private final BaiduMpService baiduMpService;

    /**
     * 生成baidu accessToken 并设置缓存
     * @return
     */
    @GetMapping("/generateBaiduAccessTokenAndSetCache")
    public String generateBaiduAccessTokenAndSetCache() {
        return baiduMpService.generateBaiduAccessTokenAndSetCache();
    }

    /**
     * 生成 baidu accessToken
     * @return
     */
    @GetMapping("/generateBaiduAccessToken")
    public GetAccessTokenResponse generateBaiduAccessToken() {

        //SmartAppGetAccessToken.getAccessToken(param);
        return baiduMpService.generateBaiduAccessToken();
    }

    /**
     * 主动获取 baidu accessToken值
     * @return
     */
    @GetMapping("/queryBaiduAccessToken")
    public String queryBaiduAccessToken() {

        //SmartAppGetAccessToken.getAccessToken(param);
        return baiduMpService.getBaiduAccessToken();
    }

    /**
     * 获取百度sessionKey
     * @param code
     * @return
     */
    @GetMapping("/handleSessionKey")
    public GetSessionKeyV2Response handleSessionKey(@RequestParam("code") String code) {
        //GetSessionKeyV2Request param = new GetSessionKeyV2Request();
        //param.setCode(code);
        return baiduMpService.handleSessionKey(code);
    }

    /**
     * 获取百度unionId
     * @param openId
     * @return
     */
    @GetMapping("/handleUnionId")
    public GetUnionIdResponse handleUnionId(@RequestParam("openId") String openId) {
        return baiduMpService.handleUnionId(openId);
    }

    /**
     * 有sessionKey解密百度手机号
     * @param baiduPhoneNoDTO
     * @return
     */
    @GetMapping(value = "/baidu-ma/resolvePhoneNo")
    BaiduPhoneNoVO resolvePhoneNo(BaiduPhoneNoDTO baiduPhoneNoDTO) {
        return baiduMpService.decodeBaiduPhoneNo(baiduPhoneNoDTO);
    }

    /**
     * 有code解密百度手机号
     * @param baiduPhoneNoBizDTO
     * @return
     */
    @GetMapping(value = "/baidu-ma/resolvePhoneNoBiz")
    BaiduPhoneNoBizVO resolvePhoneNoBiz(BaiduPhoneNoBizDTO baiduPhoneNoBizDTO) {
        return baiduMpService.decodeBaiduPhoneNoBiz(baiduPhoneNoBizDTO);
    }


}

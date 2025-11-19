package com.chargehub.thirdparty.controller.wx.open;


import com.alibaba.fastjson.JSONObject;

import com.chargehub.thirdparty.api.domain.dto.logistics.*;
import com.chargehub.thirdparty.api.domain.vo.logistics.*;
import com.chargehub.thirdparty.config.wx.ma.WxMaConfiguration;
import com.chargehub.thirdparty.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.open.api.WxOpenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


/**
 * 微信开放平台  物流助手Api
 */
@Slf4j
@RestController
@RequestMapping("/wx/open-logistics-api")
public class WxOpenLogisticsApiController {


    /**
     * 绑定/解绑物流账号
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/express/express-by-business/bindAccount.html
     *
     * @param maAppId
     * @param loAccountBindParam
     * @return
     */
    @PostMapping("/account/bind")
    public LoAccountBindVo accountBind(@RequestParam("maAppId") String maAppId, @RequestBody LoAccountBindParam loAccountBindParam) {
        try {

            String accessToken = WxMaConfiguration.getMaService(maAppId).getAccessToken();
            if (StringUtils.isEmpty(accessToken)) {
                log.info("【微信开放平台 - 物流助手】获取小程序access_token失败，小程序appId：{}", maAppId);
                return null;
            }

            String url = "https://api.weixin.qq.com/cgi-bin/express/business/account/bind?access_token=" + accessToken;
            String paramJsonStr = JSONObject.toJSONString(loAccountBindParam);
            String resultJsonStr = HttpClientUtil.doPostJson(url, paramJsonStr);

            log.info("【微信开放平台 - 物流助手】接口名称：/account/bind  请求参数：{}，返回结果：{}", paramJsonStr, resultJsonStr);
            LoAccountBindVo loAccountBindVo = JSONObject.parseObject(resultJsonStr, LoAccountBindVo.class);
            return loAccountBindVo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取所有绑定的物流账号
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/express/express-by-business/getAllAccount.html
     *
     * @param maAppId
     * @return
     */
    @GetMapping("/account/getall")
    public LoAccountGetallVo accountGetall(@RequestParam("maAppId") String maAppId) {
        try {
            String accessToken = WxMaConfiguration.getMaService(maAppId).getAccessToken();
            if (StringUtils.isEmpty(accessToken)) {
                log.info("【微信开放平台 - 物流助手】获取小程序access_token失败，小程序appId：{}", maAppId);
                return null;
            }

            String url = "https://api.weixin.qq.com/cgi-bin/express/business/account/getall?access_token=" + accessToken;
            String resultJsonStr = HttpClientUtil.doGet(url);

            log.info("【微信开放平台 - 物流助手】接口名称：/account/getall  返回结果：{}", resultJsonStr);
            LoAccountGetallVo loAccountGetallVo = JSONObject.parseObject(resultJsonStr, LoAccountGetallVo.class);
            return loAccountGetallVo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取支持的快递公司列表
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/express/express-by-business/getAllDelivery.html
     *
     * @param maAppId
     * @return
     */
    @GetMapping("/delivery/getall")
    public LoDeliveryGetallVo deliveryGetall(@RequestParam("maAppId") String maAppId) {
        try {
            String accessToken = WxMaConfiguration.getMaService(maAppId).getAccessToken();
            if (StringUtils.isEmpty(accessToken)) {
                log.info("【微信开放平台 - 物流助手】获取小程序access_token失败，小程序appId：{}", maAppId);
                return null;
            }

            String url = "https://api.weixin.qq.com/cgi-bin/express/business/delivery/getall?access_token=" + accessToken;
            String resultJsonStr = HttpClientUtil.doGet(url);

            log.info("【微信开放平台 - 物流助手】接口名称：/delivery/getall  返回结果：{}", resultJsonStr);
            LoDeliveryGetallVo loDeliveryGetallVo = JSONObject.parseObject(resultJsonStr, LoDeliveryGetallVo.class);
            return loDeliveryGetallVo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 取消运单
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/express/express-by-business/cancelOrder.html
     *
     * @param maAppId
     * @param loOrderCancelParam
     * @return
     */
    @PostMapping("/order/cancel")
    public LoOrderCancelVo orderCancel(@RequestParam("maAppId") String maAppId, @RequestBody LoOrderCancelParam loOrderCancelParam) {
        try {
            String accessToken = WxMaConfiguration.getMaService(maAppId).getAccessToken();
            if (StringUtils.isEmpty(accessToken)) {
                log.info("【微信开放平台 - 物流助手】获取小程序access_token失败，小程序appId：{}", maAppId);
                return null;
            }

            String url = "https://api.weixin.qq.com/cgi-bin/express/business/order/cancel?access_token=" + accessToken;
            String paramJsonStr = JSONObject.toJSONString(loOrderCancelParam);
            String resultJsonStr = HttpClientUtil.doPostJson(url, paramJsonStr);

            log.info("【微信开放平台 - 物流助手】接口名称：/order/cancel  返回结果：{}", resultJsonStr);
            LoOrderCancelVo loOrderCancelVo = JSONObject.parseObject(resultJsonStr, LoOrderCancelVo.class);
            return loOrderCancelVo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取电子面单余额
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/express/express-by-business/getQuota.html
     *
     * @param maAppId
     * @param loQuotaGetParam
     * @return
     */
    @PostMapping("/quota/get")
    public LoQuotaGetVo quotaGet(@RequestParam("maAppId") String maAppId, @RequestBody LoQuotaGetParam loQuotaGetParam) {
        try {
            String accessToken = WxMaConfiguration.getMaService(maAppId).getAccessToken();
            if (StringUtils.isEmpty(accessToken)) {
                log.info("【微信开放平台 - 物流助手】获取小程序access_token失败，小程序appId：{}", maAppId);
                return null;
            }

            String url = "https://api.weixin.qq.com/cgi-bin/express/business/quota/get?access_token=" + accessToken;
            String paramJsonStr = JSONObject.toJSONString(loQuotaGetParam);
            String resultJsonStr = HttpClientUtil.doPostJson(url, paramJsonStr);

            log.info("【微信开放平台 - 物流助手】接口名称：/quota/get  返回结果：{}", resultJsonStr);
            LoQuotaGetVo loQuotaGetVo = JSONObject.parseObject(resultJsonStr, LoQuotaGetVo.class);
            return loQuotaGetVo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取运单数据
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/express/express-by-business/getOrder.html
     *
     * @param maAppId
     * @param loOrderGetParam
     * @return
     */
    @PostMapping("/order/get")
    public LoOrderGetVo orderGet(@RequestParam("maAppId") String maAppId, @RequestBody LoOrderGetParam loOrderGetParam) {
        try {
            String accessToken = WxMaConfiguration.getMaService(maAppId).getAccessToken();
            if (StringUtils.isEmpty(accessToken)) {
                log.info("【微信开放平台 - 物流助手】获取小程序access_token失败，小程序appId：{}", maAppId);
                return null;
            }

            String url = "https://api.weixin.qq.com/cgi-bin/express/business/order/get?access_token=" + accessToken;
            String paramJsonStr = JSONObject.toJSONString(loOrderGetParam);
            String resultJsonStr = HttpClientUtil.doPostJson(url, paramJsonStr);

            log.info("【微信开放平台 - 物流助手】接口名称：/order/get  返回结果：{}", resultJsonStr);
            LoOrderGetVo loOrderGetVo = JSONObject.parseObject(resultJsonStr, LoOrderGetVo.class);
            return loOrderGetVo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取运单数据
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/express/express-by-business/getPath.html
     *
     * @param maAppId
     * @param loPathGetParam
     * @return
     */
    @PostMapping("/path/get")
    public LoPathGetVo pathGet(@RequestParam("maAppId") String maAppId, @RequestBody LoPathGetParam loPathGetParam) {
        try {
            String accessToken = WxMaConfiguration.getMaService(maAppId).getAccessToken();
            if (StringUtils.isEmpty(accessToken)) {
                log.info("【微信开放平台 - 物流助手】获取小程序access_token失败，小程序appId：{}", maAppId);
                return null;
            }

            String url = "https://api.weixin.qq.com/cgi-bin/express/business/path/get?access_token=" + accessToken;
            String paramJsonStr = JSONObject.toJSONString(loPathGetParam);
            String resultJsonStr = HttpClientUtil.doPostJson(url, paramJsonStr);

            log.info("【微信开放平台 - 物流助手】接口名称：/path/get  返回结果：{}", resultJsonStr);
            LoPathGetVo loPathGetVo = JSONObject.parseObject(resultJsonStr, LoPathGetVo.class);
            return loPathGetVo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 添加运单
     *
     * @param maAppId
     * @param loOrderAddParam
     * @return
     */
    @PostMapping("/order/add")
    public LoOrderAddVo orderAdd(@RequestParam("maAppId") String maAppId, @RequestBody LoOrderAddParam loOrderAddParam) {
        try {
            String accessToken = WxMaConfiguration.getMaService(maAppId).getAccessToken();

            if (StringUtils.isEmpty(accessToken)) {
                log.info("【微信开放平台 - 物流助手】获取小程序access_token失败，小程序appId：{}", maAppId);
                return null;
            }

            String url = "https://api.weixin.qq.com/cgi-bin/express/business/order/add?access_token=" + accessToken;
            String paramJsonStr = JSONObject.toJSONString(loOrderAddParam);
            String resultJsonStr = HttpClientUtil.doPostJson(url, paramJsonStr);

            log.info("【微信开放平台 - 物流助手】接口名称：/path/get  返回结果：{}", resultJsonStr);
            LoOrderAddVo loOrderAddVo = JSONObject.parseObject(resultJsonStr, LoOrderAddVo.class);
            return loOrderAddVo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}

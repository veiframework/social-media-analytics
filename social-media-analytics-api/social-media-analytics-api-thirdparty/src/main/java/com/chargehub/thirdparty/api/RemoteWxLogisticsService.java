package com.chargehub.thirdparty.api;

import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.thirdparty.api.config.BaiduFeignConfig;
import com.chargehub.thirdparty.api.domain.dto.logistics.*;
import com.chargehub.thirdparty.api.domain.vo.logistics.*;
import com.chargehub.thirdparty.api.factory.RemoteBaiduMpServiceFallbackFactory;
import com.chargehub.thirdparty.api.factory.RemoteWxLogisticsServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Zhanghaowei
 * @date 2025/08/25 16:33
 */
@FeignClient(contextId = "remoteWxLogisticsService", name = ServiceNameConstants.THIRD_PARTY_SERVICE, fallbackFactory = RemoteWxLogisticsServiceFallbackFactory.class)
public interface RemoteWxLogisticsService {


    /**
     * 绑定/解绑物流账号
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/express/express-by-business/bindAccount.html
     *
     * @param maAppId
     * @param loAccountBindParam
     * @return
     */
    @PostMapping("/wx/open-logistics-api/account/bind")
    public LoAccountBindVo accountBind(@RequestParam("maAppId") String maAppId, @RequestBody LoAccountBindParam loAccountBindParam);


    /**
     * 获取所有绑定的物流账号
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/express/express-by-business/getAllAccount.html
     *
     * @param maAppId
     * @return
     */
    @GetMapping("/wx/open-logistics-api/account/getall")
    public LoAccountGetallVo accountGetall(@RequestParam("maAppId") String maAppId);


    /**
     * 获取支持的快递公司列表
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/express/express-by-business/getAllDelivery.html
     *
     * @param maAppId
     * @return
     */
    @GetMapping("/wx/open-logistics-api/delivery/getall")
    public LoDeliveryGetallVo deliveryGetall(@RequestParam("maAppId") String maAppId);


    /**
     * 取消运单
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/express/express-by-business/cancelOrder.html
     *
     * @param maAppId
     * @param loOrderCancelParam
     * @return
     */
    @PostMapping("/wx/open-logistics-api/order/cancel")
    public LoOrderCancelVo orderCancel(@RequestParam("maAppId") String maAppId, @RequestBody LoOrderCancelParam loOrderCancelParam);


    /**
     * 获取电子面单余额
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/express/express-by-business/getQuota.html
     *
     * @param maAppId
     * @param loQuotaGetParam
     * @return
     */
    @PostMapping("/wx/open-logistics-api/quota/get")
    public LoQuotaGetVo quotaGet(@RequestParam("maAppId") String maAppId, @RequestBody LoQuotaGetParam loQuotaGetParam);


    /**
     * 获取运单数据
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/express/express-by-business/getOrder.html
     *
     * @param maAppId
     * @param loOrderGetParam
     * @return
     */
    @PostMapping("/wx/open-logistics-api/order/get")
    public LoOrderGetVo orderGet(@RequestParam("maAppId") String maAppId, @RequestBody LoOrderGetParam loOrderGetParam);


    /**
     * 获取运单数据
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/express/express-by-business/getPath.html
     *
     * @param maAppId
     * @param loPathGetParam
     * @return
     */
    @PostMapping("/wx/open-logistics-api/path/get")
    public LoPathGetVo pathGet(@RequestParam("maAppId") String maAppId, @RequestBody LoPathGetParam loPathGetParam);


    /**
     * 添加运单
     *
     * @param maAppId
     * @param loOrderAddParam
     * @return
     */
    @PostMapping("/wx/open-logistics-api/order/add")
    public LoOrderAddVo orderAdd(@RequestParam("maAppId") String maAppId, @RequestBody LoOrderAddParam loOrderAddParam);


}

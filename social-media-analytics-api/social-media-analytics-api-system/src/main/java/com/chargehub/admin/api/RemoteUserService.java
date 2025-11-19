package com.chargehub.admin.api;

import com.chargehub.admin.api.domain.SysUser;
import com.chargehub.admin.api.factory.RemoteUserFallbackFactory;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.admin.api.model.StationDataStatisticReqDTO;
import com.chargehub.admin.api.model.StationDataStatisticRspDTO;
import com.chargehub.common.core.constant.SecurityConstants;
import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.common.core.domain.R;
import com.chargehub.common.core.web.domain.AjaxResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务
 *
 * @author ruoyi
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService {
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @param source   请求来源
     * @return 结果
     */
    @GetMapping("/user/info/{username}")
    public R<LoginUser> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 更新用户最后一次登录信息
     *
     * @param userId
     * @param ipAddress
     * @param source
     * @return
     */
    @PostMapping("/user/last-login-info/{userId}")
    public R<Boolean> updateLastLoginInfo(@PathVariable("userId") Long userId, @RequestParam("ipAddress") String ipAddress,
                                          @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 通过用户名查询用户信息
     *
     * @param userid 用户名
     * @param source 请求来源
     * @return 结果
     */
    @GetMapping("/user/info_by_id/{userid}")
    public R<LoginUser> getUserInfoById(@PathVariable("userid") String userid, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 注册用户信息
     *
     * @param sysUser 用户信息
     * @param source  请求来源
     * @return 结果
     */
    @PostMapping("/user/register")
    public R<Boolean> registerUserInfo(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 发票自动审批
     *
     * @return
     */
    @PostMapping("/sys/chargInvoice/task")
    AjaxResult chargeInvoiceTask(@RequestParam(value = "invoiceId", required = false) String invoiceId, @RequestParam(value = "invoiceNum", required = false) String invoiceNum);

    @PostMapping("/sys/chargInvoice/approval/task")
    AjaxResult chargeInvoiceApprovalTask(@RequestParam(value = "interval") int interval);


    @GetMapping("/sys/statistic/charge-fail/task")
    AjaxResult chargeFailStatistic(@RequestParam("startTime") String startTime,
                                   @RequestParam("endTime") String endTime);


    @GetMapping("/sys/statistic/marketing-income/task")
    AjaxResult chargeMarketingIncomeStatistic(@RequestParam("indexType") int indexType,
                                              @RequestParam("indexTime") String indexTime);

    @GetMapping("/sys/statistic/marketing-data/task")
    AjaxResult chargeMarketingDataStatistic(@RequestParam("indexType") int indexType,
                                            @RequestParam("indexTime") String indexTime);


    @GetMapping("/sys/statistic/finance/task")
    AjaxResult chargeFinanceInnerStatistic(@RequestParam("indexTime") String indexTime);

    @GetMapping("/sys/statistic/finance-outer/task")
    AjaxResult chargeFinanceOuterStatistic(@RequestParam("indexTime") String indexTime);

    @RequestMapping(value = "/api/division/divisionHandle", method = RequestMethod.GET)
    AjaxResult divisionHandle();

    @RequestMapping(value = "/sys/statistic/marketing-data/queryStationDataStatistic", method = RequestMethod.GET)
    StationDataStatisticRspDTO queryStationDataStatistic(@SpringQueryMap StationDataStatisticReqDTO stationDataStatisticReq);
    //StationDataStatisticRspDTO queryStationDataStatistic(@RequestParam("stationId") String stationId, @RequestParam("indexType")String indexType, @RequestParam("indexDay")String indexDay);

}

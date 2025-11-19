package com.chargehub.admin.api.factory;

import com.chargehub.admin.api.RemoteUserService;
import com.chargehub.admin.api.domain.SysUser;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.admin.api.model.StationDataStatisticReqDTO;
import com.chargehub.admin.api.model.StationDataStatisticRspDTO;
import com.chargehub.common.core.domain.R;
import com.chargehub.common.core.web.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务降级处理
 *
 * @author ruoyi
 */
@Component
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteUserFallbackFactory.class);

    @Override
    public RemoteUserService create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteUserService() {
            @Override
            public R<LoginUser> getUserInfo(String username, String source) {
                return R.fail("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> updateLastLoginInfo(Long userId, String ipAddress, String source) {
                return R.fail("更新最后登录信息失败:" + throwable.getMessage());
            }

            @Override
            public R<LoginUser> getUserInfoById(String userid, String source) {
                return R.fail("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> registerUserInfo(SysUser sysUser, String source) {
                return R.fail("注册用户失败:" + throwable.getMessage());
            }

            @Override
            public AjaxResult chargeInvoiceTask(String invoiceId, String invoiceNum) {
               return AjaxResult.error(throwable.getMessage());
            }

            @Override
            public AjaxResult chargeInvoiceApprovalTask(int interval) {
                return AjaxResult.error(throwable.getMessage());
            }

            @Override
            public AjaxResult chargeFailStatistic(String startTime, String endTime) {
                throw new IllegalArgumentException(throwable.getMessage());
            }

            @Override
            public AjaxResult chargeMarketingIncomeStatistic(int indexType, String indexTime) {
                return AjaxResult.error(throwable.getMessage());
            }

            @Override
            public AjaxResult chargeMarketingDataStatistic(int indexType, String indexTime) {
                return AjaxResult.error(throwable.getMessage());
            }

            @Override
            public AjaxResult chargeFinanceInnerStatistic(String indexTime) {
                return AjaxResult.error(throwable.getMessage());
            }

            @Override
            public AjaxResult chargeFinanceOuterStatistic(String indexTime) {
                return AjaxResult.error(throwable.getMessage());
            }

            @Override
            public AjaxResult divisionHandle() {
                return null;
            }

            /*@Override
            public StationDataStatisticRspDTO queryStationDataStatistic(String stationId, String indexType, String indexDay) {
                return null;
            }*/

            @Override
            public StationDataStatisticRspDTO queryStationDataStatistic(StationDataStatisticReqDTO stationDataStatisticReq) {
                return null;
            }

            //@Override
            //public StationDataStatisticRsp queryStationDataStatistic(StationDataStatisticReq stationDataStatisticReq) {
            //    return null;
            //
        };
    }
}

package com.chargehub.admin.datasync;

import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.domain.*;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.scheduler.DouYinWorkScheduler;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.microsoft.playwright.BrowserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
public interface DataSyncService {

    Logger log = LoggerFactory.getLogger(DataSyncService.class);


    SocialMediaPlatformEnum platform();

    @Deprecated
    SocialMediaUserInfo getSocialMediaUserInfo(String secUserId);

    <T> SocialMediaWorkResult<T> getWorks(SocialMediaAccountVo socialMediaAccount, String cursor, Integer count);

    /**
     * v3版本数据同步
     * 返回的作品list如果是空则不更新cookie
     *
     * @param params
     * @param <T>
     * @return
     */
    <T> SocialMediaWorkResult<T> getWorks(DataSyncWorksParams params);


    <T> SocialMediaWorkDetail<T> getWork(DataSyncParamContext dataSyncParamContext);

    <T> SocialMediaWorkDetail<T> fetchWork(DataSyncParamContext dataSyncParamContext);

    @SuppressWarnings("unchecked")
    default <T> SocialMediaWorkResult<T> fetchWorks(DataSyncWorksParams dataSyncWorksParams) {
        BrowserContext browserContext = dataSyncWorksParams.getBrowserContext();
        Map<String, SocialMediaWork> workMap = dataSyncWorksParams.getWorkMap();
        SocialMediaWorkResult<SocialMediaWork> socialMediaWorkResult = new SocialMediaWorkResult<>();
        List<SocialMediaWork> socialMediaWorks = new ArrayList<>();
        workMap.forEach((k, v) -> {
            String mediaType = v.getMediaType();
            DataSyncParamContext dataSyncParamContext = new DataSyncParamContext();
            dataSyncParamContext.setShareLink(v.getShareLink());
            dataSyncParamContext.setBrowserContext(browserContext);
            dataSyncParamContext.setScheduler(true);
            dataSyncParamContext.setMediaType(mediaType);
            dataSyncParamContext.setProxy(dataSyncWorksParams.getProxy());
            dataSyncParamContext.setStorageState(dataSyncWorksParams.getStorageState());
            dataSyncParamContext.setWorkUid(v.getWorkUid());
            try {
                SocialMediaWorkDetail<SocialMediaWork> workDetail = this.fetchWork(dataSyncParamContext);
                if (workDetail == null) {
                    return;
                }
                socialMediaWorks.add(workDetail.getWork());
            } catch (Exception e) {
                log.error("{}获取作品失败, {}", v.getShareLink(), e);
            }
        });
        socialMediaWorkResult.setWorks(socialMediaWorks);
        return (SocialMediaWorkResult<T>) socialMediaWorkResult;
    }

}

package com.chargehub.admin.scheduler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.service.SocialMediaAccountTaskService;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.DataSyncWorksParams;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.SyncWorkStatusEnum;
import com.chargehub.admin.enums.WorkStateEnum;
import com.chargehub.admin.playwright.BrowserConfig;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.service.SocialMediaWorkCreateService;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.core.utils.file.FileUtils;
import com.chargehub.common.redis.service.RedisService;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Order
@Slf4j
@Component
public class DouYinWorkScheduler extends AbstractWorkScheduler {

    public static final String DOUYIN_WEB_RESOURCE_PATH = WEB_RESOURCE_PATH + "/web-douyin";

    public static final String DOUYIN_USER_PAGE = "https://www.douyin.com/user/self";

    private static final Map<String, byte[]> LOCAL_CACHE = new ConcurrentHashMap<>();

    protected DouYinWorkScheduler(SocialMediaAccountTaskService socialMediaAccountTaskService, RedisService redisService, DataSyncManager dataSyncManager, SocialMediaAccountService socialMediaAccountService, SocialMediaWorkService socialMediaWorkService, SocialMediaWorkCreateService socialMediaWorkCreateService, HubProperties hubProperties) {
        super(socialMediaAccountTaskService, redisService, dataSyncManager, socialMediaAccountService, socialMediaWorkService, socialMediaWorkCreateService, hubProperties, 10);
        this.setTaskName(SocialMediaPlatformEnum.DOU_YIN.getDomain());
        List<File> files = FileUtil.loopFiles(new File(DOUYIN_WEB_RESOURCE_PATH));
        for (File file : files) {
            String name = file.getName();
            byte[] bytes = FileUtil.readBytes(file);
            LOCAL_CACHE.put(name, bytes);
        }
        log.info("douyin crawler web resource load complete length: " + LOCAL_CACHE.size());
    }

    @Override
    public void fetchWorks(SocialMediaAccount socialMediaAccountVo, Proxy proxy, com.microsoft.playwright.options.Proxy browserProxy) {
        String accountId = socialMediaAccountVo.getId();
        String platformId = socialMediaAccountVo.getPlatformId();
        String secUid = socialMediaAccountVo.getSecUid();
        List<SocialMediaWork> latestWork = this.socialMediaWorkService.getLatestWork(accountId);
        if (CollectionUtils.isEmpty(latestWork)) {
            return;
        }
        DataSyncWorksParams dataSyncWorksParams = new DataSyncWorksParams();
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(browserProxy)) {
            Page page0 = navigateToDouYinUserPage(playwrightBrowser, proxy);
            this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.SYNCING);
            Map<String, SocialMediaWork> workMap = new HashMap<>();
            for (SocialMediaWork socialMediaWork : latestWork) {
                workMap.put(socialMediaWork.getWorkUid(), socialMediaWork);
            }
            dataSyncWorksParams.setSecUid(secUid);
            dataSyncWorksParams.setWorkMap(workMap);
            dataSyncWorksParams.setProxy(proxy);
            dataSyncWorksParams.setPage(page0);
            SocialMediaWorkResult<SocialMediaWork> result = this.dataSyncManager.fetchWorks(platformId, dataSyncWorksParams);
            List<SocialMediaWork> newWorks = result.getWorks();
            if (CollectionUtils.isEmpty(newWorks)) {
                return;
            }
            List<SocialMediaWork> updateList = new ArrayList<>();
            for (SocialMediaWork newWork : newWorks) {
                String workUid = newWork.getWorkUid();
                if ("-1".equals(workUid)) {
                    String shareLink = newWork.getShareLink();
                    log.error("删除作品分享链接:" + shareLink);
                    this.socialMediaWorkService.updateStateByShareLink(shareLink, WorkStateEnum.DELETED);
                } else {
                    SocialMediaWork existWork = workMap.get(workUid);
                    if (existWork == null) {
                        continue;
                    }
                    SocialMediaWork updateWork = existWork.computeMd5(newWork);
                    if (updateWork != null) {
                        updateList.add(updateWork);
                    }
                }
            }
            this.socialMediaWorkService.saveOrUpdateBatch(updateList);
        }
    }


    public static Page navigateToDouYinUserPage(PlaywrightBrowser playwrightBrowser, Proxy proxy) {
        BrowserContext browserContext = playwrightBrowser.getBrowserContext();
        browserContext.addInitScript("localStorage.clear(); sessionStorage.clear();");
        browserContext.onConsoleMessage(msg -> {
            String text = msg.text();
            if (text.contains("[douyin]")) {
                log.error("抖音同步任务请求接口失败:" + text);
            }
        });
        browserContext.route("**/*", route -> {
            Request request = route.request();
            String resourceType = request.resourceType();
            String url = request.url();
            boolean isDocument = "document".equals(resourceType);
            if (isDocument) {
                route.resume();
                return;
            }
            boolean isScript = "script".equals(resourceType);
            if (isScript) {
                String fileName = FileUtils.sanitizeUrlFileName(url);
                byte[] cachedWebResource = LOCAL_CACHE.get(fileName);
                if (cachedWebResource != null) {
                    route.fulfill(new Route.FulfillOptions()
                            .setStatus(HttpStatus.OK.value())
                            .setContentType(FastJsonHttpMessageConverter.APPLICATION_JAVASCRIPT.toString())
                            .setBodyBytes(cachedWebResource));
                    return;
                }
                APIResponse response = route.fetch(new Route.FetchOptions().setTimeout(60_000));
                if (response.ok()) {
                    byte[] body = response.body();
                    LOCAL_CACHE.put(fileName, body);
                    FileUtils.cacheWebResource(url, body, DOUYIN_WEB_RESOURCE_PATH);
                }
                route.fulfill(new Route.FulfillOptions().setResponse(response));
                return;
            }
            if (url.contains("/web/aweme/detail/")) {
                Request realRequest = route.request();
                Map<String, String> headerMap = realRequest.allHeaders();
                headerMap.put("connection", "close");
                try (HttpResponse execute = HttpUtil.createGet(realRequest.url())
                        .setProxy(proxy)
                        .timeout(30000)
                        .headerMap(headerMap, true).execute()) {
                    byte[] bodyBytes = execute.bodyBytes();
                    int status = execute.getStatus();
                    route.fulfill(new Route.FulfillOptions()
                            .setStatus(status)
                            .setContentType(MediaType.APPLICATION_JSON_VALUE)
                            .setBodyBytes(bodyBytes));
                } catch (Exception e) {
                    route.fulfill(new Route.FulfillOptions()
                            .setStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .setContentType(MediaType.APPLICATION_JSON_VALUE)
                            .setBody(""));
                }
                return;
            }
            route.abort();
        });
        Page page = playwrightBrowser.newPage();
        for (int i = 0; i <= BrowserConfig.LOAD_PAGE_RETRY; i++) {
            try {
                page.navigate(DouYinWorkScheduler.DOUYIN_USER_PAGE, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED).setTimeout(BrowserConfig.LOAD_PAGE_TIMEOUT));
                break;
            } catch (Exception e) {
                if (i == BrowserConfig.LOAD_PAGE_RETRY) {
                    page.close();
                    throw e;
                }
                browserContext.clearCookies();
                browserContext.clearPermissions();
                page.close();
                page = playwrightBrowser.newPage();
            }
        }
        return page;
    }

    public static void clearCache() {
        LOCAL_CACHE.clear();
    }
}

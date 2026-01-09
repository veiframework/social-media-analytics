package com.chargehub.admin.scheduler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.providers.base.Internet;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.Proxy;
import java.util.*;
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

    protected static final List<String> DOUYIN_PAGES = Lists.newArrayList(
            "https://www.douyin.com/user/self?from_tab_name=main&showTab=like",
            "https://www.douyin.com/follow",
            "https://www.douyin.com/friend",
            "https://www.douyin.com/help",
            "https://www.douyin.com/help?tab=no_volumn",
            "https://www.douyin.com/help?tab=not_match",
            "https://www.douyin.com/help?tab=blank_screen",
            "https://www.douyin.com/help?tab=flash_screen",
            "https://www.douyin.com/help?tab=no_network",
            "https://www.douyin.com/help?tab=video_block",
            "https://www.douyin.com/help?tab=client_network_error",
            "https://www.douyin.com/help?tab=start_error",
            "https://www.douyin.com/help?tab=flash_exit",
            "https://www.douyin.com/help?tab=popup_harassment",
            "https://www.douyin.com/help?tab=plugin_extension_conflict",
            "https://www.douyin.com/help?tab=duplicate_notification_settings",
            "https://www.douyin.com/aisearch",
            "https://www.douyin.com/?recommend=1"
    );

    protected static final Set<String> ALLOW_SCRIPTS = Sets.newHashSet("index.js", "index.umd.production.js"
            , "mammon-worklet-processor.min.js", "bdms_", "browser.cn.js", "runtime_bundler_", "webmssdk", "sdk-glue"
            , "routes-Help-route", "framework");

    private static final Map<String, byte[]> CHROME_CACHE = new ConcurrentHashMap<>();

    private static final Map<String, byte[]> FIREFOX_CACHE = new ConcurrentHashMap<>();

    private static final Map<String, byte[]> WEBKIT_CACHE = new ConcurrentHashMap<>();


    protected DouYinWorkScheduler(SocialMediaAccountTaskService socialMediaAccountTaskService, RedisService redisService, DataSyncManager dataSyncManager, SocialMediaAccountService socialMediaAccountService, SocialMediaWorkService socialMediaWorkService, SocialMediaWorkCreateService socialMediaWorkCreateService, HubProperties hubProperties) {
        super(socialMediaAccountTaskService, redisService, dataSyncManager, socialMediaAccountService, socialMediaWorkService, socialMediaWorkCreateService, hubProperties, 10);
        this.setTaskName(SocialMediaPlatformEnum.DOU_YIN.getDomain());
        loadLocalCache();
    }

    public static void loadLocalCache() {
        try {
            File[] ls = FileUtil.ls(DOUYIN_WEB_RESOURCE_PATH);
            if (ls == null) {
                return;
            }
            for (File directory : ls) {
                List<File> files = FileUtil.loopFiles(directory);
                if (CollectionUtils.isEmpty(files)) {
                    continue;
                }
                String directoryName = directory.getName();
                for (File file : files) {
                    String name = file.getName();
                    byte[] bytes = FileUtil.readBytes(file);
                    putCache(directoryName, name, bytes);
                }
            }
            log.info("douyin crawler web resource load complete length: douyin:{}, firefox:{}, webkit:{}", CHROME_CACHE.size(), FIREFOX_CACHE.size(), WEBKIT_CACHE.size());
        } catch (Exception e) {
            log.error("douyin crawler web resource load error {}", e.getMessage());
        }
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
            this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.SYNCING);
            Map<String, SocialMediaWork> workMap = new HashMap<>();
            for (SocialMediaWork socialMediaWork : latestWork) {
                workMap.put(socialMediaWork.getWorkUid(), socialMediaWork);
            }
            dataSyncWorksParams.setSecUid(secUid);
            dataSyncWorksParams.setWorkMap(workMap);
            dataSyncWorksParams.setProxy(proxy);
            dataSyncWorksParams.setPlaywrightBrowser(playwrightBrowser);
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
            this.socialMediaWorkService.updateBatchById(updateList);
        }
    }


    public static void navigateToDouYinUserPage(PlaywrightBrowser playwrightBrowser) {
        BrowserContext browserContext = playwrightBrowser.getBrowserContext();
        BrowserConfig browserConfig = playwrightBrowser.getBrowserConfig();
        Internet.UserAgent userAgent = browserConfig.getUserAgent();
        browserContext.addInitScript("localStorage.clear(); sessionStorage.clear();");
        browserContext.onResponse(response -> {
            String url = response.url();
            String resourceType = response.request().resourceType();
            boolean isScript = "script".equals(resourceType);
            if (isScript && ALLOW_SCRIPTS.stream().anyMatch(url::contains) && response.ok()) {
                String fileName = FileUtils.sanitizeUrlFileName(url);
                byte[] body = response.body();
                putCache(userAgent, fileName, body);
                FileUtils.cacheWebResource(url, body, DOUYIN_WEB_RESOURCE_PATH + "/" + userAgent);
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
            if (isScript && ALLOW_SCRIPTS.stream().anyMatch(url::contains)) {
                String fileName = FileUtils.sanitizeUrlFileName(url);
                byte[] cachedWebResource = getCache(userAgent, fileName);
                if (cachedWebResource != null) {
                    route.fulfill(new Route.FulfillOptions()
                            .setStatus(HttpStatus.OK.value())
                            .setContentType(FastJsonHttpMessageConverter.APPLICATION_JAVASCRIPT.toString())
                            .setBodyBytes(cachedWebResource));
                    return;
                }
                route.resume();
                return;
            }
            if (url.contains("/web/aweme/detail/")) {
                route.resume();
                return;
            }
            route.abort();
        });
        Page page = playwrightBrowser.newPage();
        for (int i = 0; i <= BrowserConfig.LOAD_PAGE_RETRY; i++) {
            try {
                page.navigate(DouYinWorkScheduler.randomPage(), new Page.NavigateOptions().setWaitUntil(WaitUntilState.LOAD).setTimeout(BrowserConfig.LOAD_PAGE_TIMEOUT));
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
    }

    private static void putCache(Internet.UserAgent browserName, String name, byte[] bytes) {
        if (Internet.UserAgent.CHROME == browserName) {
            CHROME_CACHE.put(name, bytes);
        }
        if (Internet.UserAgent.FIREFOX == browserName) {
            FIREFOX_CACHE.put(name, bytes);
        }
        if (Internet.UserAgent.SAFARI == browserName) {
            WEBKIT_CACHE.put(name, bytes);
        }
    }

    private static void putCache(String directoryName, String name, byte[] bytes) {
        if (directoryName.equals(Internet.UserAgent.CHROME.toString())) {
            CHROME_CACHE.put(name, bytes);
        }
        if (directoryName.equals(Internet.UserAgent.FIREFOX.toString())) {
            FIREFOX_CACHE.put(name, bytes);
        }
        if (directoryName.equals(Internet.UserAgent.SAFARI.toString())) {
            WEBKIT_CACHE.put(name, bytes);
        }
    }

    private static byte[] getCache(Internet.UserAgent browserName, String name) {
        String directoryName = browserName.toString();
        if (directoryName.equals(Internet.UserAgent.CHROME.toString())) {
            return CHROME_CACHE.get(name);
        }
        if (directoryName.equals(Internet.UserAgent.FIREFOX.toString())) {
            return FIREFOX_CACHE.get(name);
        }
        if (directoryName.equals(Internet.UserAgent.SAFARI.toString())) {
            return WEBKIT_CACHE.get(name);
        }
        throw new IllegalArgumentException("not support browser");
    }


    public static void clearCache() {
        CHROME_CACHE.clear();
        FIREFOX_CACHE.clear();
        WEBKIT_CACHE.clear();
    }


    public static void reloadCache() {
        com.microsoft.playwright.options.Proxy browserProxy = PlaywrightBrowser.buildProxy();
        for (Internet.UserAgent browser : BrowserConfig.BROWSERS) {
            for (int i = 0; i < 10; i++) {
                try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(browserProxy, browser)) {
                    navigateToDouYinUserPage(playwrightBrowser);
                    break;
                } catch (Exception e) {
                    log.error("reload cache error: " + e.getMessage());
                }
            }
        }
    }

    public static String randomPage() {
        return RandomUtil.randomEle(DOUYIN_PAGES);
    }


}

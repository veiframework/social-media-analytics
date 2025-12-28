package com.chargehub.admin.scheduler;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.domain.SocialMediaAccountTask;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.service.SocialMediaAccountTaskService;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.DataSyncWorksParams;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.MediaTypeEnum;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.SyncWorkStatusEnum;
import com.chargehub.admin.enums.WorkStateEnum;
import com.chargehub.admin.playwright.BrowserConfig;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.playwright.PlaywrightCrawlHelper;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.service.SocialMediaWorkCreateService;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.common.core.constant.Constants;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.redis.service.RedisService;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Proxy;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chargehub.admin.datasync.tikhub.DataSyncDouYinServiceImpl.DOUYIN_FETCH_WORK_JS;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Order
@Slf4j
@Component
public class DouYinWorkScheduler extends AbstractWorkScheduler {


    public static final String DOUYIN_USER_PAGE = "https://www.douyin.com/user/self";

    protected DouYinWorkScheduler(SocialMediaAccountTaskService socialMediaAccountTaskService, RedisService redisService, DataSyncManager dataSyncManager, SocialMediaAccountService socialMediaAccountService, SocialMediaWorkService socialMediaWorkService, SocialMediaWorkCreateService socialMediaWorkCreateService, HubProperties hubProperties) {
        super(socialMediaAccountTaskService, redisService, dataSyncManager, socialMediaAccountService, socialMediaWorkService, socialMediaWorkCreateService, hubProperties, null);
        this.setTaskName(SocialMediaPlatformEnum.DOU_YIN.getDomain());

    }

    @Override
    public void execute(Integer limit) {
        boolean hasTask = socialMediaWorkCreateService.hasTask();
        if (hasTask) {
            log.error("正在创建作品, {}作品同步不执行", taskName);
            return;
        }
        List<SocialMediaAccountTask> socialMediaAccounts = socialMediaAccountTaskService.getAllByPlatformId(taskName, true, limit);
        if (CollectionUtils.isEmpty(socialMediaAccounts)) {
            return;
        }
        StopWatch stopWatch = new StopWatch(taskName);
        stopWatch.start();
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(StringPool.EMPTY)) {
            DateTime now = DateUtil.date();
            redisService.setHashEx(SYNCING_WORK_LOCK, taskName, now.toString(), 2, TimeUnit.HOURS);
            log.info("{}开始同步作品 {}", taskName, now);
            Page page = DouYinWorkScheduler.navigateToDouYinUserPage(playwrightBrowser);
            log.info("{}同步任务加载网页端环境", taskName);
            List<String> completeAccountIds = new CopyOnWriteArrayList<>();
            Proxy proxy = BrowserConfig.getProxy();
            for (SocialMediaAccountTask socialMediaAccount : socialMediaAccounts) {
                String accountId = socialMediaAccount.getAccountId();
                this.fetchWorks(now, accountId, completeAccountIds, proxy, page);
            }
            Date endDate = new Date();
            List<SocialMediaAccount> completeList = completeAccountIds.stream().map(i -> {
                SocialMediaAccount socialMediaAccount = new SocialMediaAccount();
                socialMediaAccount.setId(i);
                socialMediaAccount.setSyncWorkDate(endDate);
                socialMediaAccount.setSyncWorkStatus(SyncWorkStatusEnum.COMPLETE.ordinal());
                return socialMediaAccount;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(completeList)) {
                Db.updateBatchById(completeList);
            }
        } catch (Exception e) {
            log.error("{}同步作品异常 {}", taskName, e.getMessage());
        } finally {
            stopWatch.stop();
            redisService.deleteCacheMapValue(SYNCING_WORK_LOCK, taskName);
            log.info("{}同步作品结束 {}秒", taskName, stopWatch.getTotalTimeSeconds());
        }
    }

    public static Page navigateToDouYinUserPage(PlaywrightBrowser playwrightBrowser) {
        Page page = playwrightBrowser.newPage();
        for (int i = 0; i <= BrowserConfig.LOAD_PAGE_RETRY; i++) {
            try {
                page.onConsoleMessage(msg -> {
                    String text = msg.text();
                    if (text.contains("[douyin]")) {
                        log.error("抖音同步任务请求接口失败:" + text);
                    }
                });
                page.route("**/*", route -> {
                    Request request = route.request();
                    String resourceType = request.resourceType();
                    String url = request.url();
                    if (url.contains("/web/aweme/detail/")) {
                        route.resume();
                        return;
                    }
                    if (BrowserConfig.RESOURCE_TYPES.contains(resourceType) || BrowserConfig.RESOURCE_TYPES.stream().anyMatch(url::endsWith)) {
                        route.abort();
                        return;
                    }
                    route.resume();
                });
                page.navigate(DouYinWorkScheduler.DOUYIN_USER_PAGE, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED).setTimeout(BrowserConfig.LOAD_PAGE_TIMEOUT));
                page.waitForFunction("() => typeof window.byted_acrawler !== 'undefined' && typeof window.byted_acrawler.frontierSign === 'function'");
                break;
            } catch (Exception e) {
                log.error("重试进入页面轮次" + i);
                if (i == BrowserConfig.LOAD_PAGE_RETRY) {
                    page.close();
                    throw e;
                }
                page.close();
                page = playwrightBrowser.newPage();
            }
        }
        return page;
    }


}

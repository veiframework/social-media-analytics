package com.chargehub.admin.playwright;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import com.chargehub.common.security.utils.DictUtils;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Proxy;
import com.microsoft.playwright.options.ServiceWorkerPolicy;
import com.microsoft.playwright.options.ViewportSize;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zhanghaowei
 * @since 2025-11-29 21:45
 */
@Slf4j
@Data
public class PlaywrightBrowser implements AutoCloseable {


    private BrowserContext browserContext;

    private Page page;

    private String password;

    private String username;

    private Playwright playwright;

    private Proxy proxy;

    private static volatile boolean headless;

    private BrowserConfig browserConfig;

    public PlaywrightBrowser(String username, String password, String storageState) {
        this(storageState);
        this.username = username;
        this.password = password;
    }

    public PlaywrightBrowser(Proxy proxy) {
        this.proxy = proxy;
        this.playwright = Playwright.create();
        this.browserConfig = new BrowserConfig(this.playwright);
        this.browserContext = PlaywrightBrowser.buildBrowserContext(this.browserConfig, null, proxy);

    }

    public PlaywrightBrowser(String storageState) {
        String loginState = null;
        if (StringUtils.isNotBlank(storageState)) {
            loginState = storageState;
        }
        this.playwright = Playwright.create();
        this.browserConfig = new BrowserConfig(this.playwright);
        this.browserContext = PlaywrightBrowser.buildBrowserContext(this.browserConfig, loginState);
    }

    public PlaywrightBrowser(BrowserContext browserContext) {
        this.page = browserContext.newPage();

    }

    public static void setHeadless(boolean headless) {
        PlaywrightBrowser.headless = headless;
    }

    public static Proxy buildProxy() {
        try {
            Map<String, String> crawlerProxy = DictUtils.getDictLabelMap("crawler_proxy_chrome");
            if (MapUtil.isEmpty(crawlerProxy)) {
                return null;
            }
            return new Proxy(crawlerProxy.get("proxy_url"))
                    .setUsername(crawlerProxy.get("proxy_username"))
                    .setPassword(crawlerProxy.get("proxy_password"));
        } catch (Exception e) {
            return null;
        }
    }

    public static BrowserContext buildBrowserContext(BrowserConfig browserConfig, String storageState) {
        Proxy proxy = buildProxy();
        return buildBrowserContext(browserConfig, storageState, proxy);
    }

    @SuppressWarnings("all")
    public static BrowserContext buildBrowserContext(BrowserConfig browserConfig, String storageState, Proxy proxy) {
        // 随机选择一个浏览器
        BrowserType browserType = browserConfig.getBrowserType();
        log.debug("当前浏览器: {}", browserType.name());
        Integer width = browserConfig.getWidth();
        Integer height = browserConfig.getHeight();
        Map<String, String> extraHeaders = browserConfig.getExtraHeaders();
        List<String> args = browserConfig.getArgs();
        String randomUa = browserConfig.getRandomUa();
        String fingerprint = browserConfig.getFingerprint();
        Path executablePath = browserConfig.getExecutablePath();
        Browser.NewContextOptions newContextOptions = new Browser.NewContextOptions()
                .setIgnoreHTTPSErrors(true)
                .setLocale("zh-CN")
                .setTimezoneId("Asia/Shanghai")
                .setDeviceScaleFactor(1)
                .setServiceWorkers(ServiceWorkerPolicy.BLOCK)
                .setStorageState(storageState)
                .setViewportSize(width, height)
                .setBypassCSP(true)
                .setExtraHTTPHeaders(extraHeaders)
                .setUserAgent(randomUa);
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(headless).setProxy(proxy).setArgs(args).setExecutablePath(executablePath);
        BrowserContext browserContext = browserType.launch(launchOptions).newContext(newContextOptions);
        if (StringUtils.isNotBlank(fingerprint)) {
            browserContext.addInitScript(fingerprint);
        }
        return browserContext;
    }

    public BrowserContext newContext() {
        return PlaywrightBrowser.buildBrowserContext(this.browserConfig, null, this.proxy);
    }

    public static JsonNode request(String id, Page page, String script, String envUrl) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        List<String> collect = Stream.of(id).collect(Collectors.toList());
        List<JsonNode> jsonNodes = requests(collect, page, script, envUrl);
        if (CollectionUtils.isEmpty(jsonNodes)) {
            return null;
        }
        return jsonNodes.get(0);
    }

    public static List<JsonNode> requests(Collection<String> ids, Page page, String script, String envUrl) {
        List<JsonNode> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(ids)) {
            return result;
        }
        try {
            for (String id : ids) {
                boolean success = false;
                String json = null;
                for (int attempt = 0; attempt <= BrowserConfig.LOAD_PAGE_RETRY; attempt++) {
                    json = doRequest(script, page, id);
                    if (StringUtils.isNotBlank(json) && !"reload".equals(json)) {
                        success = true;
                        break;
                    }
                    page = cleanupAndReload(page, envUrl);
                }
                if (success) {
                    JsonNode jsonNode = JacksonUtil.toObj(json);
                    result.add(jsonNode);
                } else {
                    log.error("获取作品ID为{}的详情失败", id);
                }
            }
            return result;
        } catch (Exception e) {
            log.error(String.join(",", ids) + "批量获取抖音作品详情失败: ", e);
            return result;
        }
    }

    private static String doRequest(String script, Page page, String workId) {
        try {
            if (page.isClosed()) {
                return null;
            }
            return (String) page.evaluate(script, workId);
        } catch (Exception e) {
            log.error(workId + "获取抖音作品详情失败: ", e.getMessage());
            return null;
        }
    }


    public static Page cleanupAndReload(Page page, String envUrl) {
        if (page == null || page.isClosed()) {
            throw new IllegalArgumentException("Page is null or already closed");
        }
        Page newPage = null;
        for (int attempt = 0; attempt <= BrowserConfig.LOAD_PAGE_RETRY; attempt++) {
            try {
                BrowserContext context = page.context();
                context.clearCookies();
                context.clearPermissions();
                newPage = context.newPage();
                newPage.navigate(envUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED).setTimeout(BrowserConfig.LOAD_PAGE_TIMEOUT));
                newPage.waitForTimeout(RandomUtil.randomInt(1000, 5000));
                if (!page.isClosed()) {
                    page.close();
                }
                return newPage;
            } catch (Exception e) {
                if (attempt == BrowserConfig.LOAD_PAGE_RETRY) {
                    if (newPage != null && !newPage.isClosed()) {
                        newPage.close();
                    }
                    return page;
                }
            }
        }
        return page;
    }

    public Page newPage() {
        this.page = this.browserContext.newPage();
        return page;
    }

    @Override
    public void close() {
        if (this.page != null && !page.isClosed()) {
            this.page.close();
        }
        if (this.browserContext != null) {
            try {
                this.browserContext.close();
            } catch (Exception e) {
                log.warn("browserContext close failed");
            }
        }
        if (this.playwright != null) {
            try {
                this.playwright.close();
            } catch (Exception e) {
                log.warn("playwright close failed");
            }
        }
    }

    public void randomMove() {
        ViewportSize viewport = page.viewportSize();
        // 留出边界避免超出视口
        double maxX = viewport.width - 100d;
        // 留出边界避免超出视口
        double maxY = viewport.height - 100d;
        // 生成50到(maxX-50)之间的随机浮点数
        double x = RandomUtil.randomDouble(50, maxX - 50);
        // 生成50到(maxY-50)之间的随机浮点数
        double y = RandomUtil.randomDouble(50, maxY - 50);
        page.mouse().move(x, y);
    }
}

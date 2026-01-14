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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.providers.base.Internet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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

    private BrowserConfig browserConfig;


    public PlaywrightBrowser(Proxy proxy) {
        this(proxy, true);
    }

    public PlaywrightBrowser(Proxy proxy, boolean headless) {
        this(proxy, null, headless);
    }

    public PlaywrightBrowser(Proxy proxy, Internet.UserAgent browserType, boolean headless) {
        this.proxy = proxy;
        this.playwright = Playwright.create();
        this.browserConfig = new BrowserConfig(this.playwright, browserType, headless);
        this.browserContext = buildBrowserContext(this.browserConfig, null, proxy);
    }


    /**
     * 获取代理
     * 此处好像safari只支持ip白名单
     *
     * @return
     */
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
//            return new Proxy("tun-igkddr.qg.net:28614").setUsername("37D9A171").setPassword("D9AA789BC9A0");
            return null;
        }
    }


    @SuppressWarnings("all")
    public BrowserContext buildBrowserContext(BrowserConfig browserConfig, String storageState, Proxy proxy) {
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
        boolean headless = browserConfig.isHeadless();
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
        if (this.page != null) {
            if (!this.page.isClosed()) {
                this.page.close();
            }
            this.page = null;
        }
        if (this.browserContext != null) {
            try {
                this.browserContext.close();
            } catch (Exception e) {
                log.error("browserContext close failed");
            }
            this.browserContext = null;
        }
        boolean headless = this.browserConfig.isHeadless();
        this.browserConfig = null;
        this.browserConfig = new BrowserConfig(this.playwright, headless);
        this.browserContext = buildBrowserContext(this.browserConfig, null, proxy);
        return this.browserContext;
    }

    public static JsonNode request(String id, PlaywrightBrowser playwrightBrowser, String script, Consumer<PlaywrightBrowser> consumer) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        List<String> collect = Stream.of(id).collect(Collectors.toList());
        List<JsonNode> jsonNodes = requests(collect, playwrightBrowser, script, consumer);
        if (CollectionUtils.isEmpty(jsonNodes)) {
            return null;
        }
        return jsonNodes.get(0);
    }

    public static List<JsonNode> requests(Collection<String> ids, PlaywrightBrowser playwrightBrowser, String script, Consumer<PlaywrightBrowser> consumer) {
        List<JsonNode> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(ids)) {
            return result;
        }
        try {
            for (String id : ids) {
                boolean success = false;
                String json = null;
                for (int attempt = 0; attempt <= BrowserConfig.LOAD_PAGE_RETRY; attempt++) {
                    json = doRequest(script, playwrightBrowser, id);
                    if (StringUtils.isNotBlank(json) && !"reload".equals(json)) {
                        success = true;
                        break;
                    }
                    //重置浏览器
                    playwrightBrowser.newContext();
                    consumer.accept(playwrightBrowser);
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

    private static String doRequest(String script, PlaywrightBrowser playwrightBrowser, String workId) {
        try {
            Page page = playwrightBrowser.getPage();
            if (page.isClosed()) {
                return null;
            }
            return (String) page.evaluate(script, workId);
        } catch (Exception e) {
            log.error(workId + "获取抖音作品详情失败: ", e.getMessage());
            return null;
        }
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

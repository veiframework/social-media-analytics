package com.chargehub.admin.playwright;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import com.chargehub.common.security.utils.DictUtils;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Proxy;
import com.microsoft.playwright.options.ServiceWorkerPolicy;
import com.microsoft.playwright.options.ViewportSize;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.providers.base.Internet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zhanghaowei
 * @since 2025-11-29 21:45
 */
@Slf4j
@Data
public class PlaywrightBrowser implements AutoCloseable {


    private static final List<BrowserConfig> BROWSER_CONFIGS = Lists.newArrayList(
//            new BrowserConfig(Internet.UserAgent.FIREFOX)
            new BrowserConfig(Internet.UserAgent.CHROME)
//            ,new BrowserConfig(Internet.UserAgent.SAFARI)
    );

    private BrowserContext browserContext;

    private Page page;

    private String password;

    private String username;

    private Playwright playwright;

    private static volatile boolean headless;

    public PlaywrightBrowser(String username, String password, String storageState) {
        this(storageState);
        this.username = username;
        this.password = password;
    }

    public PlaywrightBrowser(String storageState) {
        String loginState = null;
        if (StringUtils.isNotBlank(storageState)) {
            loginState = storageState;
        }
        this.playwright = Playwright.create();
        this.browserContext = PlaywrightBrowser.buildBrowserContext(loginState, this.playwright);
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

    public static BrowserContext buildBrowserContext(String storageState, Playwright playwright) {
        Proxy proxy = buildProxy();
        return buildBrowserContext(storageState, playwright, proxy);
    }

    @SuppressWarnings("all")
    public static BrowserContext buildBrowserContext(String storageState, Playwright playwright, Proxy proxy) {
        // 随机选择一个浏览器
        BrowserConfig browserConfig = new BrowserConfig(Internet.UserAgent.CHROME);
        browserConfig.setPlaywright(playwright);
        BrowserType browserType = browserConfig.getBrowserType();
        String randomUa = browserConfig.getRandomUa();
        Browser.NewContextOptions newContextOptions = new Browser.NewContextOptions()
                .setIgnoreHTTPSErrors(true)
                .setLocale("zh-CN")
                .setTimezoneId("Asia/Shanghai")
                .setDeviceScaleFactor(1)
                .setServiceWorkers(ServiceWorkerPolicy.BLOCK)
                .setStorageState(storageState)
                .setViewportSize(1920, 1080)
                .setBypassCSP(true)
                .setUserAgent(randomUa);
        // 启动选项（可统一配置）
        BrowserContext browserContext = browserType.launch(new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setProxy(proxy)
                //设置启动系统浏览器
//                .setExecutablePath(Paths.get("C://Program Files (x86)//Microsoft//Edge//Application//msedge.exe"))
                .setArgs(Arrays.asList(
                        "--no-sandbox"
                        , "--disable-web-security"
//                       TODO 屏蔽webgl警告,未来需要截图的话需要开启
                        , "--disable-gpu"
                        , "--enable-unsafe-swiftshader"
                        , "--use-gl=swiftshader"
                        , "--disable-setuid-sandbox"
                        , "--disable-dev-shm-usage"
                        , "--disable-blink-features=AutomationControlled"
                        , "--MOZ_DISABLE_FIREFOX_SAFEBROWSING=1"
                        , "--MOZ_DISABLE_TELEMETRY=1"
                        , "--disable-component-update",
                        "--allow-running-insecure-content",
                        "--unsafely-treat-insecure-origin-as-secure",
                        "--ignore-certificate-errors"
                ))).newContext(newContextOptions);
        String fingerprintJs = loadBrowserFingerprintJs(randomUa);
        browserContext.addInitScript(fingerprintJs);
        return browserContext;
    }

    public static String loadBrowserFingerprintJs(String randomUa) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(MessageFormat.format("classpath:{0}", "BrowserFingerprint.js"));
        try {
            return IoUtil.readUtf8(resource.getInputStream()).replace("{REAL_USER_AGENT}", randomUa);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    @SuppressWarnings("unchecked")
    public static List<JsonNode> requests(Collection<String> ids, Page page, String script) {
        List<JsonNode> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(ids)) {
            return result;
        }
        try {
            boolean hasMore = ids.size() > 1;
            for (String id : ids) {
                if (hasMore) {
                    HumanMouseSimulator.randomMove(page);
                }
                List<String> evaluate = (List<String>) page.evaluate(script, Lists.newArrayList(id));
                if (CollectionUtils.isEmpty(evaluate)) {
                    continue;
                }
                for (String json : evaluate) {
                    if (StringUtils.isBlank(json)) {
                        continue;
                    }
                    JsonNode jsonNode = JacksonUtil.toObj(json);
                    result.add(jsonNode);
                }
            }
            return result;
        } catch (Exception e) {
            log.error(String.join(",", ids) + "批量获取抖音作品详情失败: ", e);
            return result;
        }
    }

    public static JsonNode request(String id, Page page, String script) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        List<String> collect = Stream.of(id).collect(Collectors.toList());
        List<JsonNode> jsonNodes = requests(collect, page, script);
        if (CollectionUtils.isEmpty(jsonNodes)) {
            return null;
        }
        return jsonNodes.get(0);
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
            this.browserContext.close();
        }
        if (this.playwright != null) {
            this.playwright.close();
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

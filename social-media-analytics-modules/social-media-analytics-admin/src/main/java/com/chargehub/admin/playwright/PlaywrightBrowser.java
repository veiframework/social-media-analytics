package com.chargehub.admin.playwright;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import com.chargehub.common.security.utils.DictUtils;
import com.google.common.collect.Lists;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Proxy;
import com.microsoft.playwright.options.ServiceWorkerPolicy;
import com.microsoft.playwright.options.ViewportSize;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.providers.base.Internet;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @since 2025-11-29 21:45
 */
@Slf4j
@Data
public class PlaywrightBrowser implements AutoCloseable {


    private static final String SCRIPT = "Object.defineProperty(navigator, 'webdriver', { get: () => undefined });\n" +
            "    window.chrome = { runtime: {} };\n" +
            "    Object.defineProperty(navigator, 'plugins', { \n" +
            "        get: () => [\n" +
            "            { 0: { type: \"application/x-google-chrome-pdf\", suffixes: \"pdf\", description: \"Chrome PDF Plugin\" } },\n" +
            "            { 0: { type: \"application/pdf\", suffixes: \"pdf\", description: \"Chrome PDF Viewer\" } }\n" +
            "        ] \n" +
            "    });\n" +
            "    Object.defineProperty(navigator, 'languages', { get: () => ['zh-CN', 'zh', 'en']});\n" +
            "    Object.defineProperty(navigator, 'hardwareConcurrency', { get: () => 8 });\n" +
            "    Object.defineProperty(navigator, 'deviceMemory', { get: () => 8 });\n" +
            "    const getParameter = WebGLRenderingContext.prototype.getParameter;\n" +
            "    WebGLRenderingContext.prototype.getParameter = function(param) { \n" +
            "        if (param === 37445) return \"Google Inc.\";\n" +
            "        if (param === 37446) return \"Google Inc.\";\n" +
            "        return getParameter.call(this, param);\n" +
            "    };";

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
        Browser.NewContextOptions newContextOptions = new Browser.NewContextOptions()
                .setIgnoreHTTPSErrors(true)
                .setLocale("zh-CN")
                .setTimezoneId("Asia/Shanghai")
                .setDeviceScaleFactor(1)
                .setServiceWorkers(ServiceWorkerPolicy.BLOCK)
                .setStorageState(storageState)
                .setExtraHTTPHeaders(MapUtil.builder(new HashMap<String, String>())
                        .put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                        .build())
                .setViewportSize(1920, 1080)
                .setUserAgent(browserConfig.getRandomUa());
        // 启动选项（可统一配置）
        BrowserContext browserContext = browserType.launch(new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setProxy(proxy)
                //设置启动系统浏览器
//                .setExecutablePath(Paths.get("C://Program Files (x86)//Microsoft//Edge//Application//msedge.exe"))
                .setArgs(Arrays.asList(
                        "--no-sandbox"
//                        , "-private"
                        , "--disable-setuid-sandbox"
                        , "--disable-dev-shm-usage"
                        , "--disable-blink-features=AutomationControlled"
//                        , "--disable-gpu"
                        , "--MOZ_DISABLE_FIREFOX_SAFEBROWSING=1"
                        , "--MOZ_DISABLE_TELEMETRY=1"
                        , "--disable-component-update",
//                        //防止浏览器基于用户与媒体的交互历史来决定是否允许自动播放
//                        , "--disable-features=MediaEngagementBypassAutoplayPolicies"
//                        , "--autoplay-policy=user-gesture-required"
//                        //禁用后台媒体暂停功能
//                        , "--disable-background-media-suspend"
//                       //确保不阻止 WebSocket
                        "--allow-running-insecure-content",
                        "--unsafely-treat-insecure-origin-as-secure",
                        "--ignore-certificate-errors"
                ))).newContext(newContextOptions);
        browserContext.addInitScript(SCRIPT);
        return browserContext;
    }

    public Page newPage() {
        this.page = this.browserContext.newPage();
        return page;
    }

    @Override
    public void close() {
        if (this.page != null) {
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
//        ThreadUtil.safeSleep(RandomUtil.randomInt(300, 1000));
    }
}

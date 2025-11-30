package com.chargehub.admin.playwright;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Lists;
import com.microsoft.playwright.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.providers.base.Internet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Zhanghaowei
 * @since 2025-11-29 21:45
 */
@Slf4j
@Data
public class PlaywrightBrowser implements AutoCloseable {

    private static final String SCRIPT = "Object.defineProperty(navigator, 'webdriver', { get: () => undefined });\n" +
            " window.chrome = { runtime: {} };\n" +
            "Object.defineProperty(navigator, 'plugins', { get: () => [1, 2, 3, 4, 5] });\n" +
            "Object.defineProperty(navigator, 'languages', { get: () => ['zh-CN', 'zh']});\n" +
            "Object.defineProperty(navigator, 'hardwareConcurrency', { get: () => 8 });\n" +
            "Object.defineProperty(navigator, 'deviceMemory', { get: () => 8 });\n" +
            "const getParameter = WebGLRenderingContext.prototype.getParameter;WebGLRenderingContext.prototype.getParameter = function(param) { if (param === 37445) return \"Google Inc.\"; if (param === 37446) return \"ANGLE (Intel, Intel(R) UHD Graphics Direct3D11 vs_5_0 ps_5_0, D3D11)\";return getParameter.call(this, param);};";


    private static final List<BrowserConfig> BROWSER_CONFIGS = Lists.newArrayList(
//            new BrowserConfig(Internet.UserAgent.FIREFOX)
            new BrowserConfig(Internet.UserAgent.CHROME)
//            ,new BrowserConfig(Internet.UserAgent.SAFARI)
    );

    private Playwright playwright;

    private List<Page> pages;


    private String password;

    private String username;

    private String storageState;

    private BrowserContext browserContext;

    public PlaywrightBrowser(String username, String password, String storageState) {
        this.username = username;
        this.password = password;
        this.storageState = storageState;
    }

    public PlaywrightBrowser(String storageState) {
        this.storageState = storageState;
    }

    public void init() {
        // 随机选择一个浏览器
        BrowserConfig browserConfig = RandomUtil.randomEle(BROWSER_CONFIGS);
        Playwright playwright = Playwright.create();
        browserConfig.setPlaywright(playwright);
        BrowserType browserType = browserConfig.getBrowserType();
        Browser.NewContextOptions newContextOptions = new Browser.NewContextOptions()
                .setLocale("zh-CN")
                .setTimezoneId("Asia/Shanghai")
                .setDeviceScaleFactor(1)
                .setStorageState(storageState)
                .setExtraHTTPHeaders(MapUtil.of("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8"))
                .setViewportSize(1980, 1080)
                .setUserAgent(browserConfig.getRandomUA());
        // 启动选项（可统一配置）
        BrowserContext browserContext = browserType.launch(new BrowserType.LaunchOptions()
//                .setProxy("socks5://106.15.129.14:8081")
                .setHeadless(false)
                .setArgs(Arrays.asList(
                        "--no-sandbox"
                        , "-private"
                        , "--disable-setuid-sandbox"
                        , "--disable-dev-shm-usage"
                        , "--disable-blink-features=AutomationControlled"
                        , "--disable-gpu"
                        , "--MOZ_DISABLE_FIREFOX_SAFEBROWSING=1"
                        , "--MOZ_DISABLE_TELEMETRY=1"
                        , "--disable-component-update"
                        , "--disable-features=MediaEngagementBypassAutoplayPolicies"
                        , "--autoplay-policy=user-gesture-required"
                        , "--disable-background-media-suspend"

                ))).newContext(newContextOptions);
        browserContext.addInitScript(SCRIPT);
        this.browserContext = browserContext;
        this.playwright = playwright;
        this.pages = new ArrayList<>();
    }

    public Page newPage() {
        Page page = this.browserContext.newPage();
        this.pages.add(page);
        return page;
    }

    @Override
    public void close() {
        this.pages.forEach(Page::close);
        this.browserContext.close();
        this.playwright.close();
    }


}

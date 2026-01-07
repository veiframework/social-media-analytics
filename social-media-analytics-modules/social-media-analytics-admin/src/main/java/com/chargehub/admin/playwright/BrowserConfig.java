package com.chargehub.admin.playwright;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import com.chargehub.common.security.utils.DictUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ViewportSize;
import lombok.Data;
import net.datafaker.providers.base.Internet;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpHeaders;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zhanghaowei
 * @since 2025-11-29 21:47
 */
@Data
public class BrowserConfig {

    public static final List<Internet.UserAgent> BROWSERS = Lists.newArrayList(
            Internet.UserAgent.CHROME
            , Internet.UserAgent.FIREFOX
            , Internet.UserAgent.SAFARI
    );

    protected static final int[][] RESOLUTIONS = {
            {1920, 1080},
            {1366, 768},
            {1440, 900},
            {1536, 864},
            {1600, 900},
            {1680, 1050},
            {1280, 720},
            {1280, 1024},
            {1600, 1200},
            {1920, 1200},
            {2560, 1440}
    };

    private static final List<String> WEBKIT_UA = Lists.newArrayList(
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Safari/605.1.15",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.1 Safari/605.1.15",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4 Safari/605.1.15",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.6 Safari/605.1.15",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.0 Safari/605.1.15",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.1 Safari/605.1.15"
    );

    private static final List<String> CHROME_UA = Lists.newArrayList(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36"
    );

    private static final List<String> FIREFOX_UA = Lists.newArrayList(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:135.0) Gecko/20100101 Firefox/135.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:137.0) Gecko/20100101 Firefox/137.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:140.0) Gecko/20100101 Firefox/140.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:145.0) Gecko/20100101 Firefox/145.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:143.0) Gecko/20100101 Firefox/143.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:144.0) Gecko/20100101 Firefox/144.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:146.0) Gecko/20100101 Firefox/146.0"
    );

    public static final Map<String, String> BROWSER_HEADERS = MapUtil.builder(new LinkedHashMap<String, String>())
            .put(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
            .put(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br, zstd")
            .put(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
            .put(HttpHeaders.CACHE_CONTROL, "max-age=0")
            .put(HttpHeaders.CONNECTION, "close")
            .put("Sec-Ch-Ua", "\"Microsoft Edge\";v=\"143\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\"")
            .put("Sec-Ch-Ua-Mobile", "?0")
            .put("Sec-Ch-Ua-Platform", "\"Windows\"")
            .put("Sec-Fetch-Dest", "document")
            .put("Sec-Fetch-Mode", "navigate")
            .put("Sec-Fetch-Site", "same-origin")
            .put("Sec-Fetch-User", "?1")
            .put("Upgrade-Insecure-Requests", "1")
            .put(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0")
            .build();


    public static final Set<String> RESOURCE_TYPES = Sets.newHashSet("fetch", "websocket", ".svg", ".mp3", ".mp4", "wasm", "vnd.microsoft.icon", "xhr", "png", "media", "avif", "webp", "gif", "svg+xml", "font", "stylesheet");

    public static final Integer LOAD_PAGE_RETRY = 10;

    public static final Integer LOAD_PAGE_TIMEOUT = 60000;

    private Integer width;

    private Integer height;

    private BrowserType browserType;

    private Internet.UserAgent userAgent;

    private String randomUa;

    private List<String> args;

    private Map<String, String> extraHeaders;

    private String fingerprint;

    /**
     * Paths.get("C://Program Files (x86)//Microsoft//Edge//Application//msedge.exe")
     */
    private Path executablePath;

    public BrowserConfig(Playwright playwright) {
        this(playwright, null);
    }

    public BrowserConfig(Playwright playwright, Internet.UserAgent browserType) {
        ViewportSize randomViewport = this.getRandomViewport();
        this.width = randomViewport.width;
        this.height = randomViewport.height;
        this.userAgent = browserType == null ? RandomUtil.randomEle(BROWSERS) : browserType;
        this.setPlaywright(playwright);
    }

    public void setPlaywright(Playwright playwright) {
        if (this.userAgent == Internet.UserAgent.CHROME) {
            this.randomUa = RandomUtil.randomEle(CHROME_UA);
            this.args = Lists.newArrayList(
                    "--no-sandbox"
                    , "--disable-web-security"
//                       TODO 屏蔽webgl警告,未来需要截图的话需要开启
                    , "--disable-gpu"
                    , "--disable-setuid-sandbox"
                    , "--disable-dev-shm-usage"
                    , "--disable-blink-features=AutomationControlled"
                    , "--MOZ_DISABLE_FIREFOX_SAFEBROWSING=1"
                    , "--MOZ_DISABLE_TELEMETRY=1"
                    , "--disable-component-update",
                    "--allow-running-insecure-content",
                    "--unsafely-treat-insecure-origin-as-secure",
                    "--ignore-certificate-errors");
            String version = BrowserConfig.extractChromeMajorVersion(randomUa);
            this.extraHeaders = MapUtil.of("Sec-Ch-Ua", "\"Google Chrome\";v=\"" + version + "\", \"Chromium\";v=\"" + version + "\", \"Not A(Brand\";v=\"24\"");
            this.browserType = playwright.chromium();
            this.fingerprint = this.loadBrowserFingerprintJs("BrowserFingerprint.js");
        } else if (userAgent == Internet.UserAgent.SAFARI) {
            this.browserType = playwright.webkit();
            this.randomUa = RandomUtil.randomEle(WEBKIT_UA);
            this.fingerprint = this.loadBrowserFingerprintJs("WebkitFingerprint.js");
        } else {
            this.browserType = playwright.firefox();
            this.randomUa = RandomUtil.randomEle(FIREFOX_UA);
            this.fingerprint = this.loadBrowserFingerprintJs("FirefoxFingerprint.js");
        }

    }


    public static Proxy getProxy() {
        try {
            Map<String, String> crawlerProxy = DictUtils.getDictLabelMap("crawler_proxy");
            if (MapUtil.isEmpty(crawlerProxy)) {
                return null;
            }
            String proxyUrl = crawlerProxy.get("proxy_url");
            String[] split = proxyUrl.split(":");
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(split[0], Integer.parseInt(split[1])));
        } catch (Exception e) {
            return null;
        }
    }

    public static String extractChromeMajorVersion(String userAgent) {
        Pattern pattern = Pattern.compile("Chrome/(\\d+)");
        Matcher matcher = pattern.matcher(userAgent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }


    public static Integer clearWord(String text) {
        if (text.contains("万")) {
            String replace = text.replace("万", "").replace("+", "");
            return new BigDecimal(replace).multiply(BigDecimal.valueOf(10000)).intValue();
        }
        if (text.contains("千")) {
            String replace = text.replace("千", "").replace("+", "");
            return new BigDecimal(replace).multiply(BigDecimal.valueOf(1000)).intValue();
        }
        return Integer.parseInt(text.replace("+", ""));
    }

    public ViewportSize getRandomViewport() {
        int index = RandomUtil.randomInt(RESOLUTIONS.length);
        int[] resolution = RESOLUTIONS[index];
        return new ViewportSize(resolution[0], resolution[1]);
    }

    public String loadBrowserFingerprintJs(String fileName) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(MessageFormat.format("classpath:{0}", fileName));
        try {
            return IoUtil.readUtf8(resource.getInputStream());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(PlaywrightBrowser.buildProxy());
        for (int i = 0; i < 4; i++) {
            playwrightBrowser.getBrowserContext().addInitScript("localStorage.clear(); sessionStorage.clear();");
            Page page = playwrightBrowser.newPage();
            Response navigate = page.navigate("https://ifconfig.me/all.json");
            System.out.println(navigate.text());
            page.close();
        }
    }
}

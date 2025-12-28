package com.chargehub.admin.playwright;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import com.chargehub.common.security.utils.DictUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import lombok.Data;
import net.datafaker.providers.base.Internet;
import org.springframework.http.HttpHeaders;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

/**
 * @author Zhanghaowei
 * @since 2025-11-29 21:47
 */
@Data
public class BrowserConfig {


    private static final List<String> UA = Lists.newArrayList(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36"
    );

    public static final Map<String, String> BROWSER_HEADERS = MapUtil.builder(new LinkedHashMap<String, String>())
            .put(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
            .put(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br, zstd")
            .put(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
            .put(HttpHeaders.CACHE_CONTROL, "max-age=0")
            .put(HttpHeaders.CONNECTION, "keep-alive")
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


    public static final Set<String> RESOURCE_TYPES = Sets.newHashSet("fetch", "websocket", ".mp3", ".mp4", "wasm", "vnd.microsoft.icon", "xhr", "png", "media", "avif", "webp", "gif", "svg+xml", "font", "stylesheet");

    public static final Integer LOAD_PAGE_RETRY = 4;

    public static final Integer LOAD_PAGE_TIMEOUT = 60000;


    private BrowserType browserType;

    private Internet.UserAgent userAgent;

    private String randomUa;

    public BrowserConfig(Internet.UserAgent userAgent) {
        this.userAgent = userAgent;

    }

    public void setPlaywright(Playwright playwright) {
        if (this.userAgent == Internet.UserAgent.CHROME) {
            this.browserType = playwright.chromium();
        } else if (userAgent == Internet.UserAgent.SAFARI) {
            this.browserType = playwright.webkit();
        } else {
            this.browserType = playwright.firefox();
        }
        this.randomUa = RandomUtil.randomEle(UA);
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

}

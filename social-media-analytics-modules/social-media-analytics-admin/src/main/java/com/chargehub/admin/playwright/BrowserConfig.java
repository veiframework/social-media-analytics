package com.chargehub.admin.playwright;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Lists;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import lombok.Data;
import net.datafaker.providers.base.Internet;

import java.util.List;

/**
 * @author Zhanghaowei
 * @since 2025-11-29 21:47
 */
@Data
public class BrowserConfig {


    private static final List<String> UA = Lists.newArrayList(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36"
    );

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


}

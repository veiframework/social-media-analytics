package com.chargehub.admin.playwright;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import lombok.Data;
import net.datafaker.Faker;
import net.datafaker.providers.base.Internet;

import java.util.Locale;

/**
 * @author Zhanghaowei
 * @since 2025-11-29 21:47
 */
@Data
public class BrowserConfig {

    private static final Faker FAKER = new Faker(Locale.SIMPLIFIED_CHINESE);

    private BrowserType browserType;

    private Internet.UserAgent userAgent;

    private String randomUA;

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
        this.randomUA = FAKER.internet().userAgent(this.getUserAgent());
    }


}

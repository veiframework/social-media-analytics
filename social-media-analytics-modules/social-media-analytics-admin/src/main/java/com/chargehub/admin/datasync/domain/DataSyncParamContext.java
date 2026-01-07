package com.chargehub.admin.datasync.domain;

import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.microsoft.playwright.BrowserContext;
import lombok.Data;

import java.net.Proxy;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class DataSyncParamContext {

    private String accountId;

    private String storageState;

    private String shareLink;

    private boolean isScheduler;

    private BrowserContext browserContext;

    private String redirectUrl;

    private String mediaType;

    private Proxy proxy;

    private String workUid;

    private PlaywrightBrowser playwrightBrowser;
}

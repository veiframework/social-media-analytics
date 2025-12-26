package com.chargehub.admin.datasync.domain;

import com.chargehub.admin.work.domain.SocialMediaWork;
import com.microsoft.playwright.BrowserContext;
import lombok.Data;

import java.net.Proxy;
import java.util.Map;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class DataSyncWorksParams {

    private String secUid;

    private Map<String, String> workUids;

    private BrowserContext browserContext;

    private BrowserContext stateBrowserContext;

    private Proxy proxy;

    private Map<String, SocialMediaWork> workMap;
}

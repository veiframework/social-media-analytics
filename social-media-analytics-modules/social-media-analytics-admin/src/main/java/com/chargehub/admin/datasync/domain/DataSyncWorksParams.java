package com.chargehub.admin.datasync.domain;

import com.microsoft.playwright.BrowserContext;
import lombok.Data;

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

}

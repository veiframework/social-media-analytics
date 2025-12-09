package com.chargehub.admin.playwright;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @since 2025-11-29 23:37
 */
@Slf4j
public class PlaywrightCrawlTestHelper implements PlaywrightCrawlHelper {

    @Override
    public void saveSmsCode(String accountId, String smsCode) {

    }

    @Override
    public String checkSmsCode(String accountId) {
        return FileUtil.readUtf8String("E:\\workspace\\social-media-analytics\\social-media-analytics-modules\\social-media-analytics-admin\\src\\main\\resources\\login.txt");
    }

    @Override
    public void saveLoginQrCode(String accountId, String src) {
        log.info(src);
    }

    @Override
    public String getLoginQrCode(String accountId) {
        return "";
    }

    @Override
    public void saveLoginState(String accountId, String content) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        FileUtil.writeUtf8String(content, "E:\\workspace\\social-media-analytics\\social-media-analytics-modules\\social-media-analytics-admin\\src\\main\\resources\\login_state.json");
    }

    @Override
    public String getLoginState(String accountId) {
        try {
            return FileUtil.readUtf8String("E:\\workspace\\social-media-analytics\\social-media-analytics-modules\\social-media-analytics-admin\\src\\main\\resources\\login_state.json");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getCrawlerLoginState(String platform) {
        return "";
    }

    @Override
    public void updateCrawlerLoginState(String platform, String content) {

    }

    @Override
    public Map<String, String> getCrawlerLoginStateMap() {
        return Collections.emptyMap();
    }


}

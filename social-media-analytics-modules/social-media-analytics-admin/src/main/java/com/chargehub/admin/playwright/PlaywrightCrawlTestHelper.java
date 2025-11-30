package com.chargehub.admin.playwright;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Zhanghaowei
 * @since 2025-11-29 23:37
 */
@Slf4j
public class PlaywrightCrawlTestHelper implements PlaywrightCrawlHelper {

    @Override
    public String checkSmsCode(String accountId) {
        return FileUtil.readUtf8String("D:\\ideaProject\\social-media-analytics\\social-media-analytics-modules\\social-media-analytics-admin\\src\\main\\resources\\login.txt");
    }

    @Override
    public void saveLoginQrCode(String accountId, String src) {
        log.info(src);
    }

    @Override
    public void saveLoginState(String accountId, String content) {
        FileUtil.writeUtf8String(content, "D:\\ideaProject\\social-media-analytics\\social-media-analytics-modules\\social-media-analytics-admin\\src\\main\\resources\\login_state.json");
    }

    @Override
    public String getLoginState(String accountId) {
        return FileUtil.readUtf8String("D:\\ideaProject\\social-media-analytics\\social-media-analytics-modules\\social-media-analytics-admin\\src\\main\\resources\\login_state.json");
    }


}

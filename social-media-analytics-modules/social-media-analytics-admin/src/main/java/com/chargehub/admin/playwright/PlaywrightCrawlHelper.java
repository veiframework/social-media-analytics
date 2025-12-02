package com.chargehub.admin.playwright;

/**
 * @author Zhanghaowei
 * @since 2025-11-29 23:34
 */
public interface PlaywrightCrawlHelper {

    void saveSmsCode(String accountId, String smsCode);

    String checkSmsCode(String accountId);

    void saveLoginQrCode(String accountId, String src);

    String getLoginQrCode(String accountId);

    void saveLoginState(String accountId, String content);

    String getLoginState(String accountId);
}

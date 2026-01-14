//package com.chargehub.admin.playwright;
//
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.thread.ThreadUtil;
//import cn.hutool.core.util.RandomUtil;
//import com.chargehub.admin.work.domain.SocialMediaWork;
//import com.chargehub.common.security.utils.JacksonUtil;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.microsoft.playwright.ElementHandle;
//import com.microsoft.playwright.Page;
//import com.microsoft.playwright.Response;
//import com.microsoft.playwright.Route;
//import com.microsoft.playwright.options.WaitForSelectorState;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.Assert;
//
//import java.util.List;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.function.Consumer;
//
///**
// * 代理服务器      String douyin = "https://proxy.scdn.io";
// *
// * @author Zhanghaowei
// * @since 2025-11-29 10:36
// */
//@Service
//@Slf4j
//public class PlaywrightManager {
//
//    @Autowired
//    private PlaywrightCrawlHelper playwrightCrawlHelper;
//
//
//    private static final boolean SMS_LOGIN = true;
//
//    public void loginWebDouYin() {
//        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser("15514557561", "qwe6181513640031", null)) {
//            Page page = playwrightBrowser.newPage();
//            page.navigate("https://www.douyin.com", new Page.NavigateOptions().setTimeout(60_000));
//            log.info("进入页面");
//            page.waitForSelector("input[placeholder='请输入手机号']");
//            if (SMS_LOGIN) {
//                page.click("text=验证码登录");
//            } else {
//                page.click("text=密码登录");
//            }
//            log.info("检测到输入手机号");
//            page.fill("input[placeholder='请输入手机号']", playwrightBrowser.getUsername());
//            log.info("手机号填充完毕");
//            if (SMS_LOGIN) {
//                page.click("text=获取验证码");
//                log.info("点击获取验证码,等待...");
//                String smsCode = "";
//                for (int i = 0; i < 6000; i++) {
//                    smsCode = playwrightCrawlHelper.checkSmsCode("");
//                    if (StringUtils.isNotBlank(smsCode)) {
//                        break;
//                    }
//                    ThreadUtil.safeSleep(10);
//                }
//                log.info("填充验证码{}", smsCode);
//                page.fill("input[placeholder='请输入验证码']", smsCode);
//            } else {
//                log.info("填充密码");
//                page.fill("input[placeholder='请输入密码']", playwrightBrowser.getPassword());
//            }
//            log.info("发出登陆请求");
//            //TODO 此处频繁登录有可能触发图形验证码
//            page.click("text=登录/注册");
//            Response smsResponse = page.waitForResponse(res -> res.url().contains("sms_login"), new Page.WaitForResponseOptions().setTimeout(60_000), () -> {
//            });
//            String responseBody = new String(smsResponse.body());
//            JsonNode jsonNode = JacksonUtil.toObj(responseBody);
//            if (jsonNode.at("/data/description").asText().contains("错误")) {
//                throw new IllegalArgumentException("登录失败,验证码或手机号不正确!");
//            }
//            if (page.isVisible("text=使用原设备扫码")) {
//                page.waitForSelector("text=使用原设备扫码", new Page.WaitForSelectorOptions().setTimeout(60_000));
//                log.info("等待二维码");
//                page.waitForSelector("img[aria-label='二维码']", new Page.WaitForSelectorOptions()
//                        .setTimeout(60_000));
//
//                // 获取 img 的 src 属性（Base64 字符串）
//                String src = page.getAttribute("img[aria-label='二维码']", "src");
//                playwrightCrawlHelper.saveLoginQrCode("", src);
//            }
//            page.waitForSelector("span[data-e2e='live-avatar']",
//                    new Page.WaitForSelectorOptions().setTimeout(60_000));
//            log.info("登陆成功");
//            // 保存登录状态到文件（包含 cookies + localStorage）
//            String storageState = page.context().storageState();
//            playwrightCrawlHelper.saveLoginState("", storageState);
//        }
//    }
//
//    public static void crawlRedNoteLogin() {
//        String s = FileUtil.readUtf8String("red_note_state.json");
//        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(s)) {
//            Page page = playwrightBrowser.newPage();
//            page.navigate("https://www.xiaohongshu.com/", new Page.NavigateOptions().setTimeout(60_000));
//            log.info("进入页面");
//            page.waitForSelector(".qrcode-img");
//            ElementHandle element = page.querySelector(".qrcode-img");
//            String src = element.getAttribute("src");
//            System.out.println(src);
//            page.waitForSelector("a[title='我']", new Page.WaitForSelectorOptions().setTimeout(180_000));
//            String storageState = page.context().storageState();
//            FileUtil.writeUtf8String(storageState, "red_note_state.json");
//        }
//    }
//
//    public static String crawlDouYinWorkList(String enterUrl, String loginState, Consumer<String> consumer) {
//        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(loginState)) {
//            Page page = playwrightBrowser.newPage();
//            page.onResponse(res -> {
//                if (res.url().contains("/aweme/v1/web/aweme/post/")) {
//                    String s = new String(res.body());
//                    consumer.accept(s);
//                }
//            });
//            page.navigate(enterUrl, new Page.NavigateOptions().setTimeout(60_000));
//            log.info("开始滚动加载数据");
//            for (int i = 0; i < 600; i++) {
//                if (page.isVisible("text=暂时没有更多了")) {
//                    break;
//                }
//                page.mouse().wheel(0, 600);
//                page.evaluate("const container = document.querySelector(\"div[class*='parent-route-container']\"); container.scrollBy(0, 600)");
//                ThreadUtil.safeSleep(100);
//            }
//            return page.context().storageState();
//        }
//    }
//
//    public static SocialMediaWork getWork(String workUrl) {
//        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(new PlaywrightCrawlTestHelper().getLoginState(""))) {
//            Page page = playwrightBrowser.newPage();
//            Response response = page.waitForResponse(res -> res.url().contains("/creator/item/mget"), new Page.WaitForResponseOptions().setTimeout(60_000), () -> {
//                page.navigate(workUrl, new Page.NavigateOptions().setTimeout(60_000));
//            });
//            Assert.isTrue(response.ok());
//            String body = new String(response.body());
//            System.out.println(body);
//            return null;
//        }
//    }
//
//    public static void crawDouYinLogin() {
//        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(new PlaywrightCrawlTestHelper().getLoginState(""))) {
//            Page page = playwrightBrowser.newPage();
//            page.navigate("https://www.douyin.com", new Page.NavigateOptions().setTimeout(60_000));
//            log.info("进入页面");
//            page.waitForSelector("span[data-e2e='live-avatar']",
//                    new Page.WaitForSelectorOptions().setTimeout(60_000));
//            String s = page.context().storageState();
//            FileUtil.writeUtf8String(s,"douyin_state");
//
//        }
//    }
//
//    public static void crawlKuaiShouLogin() {
//        String s = FileUtil.readUtf8String("kuaishou_state.json");
//        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(s)) {
//            Page page = playwrightBrowser.newPage();
//            page.navigate("https://v.kuaishou.com/7GK5P2ic", new Page.NavigateOptions().setTimeout(60_000));
//            log.info("进入页面");
//            page.click("[class*='sidebar-login-button']");
//            page.waitForSelector(".qrcode-img");
//            ElementHandle element = page.querySelector(".qrcode-img");
//            element.waitForSelector("img");
//            ElementHandle imgElement = element.querySelector("img");
//            String src = imgElement.getAttribute("src");
//            System.out.println(src);
//            page.waitForSelector("[class*='sidebar-login-button']", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN));
//            String storageState = page.context().storageState();
//            FileUtil.writeUtf8String(storageState, "kuaishou_state.json");
//        }
//    }
//
//    public static void kuaishouWorks() {
//        String s = FileUtil.readUtf8String("kuaishou_state.json");
//        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(s)) {
//            Page page = playwrightBrowser.newPage();
//            log.info("进入页面");
//            try {
//                Response response = page.waitForResponse(url -> url.ok() && url.url().contains("graphql"), new Page.WaitForResponseOptions().setTimeout(10_000), () -> {
//                    page.navigate("https://v.kuaishou.com/7GK5P2ic", new Page.NavigateOptions().setTimeout(60_000));
//                    String url = page.url();
//                    if (url.contains("short-video")) {
//
//                        return;
//                    }
//                    System.out.println("图文");
//                    ThreadUtil.safeSleep(40_000);
//                    throw new IllegalArgumentException("不是图文");
//                });
//                byte[] body = response.body();
//                System.out.println(new String(body) + "结果" + DateUtil.now());
//            } catch (Exception e) {
//                if (e.getMessage().equals("不是图文")) {
//                    System.out.println(e.getMessage());
//                }
//                System.out.println("结果" + DateUtil.now());
//            }
//        }
//    }
//
//    public static void kuaishouWorks2() {
//        String s = FileUtil.readUtf8String("kuaishou_state.json");
//        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(s)) {
//            Page page = playwrightBrowser.newPage();
//            log.info("进入页面");
//
//            AtomicInteger atomicInteger = new AtomicInteger(-1);
//            page.onResponse(response -> {
//                if (response.url().contains("graphql")) {
//                    String str = new String(response.body());
//                    if (str.contains("commentCount")) {
//                        atomicInteger.set(233);
//                    }
//                }
//            });
//            page.navigate("https://v.kuaishou.com/7GK5P2ic", new Page.NavigateOptions().setTimeout(60_000));
////            while (atomicInteger.get() == -1) {
////                page.waitForTimeout(1000);
////            }
////            System.out.println(atomicInteger.get());
//            page.waitForFunction("typeof INIT_STATE !== 'undefined'", null, new Page.WaitForFunctionOptions().setTimeout(30_000L));
//
//        }
//    }
//
//    public static void crawlRedNotes(String url, List<String> workIds) {
//        String s = FileUtil.readUtf8String("red_note_state.json");
//        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(s)) {
//            Page page = playwrightBrowser.newPage();
//            page.navigate(url, new Page.NavigateOptions().setTimeout(60_000));
//            log.info("进入页面");
//            page.waitForTimeout(RandomUtil.randomInt(300, 500));
//            if (page.isVisible("text=登录后推荐更懂你的笔记")) {
//                log.error("需要重新登录");
//                return;
//            }
//            page.route(route -> route.contains(".jpeg") || route.contains(".webp") || route.contains(".png") || route.contains(".jpg"), Route::abort);
//            LinkedBlockingQueue<JsonNode> list = new LinkedBlockingQueue<>();
//            AtomicBoolean hasMore = new AtomicBoolean(true);
//            AtomicBoolean processing = new AtomicBoolean(false);
//            page.onResponse(res -> {
//                if (!(res.url().contains("/api/sns/web/v1/user_posted") && "get".equalsIgnoreCase(res.request().method()) && res.ok())) {
//                    return;
//                }
//                processing.set(true);
//                String body = new String(res.body());
//                JsonNode jsonNode = JacksonUtil.toObj(body);
//                JsonNode notes = jsonNode.at("/data/notes");
//                if (notes.isEmpty()) {
//                    return;
//                }
//                fetchDetail(workIds, notes, page, list);
//                processing.set(false);
//                hasMore.set(jsonNode.at("/data/has_more").asBoolean());
//            });
//            page.navigate(url, new Page.NavigateOptions().setTimeout(60_000));
//            page.waitForFunction("typeof __INITIAL_STATE__ !== 'undefined'", null, new Page.WaitForFunctionOptions().setTimeout(30_000L));
//            String string = (String) page.evaluate("JSON.stringify(__INITIAL_STATE__.user.notes._rawValue)");
//            if (StringUtils.isBlank(string)) {
//                return;
//            }
//            JsonNode jsonNode = JacksonUtil.toObj(string);
//            if (jsonNode.isEmpty()) {
//                return;
//            }
//            JsonNode firstPage = jsonNode.get(0);
//            if (firstPage.isEmpty()) {
//                return;
//            }
//            fetchDetail(workIds, firstPage, page, list);
//            int size = 0;
//            while (hasMore.get()) {
//                page.waitForTimeout(2000);
//                if (processing.get()) {
//                    continue;
//                }
//                JsonNode poll = list.poll();
//                if (poll != null) {
//                    FileUtil.writeUtf8String(poll + "\r\n", "tikhub-rednote-work.json");
//                    size = size + poll.size();
//                }
//                page.mouse().wheel(0, 400);
//            }
//            System.out.println("结束" + size);
//            page.waitForTimeout(600_00000);
//
//        }
//    }
//
//    private static void fetchDetail(List<String> workIds, JsonNode firstPage, Page page, LinkedBlockingQueue<JsonNode> list) {
//        for (JsonNode node : firstPage) {
//            String noteId = node.at("/id").asText();
//            if (StringUtils.isBlank(noteId)) {
//                noteId = node.at("/note_id").asText();
//            }
//            if (workIds.contains(noteId)) {
//                try {
//                    String finalNoteId = noteId;
//                    Response response = page.waitForResponse(res -> res.ok() && res.url().contains("/api/sns/web/v1/feed") && "post".equalsIgnoreCase(res.request().method()), () -> {
//                        ElementHandle elementHandle = page.querySelector("a[href*='/user/profile/'][href*='" + finalNoteId + "']");
//                        elementHandle.click();
//                    });
//                    if (response != null && response.ok()) {
//                        String body = new String(response.body());
//                        JsonNode jsonNode = JacksonUtil.toObj(body);
//                        JsonNode items = jsonNode.at("/data/items");
//                        if (!items.isEmpty()) {
//                            JsonNode item = items.get(0);
//                            System.out.println(item);
//                            list.offer(item);
//                        }
//                    }
//                    page.click("div[class*='close close-mask-dark']");
//                } catch (Exception e) {
//                    log.error("错误" + noteId);
//                }
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        String douyin = "https://www.douyin.com/user/MS4wLjABAAAAhSaD3wD3rsLVCezq2LaPpCXrRDjBb8R8Np4SnAcZQE4";
//        String realUrl = "https://www.douyin.com/video/7577294438381237403";
//        String shareUrl = "https://v.douyin.com/BnP9rp9WtMw/ 11/28 nqR:/ x@s.eO";
//        String xiaohongshu = "http://xhslink.com/o/6vwGmnLDROY";
////        getWork("https://creator.douyin.com/creator-micro/work-management/work-detail/7578754998953051121");
////        String read = FileUtil.readUtf8String("E:\\workspace\\social-media-analytics\\social-media-analytics-modules\\social-media-analytics-admin\\src\\main\\resources\\login_state.json");
////        AtomicInteger a = new AtomicInteger();
////        crawlDouYinWorkList("https://www.douyin.com/user/MS4wLjABAAAAJ6GUXA-U_4pDM-vPq_Xl2onTfM-MFA2j9WEjH9mzk-BOiM2MoNcRp56juY9Pbb_c", read, (res) -> {
////            System.out.println(a.getAndIncrement());
////        });
////        crawlRedNoteLogin();
////        crawlKuaiShouLogin();
////        crawDouYinLogin();
////        kuaishouWorks2();
////        String xiaohongshuPage = "https://www.xiaohongshu.com/user/profile/5ac0afbf4eacab35ebf496e4?xsec_token=ABPbIISrLrVsEu64NSoMv-V0lvBpAH-ur4ae-fAkwioWY=&xsec_source=pc_note";
////        crawlRedNotes(xiaohongshuPage, Lists.newArrayList("691edd120000000019026f42", "68fa296400000000030136a3", "691890fb000000001b026c6a", "6841441e000000000303f9f1", "6749618f00000000060176e7", "63c60db0000000001b026b20"));
//        crawDouYinLogin();
//    }
//
//
//}

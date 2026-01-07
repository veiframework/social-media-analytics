package com.chargehub.admin.datasync.tikhub;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.DataSyncMessageQueue;
import com.chargehub.admin.datasync.DataSyncService;
import com.chargehub.admin.datasync.domain.*;
import com.chargehub.admin.enums.MediaTypeEnum;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.WorkTypeEnum;
import com.chargehub.admin.playwright.BrowserConfig;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.core.utils.JsoupUtil;
import com.chargehub.common.core.utils.MessageFormatUtils;
import com.chargehub.common.security.utils.DictUtils;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.BoundingBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@Service
public class KuaiShouSyncServiceImpl implements DataSyncService {

    private static final String PROFILE_INFO = "/api/v1/kuaishou/app/fetch_one_user_v2";

    private static final Integer KUAISHOU_RETRY = 4;

    @Autowired
    private HubProperties hubProperties;

    @Autowired
    private DataSyncMessageQueue dataSyncMessageQueue;

    @Override
    public SocialMediaPlatformEnum platform() {
        return SocialMediaPlatformEnum.KUAI_SHOU;
    }

    @Override
    public SocialMediaUserInfo getSocialMediaUserInfo(String secUserId) {
        return null;
    }

    @Override
    public <T> SocialMediaWorkResult<T> getWorks(SocialMediaAccountVo socialMediaAccount, String cursor, Integer count) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> SocialMediaWorkResult<T> getWorks(DataSyncWorksParams params) {
        Map<String, String> workUids = params.getWorkUids();
        BrowserContext browserContext = params.getBrowserContext();
        SocialMediaWorkResult<SocialMediaWork> socialMediaWorkResult = new SocialMediaWorkResult<>();
        List<SocialMediaWork> socialMediaWorks = new ArrayList<>();
        for (Map.Entry<String, String> entry : workUids.entrySet()) {
            ThreadUtil.safeSleep(RandomUtil.randomInt(500, 3000));
            String url = entry.getValue();
            DataSyncParamContext dataSyncParamContext = new DataSyncParamContext();
            dataSyncParamContext.setShareLink(url);
            dataSyncParamContext.setBrowserContext(browserContext);
            dataSyncParamContext.setScheduler(true);
            SocialMediaWorkDetail<SocialMediaWork> workDetail = this.getWork(dataSyncParamContext);
            if (workDetail == null) {
                continue;
            }
            socialMediaWorks.add(workDetail.getWork());
        }
        socialMediaWorkResult.setWorks(socialMediaWorks);
        return (SocialMediaWorkResult<T>) socialMediaWorkResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> SocialMediaWorkDetail<T> getWork(DataSyncParamContext dataSyncParamContext) {
        boolean isScheduler = dataSyncParamContext.isScheduler();
        BrowserContext browserContext = dataSyncParamContext.getBrowserContext();
        String shareLink = dataSyncParamContext.getShareLink();
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(browserContext)) {
            Page page = playwrightBrowser.getPage();
            AtomicInteger atomicInteger = new AtomicInteger(-1);
            page.onResponse(response -> {
                boolean graphql = response.url().contains("graphql");
                if (!graphql) {
                    return;
                }
                String postData = response.request().postData();
                if (!postData.contains("commentListQuery")) {
                    return;
                }
                byte[] body = response.body();
                Integer commentCountV2 = JacksonUtil.readField(body, "commentCountV2", Integer.class);
                if (commentCountV2 == null) {
                    return;
                }
                atomicInteger.set(commentCountV2);
            });
            page.navigate(shareLink, new Page.NavigateOptions().setTimeout(90_000));
            String currentUrl = page.url();
            int commentNum = 0;
            int collectNum = 0;
            int thumbNum = 0;
            int shareNum = 0;
            int playNum = 0;
            String nickname = "";
            String uid = "";
            String desc = "";
            String title;
            String topics;
            String customType;
            String workUid = "";
            Date postTime = null;
            String mediaType = "";
            String workType = "";
            String secUid = "";
            if (currentUrl.contains("short-video")) {
                if (page.isVisible("text=立即登录")) {
                    log.error("快手需要重新登陆");
                    return null;
                }
                if (page.isVisible("text=作品已失效")) {
                    log.error("快手检测到对方已删除此作品! {}", dataSyncParamContext.getShareLink());
                    SocialMediaWork socialMediaWork = new SocialMediaWork();
                    socialMediaWork.setShareLink(dataSyncParamContext.getShareLink());
                    socialMediaWork.setWorkUid("-1");
                    return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, null);
                }
                for (int i = 0; i < 60; i++) {
                    int integer = atomicInteger.get();
                    if (integer != -1) {
                        commentNum = integer;
                        break;
                    }
                    page.waitForTimeout(1000);
                }
                String html = page.content();
                Document doc = Jsoup.parse(html, "", Parser.htmlParser());
                Element script = doc.select("script").stream().filter(i -> i.html(new StringBuilder()).toString().contains("__APOLLO_STATE__"))
                        .findFirst().orElse(null);
                if (script == null) {
                    log.error("快手内容没解析到" + shareLink);
                    return null;
                }
                String json = script.html().replace("window.__APOLLO_STATE__ = ", "");
                JsonNode jsonNode = JacksonUtil.toObj(json);
                JsonNode defaultClient = jsonNode.get("defaultClient");
                Iterator<Map.Entry<String, JsonNode>> fields = defaultClient.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    String key = entry.getKey();
                    JsonNode value = entry.getValue();
                    if (key.startsWith("VisionVideoDetailPhoto")) {
                        workUid = value.get("id").asText();
                        thumbNum = value.get("realLikeCount").asInt();
                        playNum = BrowserConfig.clearWord(value.get("viewCount").asText());
                        shareNum = 0;
                        collectNum = 0;
                        desc = value.get("caption").asText();
                        postTime = DateUtil.date(value.get("timestamp").asLong(0));
                        mediaType = MediaTypeEnum.VIDEO.getType();
                        workType = WorkTypeEnum.NORMAL_VIDEO.getType();
                    }
                    if (key.contains("VisionVideoDetailAuthor")) {
                        nickname = value.get("name").asText();
                        secUid = value.get("id").asText();
                        if (!isScheduler) {
                            Page popupPage = page.waitForPopup(() -> {
                                ElementHandle element = page.querySelector("span[class*='profile-user-name-title']");
                                BoundingBox box = element.boundingBox();
                                double x = box.x + box.width / 2;
                                double y = box.y + box.height / 2;
                                page.mouse().move(x, y);
                                page.mouse().click(x, y);
                            });
                            String fullText = popupPage.textContent(":text-matches(' 快手号：.*')");
                            uid = fullText.replace(" 快手号：", "");
                        }
                    }
                }
            } else {
                if (page.isVisible("text=作品不存在，可能已经被删除。")) {
                    log.error("快手检测到对方已删除此作品! {}", dataSyncParamContext.getShareLink());
                    SocialMediaWork socialMediaWork = new SocialMediaWork();
                    socialMediaWork.setShareLink(dataSyncParamContext.getShareLink());
                    socialMediaWork.setWorkUid("-1");
                    return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, null);
                }
                String html = page.content();
                Document doc = Jsoup.parse(html, "", Parser.htmlParser());
                Element script = doc.select("script").stream().filter(i -> i.html(new StringBuilder()).toString().contains("window.INIT_STATE"))
                        .findFirst().orElse(null);
                if (script == null) {
                    log.error("快手内容没解析到" + shareLink);
                    return null;
                }
                String json = script.html().replace("window.INIT_STATE = ", "");
                JsonNode obj = JacksonUtil.toObj(json);
                JsonNode jsonNode = null;
                for (JsonNode node : obj) {
                    if (node.has("photo")) {
                        jsonNode = node;
                        break;
                    }
                }
                Assert.notNull(jsonNode, "快手获取作品失败" + shareLink);
                JsonNode countsNode = jsonNode.get("counts");
                collectNum = countsNode.get("collectionCount").asInt(0);
                JsonNode photoNode = jsonNode.get("photo");
                thumbNum = photoNode.get("likeCount").asInt(0);
                shareNum = photoNode.path("shareCount").asInt(0);
                commentNum = photoNode.get("commentCount").asInt(0);
                playNum = photoNode.get("viewCount").asInt(0);
                nickname = photoNode.get("userName").asText();
                uid = photoNode.get("userId").asText();
                secUid = photoNode.get("userEid").asText();
                desc = photoNode.get("caption").asText();
                postTime = DateUtil.date(photoNode.get("timestamp").asLong(0));
                String shareInfo = photoNode.get("share_info").asText();
                Map<String, String> paramMap = HttpUtil.decodeParamMap(shareInfo, StandardCharsets.UTF_8);
                workUid = paramMap.get("photoId");
                mediaType = MediaTypeEnum.PICTURE.getType();
                workType = WorkTypeEnum.RICH_TEXT.getType();
            }
            title = MessageFormatUtils.cleanDescription(desc);
            topics = MessageFormatUtils.extractHashtagsStr(desc);
            customType = "";
            Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
            for (Map.Entry<String, String> entry : socialMediaCustomType.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (desc.contains(k)) {
                    customType = v;
                }
            }
            SocialMediaWork socialMediaWork = new SocialMediaWork();
            socialMediaWork.setUrl(currentUrl);
            socialMediaWork.setShareLink(shareLink);
            socialMediaWork.setPlatformId(SocialMediaPlatformEnum.KUAI_SHOU.getDomain());
            socialMediaWork.setDescription(desc);
            socialMediaWork.setWorkUid(workUid);
            socialMediaWork.setPostTime(postTime);
            socialMediaWork.setMediaType(mediaType);
            socialMediaWork.setType(workType);
            socialMediaWork.setThumbNum(thumbNum);
            socialMediaWork.setCollectNum(collectNum);
            socialMediaWork.setShareNum(shareNum);
            socialMediaWork.setCommentNum(commentNum);
            socialMediaWork.setLikeNum(0);
            socialMediaWork.setPlayNum(playNum);
            socialMediaWork.setTitle(title);
            socialMediaWork.setTopics(topics);
            socialMediaWork.setCustomType(customType);
            socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
            SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
            socialMediaUserInfo.setSecUid(secUid);
            socialMediaUserInfo.setNickname(nickname);
            socialMediaUserInfo.setUid(uid);
            return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, socialMediaUserInfo);
        }
    }

    @Override
    public <T> SocialMediaWorkDetail<T> fetchWork(DataSyncParamContext dataSyncParamContext) {
        return dataSyncMessageQueue.retryWithExponentialBackoff(() -> this.fetchWork0(dataSyncParamContext), KUAISHOU_RETRY, dataSyncParamContext.getShareLink());
    }

    @SuppressWarnings("unchecked")
    public <T> SocialMediaWorkDetail<T> fetchWork0(DataSyncParamContext dataSyncParamContext) {
        boolean isScheduler = dataSyncParamContext.isScheduler();
        String shareLink = dataSyncParamContext.getShareLink();
        String redirectUrl = dataSyncParamContext.getRedirectUrl();
        boolean isVideo;
        if (StringUtils.isBlank(redirectUrl)) {
            isVideo = dataSyncParamContext.getMediaType().equals(MediaTypeEnum.VIDEO.getType());
        } else {
            isVideo = redirectUrl.contains("short-video");
        }
        String currentUrl = redirectUrl;
        int commentNum = 0;
        int collectNum = 0;
        int thumbNum = 0;
        int shareNum = 0;
        int playNum = 0;
        String nickname = "";
        String uid = "";
        String desc = "";
        String title;
        String topics;
        String customType;
        String workUid = "";
        Date postTime = null;
        String mediaType = "";
        String workType = "";
        String secUid = "";
        if (isVideo) {
            try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(PlaywrightBrowser.buildProxy()); Page page = playwrightBrowser.newPage()) {
                AtomicReference<String> navigateContent = new AtomicReference<>();
                Response commentRes = page.waitForResponse(res -> {
                    boolean graphql = res.url().contains("graphql");
                    if (graphql) {
                        String postData = res.request().postData();
                        return postData.contains("commentListQuery");
                    }
                    return false;
                }, () -> {
                    Response navigate = page.navigate(shareLink, new Page.NavigateOptions().setTimeout(BrowserConfig.LOAD_PAGE_TIMEOUT));
                    navigateContent.set(new String(navigate.body()));
                });
                Integer commentCountV2 = JacksonUtil.readField(commentRes.body(), "commentCountV2", Integer.class);
                commentNum = commentCountV2 == null ? 0 : commentCountV2;
                String content = navigateContent.get();
                Document document = JsoupUtil.parse(content);
                if (document.text().contains("作品已失效")) {
                    log.error("快手检测到对方已删除此作品! {}", dataSyncParamContext.getShareLink());
                    SocialMediaWork socialMediaWork = new SocialMediaWork();
                    socialMediaWork.setShareLink(dataSyncParamContext.getShareLink());
                    socialMediaWork.setWorkUid("-1");
                    return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, null);
                }
                String json = JsoupUtil.findContentInScript(document, "window.__APOLLO_STATE__=");
                Assert.hasText(json, "触发快手限流了,开始重试");
                JsonNode jsonNode = JacksonUtil.toObj(json);
                JsonNode defaultClient = jsonNode.get("defaultClient");
                Iterator<Map.Entry<String, JsonNode>> fields = defaultClient.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    String key = entry.getKey();
                    JsonNode value = entry.getValue();
                    if (key.startsWith("VisionVideoDetailPhoto")) {
                        workUid = value.get("id").asText();
                        thumbNum = value.get("realLikeCount").asInt();
                        playNum = BrowserConfig.clearWord(value.get("viewCount").asText());
                        shareNum = 0;
                        collectNum = 0;
                        desc = value.get("caption").asText();
                        postTime = DateUtil.date(value.get("timestamp").asLong(0));
                        mediaType = MediaTypeEnum.VIDEO.getType();
                        workType = WorkTypeEnum.NORMAL_VIDEO.getType();
                    }
                    if (key.contains("VisionVideoDetailAuthor")) {
                        nickname = value.get("name").asText();
                        secUid = value.get("id").asText();
                        if (isScheduler) {
                            continue;
                        }
                        try {
                            Page popupPage = page.waitForPopup(() -> {
                                ElementHandle element = page.querySelector("span[class*='profile-user-name-title']");
                                BoundingBox box = element.boundingBox();
                                double x = box.x + box.width / 2;
                                double y = box.y + box.height / 2;
                                page.mouse().move(x, y);
                                page.mouse().click(x, y);
                            });
                            String fullText = popupPage.textContent(":text-matches(' 快手号：.*')");
                            uid = fullText.replace(" 快手号：", "");
                        } catch (Exception e) {
                            log.error("获取快手号异常", e);
                        }
                    }
                }
            }
        } else {
            HttpRequest httpRequest = HttpUtil.createGet(shareLink)
                    .setFollowRedirects(true)
                    .timeout(60000)
                    .setProxy(dataSyncParamContext.getProxy())
                    .headerMap(BrowserConfig.BROWSER_HEADERS, true);
            try (HttpResponse response = httpRequest.execute()) {
                currentUrl = httpRequest.getUrl();
                InputStream inputStream = response.bodyStream();
                String json = JsoupUtil.findContentInScript(inputStream, "window.INIT_STATE = ");
                JsonNode obj = JacksonUtil.toObj(json);
                JsonNode jsonNode = null;
                for (JsonNode node : obj) {
                    if (node.has("photo")) {
                        jsonNode = node;
                        break;
                    }
                }
                if (jsonNode == null) {
                    log.error("快手检测到对方已删除此作品! {}", dataSyncParamContext.getShareLink());
                    SocialMediaWork socialMediaWork = new SocialMediaWork();
                    socialMediaWork.setShareLink(dataSyncParamContext.getShareLink());
                    socialMediaWork.setWorkUid("-1");
                    return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, null);
                }
                JsonNode countsNode = jsonNode.get("counts");
                collectNum = countsNode.get("collectionCount").asInt(0);
                JsonNode photoNode = jsonNode.get("photo");
                thumbNum = photoNode.get("likeCount").asInt(0);
                shareNum = photoNode.path("shareCount").asInt(0);
                commentNum = photoNode.get("commentCount").asInt(0);
                playNum = photoNode.get("viewCount").asInt(0);
                nickname = photoNode.get("userName").asText();
                uid = photoNode.get("userId").asText();
                secUid = photoNode.get("userEid").asText();
                desc = photoNode.get("caption").asText();
                postTime = DateUtil.date(photoNode.get("timestamp").asLong(0));
                String shareInfo = photoNode.get("share_info").asText();
                Map<String, String> paramMap = HttpUtil.decodeParamMap(shareInfo, StandardCharsets.UTF_8);
                workUid = paramMap.get("photoId");
                mediaType = MediaTypeEnum.PICTURE.getType();
                workType = WorkTypeEnum.RICH_TEXT.getType();
            }
        }
        title = MessageFormatUtils.cleanDescription(desc);
        topics = MessageFormatUtils.extractHashtagsStr(desc);
        SocialMediaWork socialMediaWork = new SocialMediaWork();
        socialMediaWork.setUrl(currentUrl);
        socialMediaWork.setShareLink(shareLink);
        socialMediaWork.setPlatformId(SocialMediaPlatformEnum.KUAI_SHOU.getDomain());
        socialMediaWork.setDescription(desc);
        socialMediaWork.setWorkUid(workUid);
        socialMediaWork.setPostTime(postTime);
        socialMediaWork.setMediaType(mediaType);
        socialMediaWork.setType(workType);
        socialMediaWork.setThumbNum(thumbNum);
        socialMediaWork.setCollectNum(collectNum);
        socialMediaWork.setShareNum(shareNum);
        socialMediaWork.setCommentNum(commentNum);
        socialMediaWork.setLikeNum(0);
        socialMediaWork.setPlayNum(playNum);
        socialMediaWork.setTitle(title);
        socialMediaWork.setTopics(topics);
        socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
        SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
        socialMediaUserInfo.setSecUid(secUid);
        socialMediaUserInfo.setNickname(nickname);
        socialMediaUserInfo.setUid(uid);
        return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, socialMediaUserInfo);
    }

    public static void main(String[] args) {
        String commentRq = "https://www.kuaishou.com/graphql";
        String profile = "https://www.kuaishou.com/rest/v/profile/user";
        String profileBody = "{\"user_id\":\"3xgh2i9dfvu23i9\"}";
        String commentBody = "{\"operationName\":\"commentListQuery\",\"variables\":{\"photoId\":\"3xm725ngdnh8hpg\",\"pcursor\":\"\"},\"query\":\"query commentListQuery($photoId: String, $pcursor: String) {\\n  visionCommentList(photoId: $photoId, pcursor: $pcursor) {\\n    commentCount\\n    commentCountV2\\n    pcursor\\n    rootCommentsV2 {\\n      commentId\\n      authorId\\n      authorName\\n      content\\n      headurl\\n      timestamp\\n      hasSubComments\\n      likedCount\\n      liked\\n      status\\n      __typename\\n    }\\n    pcursorV2\\n    rootComments {\\n      commentId\\n      authorId\\n      authorName\\n      content\\n      headurl\\n      timestamp\\n      likedCount\\n      realLikedCount\\n      liked\\n      status\\n      authorLiked\\n      subCommentCount\\n      subCommentsPcursor\\n      subComments {\\n        commentId\\n        authorId\\n        authorName\\n        content\\n        headurl\\n        timestamp\\n        likedCount\\n        realLikedCount\\n        liked\\n        status\\n        authorLiked\\n        replyToUserName\\n        replyTo\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}";
        HttpRequest httpRequest = HttpUtil.createPost(commentRq)
                .body(commentBody)
                .cookie("kpf=PC_WEB; clientid=3; did=web_505a3785b73d87468952f289a74cb860; kwpsecproductname=kuaishou-vision; kwpsecproductname=kuaishou-vision; kwssectoken=DwlqT+YY4EroHs0daZItyq2g0dnUazUMgRl9rIqC9+n/3Wm5KOmljKcX0uJkIHD/qkukEDo6hlX8eUxhZsXahA==; kwscode=e24d61051f3cf99e525e2dfbaf92ce4023639a2c92e21bfb1e3b2bd02442fbb5; kwfv1=PnGU+9+Y8008S+nH0U+0mjPf8fP08f+98f+nLlwnrIP9+Sw/ZFGfzY+eGlGf+f+e4SGfbYP0QfGnLFwBLU80mYG9rh80LE8eZhPeZFweGIPnH7GnLM+/cFwBpY+fPE8/chG9pYG/zD8eL7+0zY+eHAGnzSPADh80Z9GAclw/p0PAzSP/mjPAH98/LFP/HhweYfG/DhP0HF8fpY+eYj+eYSPI==; kwssectoken=bqTlH5G/S/4y60ldJKXeN8lN+86KFrYZIYmEVWnlZNDo7pDYCs8P82HLRe4Oi1UflWmveQGRq14U0HxsZTYbFg==; kwscode=221e9b74fd391ece5b7009f34d24ccb1e330605d70e7fcab17150fd8c6f4f944; ktrace-context=1|MS44Nzg0NzI0NTc4Nzk2ODY5LjU2Mjg2MTQxLjE3NjY5MjIyMjgzOTMuMjI3MzgyMA==|MS44Nzg0NzI0NTc4Nzk2ODY5Ljg1MTQyODc4LjE3NjY5MjIyMjgzOTMuMjI3MzgyMQ==|0|webservice-user-growth-node|webservice|true|src-Js; kpn=KUAISHOU_VISION")
                .headerMap(BrowserConfig.BROWSER_HEADERS, true);
        try (HttpResponse response = httpRequest.execute()) {
            System.out.println(response.body());
        }

    }
}

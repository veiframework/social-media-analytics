package com.chargehub.admin.datasync.tikhub;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
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
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
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


    @Override
    public <T> SocialMediaWorkDetail<T> getWork(DataSyncParamContext dataSyncParamContext) {
        return null;
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
        PlaywrightBrowser playwrightBrowser = dataSyncParamContext.getPlaywrightBrowser();
        try (Page page = playwrightBrowser.newPage()) {
            AtomicInteger atomicInteger = new AtomicInteger(-1);
            AtomicReference<String> detail = new AtomicReference<>();
            page.onResponse(response -> {
                boolean graphql = response.url().contains("graphql");
                if (!graphql) {
                    return;
                }
                String postData = response.request().postData();
                if (postData.contains("commentListQuery")) {
                    byte[] body = response.body();
                    Integer commentCountV2 = JacksonUtil.readField(body, "commentCountV2", Integer.class);
                    if (commentCountV2 == null) {
                        return;
                    }
                    atomicInteger.set(commentCountV2);
                }
                if (postData.contains("visionVideoDetail")) {
                    detail.set(response.text());
                }
            });
            Response navigate = page.navigate(shareLink, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED).setTimeout(BrowserConfig.LOAD_PAGE_TIMEOUT));
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
            String workUid = "";
            Date postTime = null;
            String mediaType = "";
            String workType = "";
            String secUid = "";
            if (isVideo) {
                for (int i = 0; i < 60; i++) {
                    int integer = atomicInteger.get();
                    if (integer != -1) {
                        commentNum = integer;
                        break;
                    }
                    page.waitForTimeout(1000);
                }
                InputStream inputStream = new ByteArrayInputStream(navigate.body());
                Document document = JsoupUtil.parse(inputStream);
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
                    }
                }
                if (StringUtils.isBlank(workUid)) {
                    log.error("快手使用api返回视频作品内容");
                    for (int i = 0; i < 60; i++) {
                        String videoDetail = detail.get();
                        if (StringUtils.isNotBlank(videoDetail)) {
                            break;
                        }
                        page.waitForTimeout(1000);
                    }
                    JsonNode detailJsonNode = JacksonUtil.toObj(detail.get());
                    cn.hutool.core.lang.Assert.isFalse(detailJsonNode.has("errors"), "快手限流了");
                    JsonNode videoDetailNode = detailJsonNode.at("/data/visionVideoDetail");
                    JsonNode photoNode = videoDetailNode.get("photo");
                    workUid = photoNode.get("id").asText();
                    photoNode.get("realLikeCount").asInt();
                    BrowserConfig.clearWord(photoNode.get("viewCount").asText());
                    photoNode.get("caption").asText();
                    DateUtil.date(photoNode.get("timestamp").asLong(0));
                    MediaTypeEnum.VIDEO.getType();
                    WorkTypeEnum.NORMAL_VIDEO.getType();
                    nickname = videoDetailNode.at("/author/name").asText();
                    secUid = videoDetailNode.at("/author/id").asText();
                } else {
                    log.error("快手使用SSR返回视频作品内容");
                }
                Assert.hasText(workUid, "快手作品ID空了");
                if (!isScheduler) {
                    String uidUrl = "https://www.kuaishou.com/profile/" + secUid;
                    uid = dataSyncMessageQueue.retryWithExponentialBackoff(() -> {
                        try (Page userPage = playwrightBrowser.getBrowserContext().newPage()) {
                            Response response = userPage.waitForResponse(res -> res.url().contains("rest/v/profile/user") && res.ok(), new Page.WaitForResponseOptions().setTimeout(BrowserConfig.LOAD_PAGE_TIMEOUT),
                                    () -> userPage.navigate(uidUrl, new Page.NavigateOptions().setTimeout(BrowserConfig.LOAD_PAGE_TIMEOUT).setWaitUntil(WaitUntilState.COMMIT)));
                            JsonNode userJson = JacksonUtil.toObj(response.text());
                            return userJson.at("/userProfile/userDefineId").asText();
                        }
                    }, KUAISHOU_RETRY, uidUrl);
                    Assert.hasText(uid, "快手用户uid获取失败");
                }
            } else {
                InputStream inputStream = new ByteArrayInputStream(navigate.body());
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
            title = MessageFormatUtils.cleanDescription(desc);
            topics = MessageFormatUtils.extractHashtagsStr(desc);
            SocialMediaWork socialMediaWork = new SocialMediaWork();
            socialMediaWork.setUrl(redirectUrl);
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
    }

}

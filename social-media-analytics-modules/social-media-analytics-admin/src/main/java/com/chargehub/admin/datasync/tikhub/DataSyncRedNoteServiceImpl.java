package com.chargehub.admin.datasync.tikhub;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
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
import com.chargehub.common.core.utils.ThreadHelper;
import com.chargehub.common.redis.service.RedisService;
import com.chargehub.common.security.utils.DictUtils;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Slf4j
@Service
public class DataSyncRedNoteServiceImpl implements DataSyncService {

    @Autowired
    private HubProperties hubProperties;

    @Autowired
    private RedisService redisService;

    private static final boolean TEST = true;

    private static final boolean TEST_DETAIL = true;

    private static final Integer RED_NOTE_RETRY = 4;

    private static final String RED_NOTE_UID_URL = "https://www.xiaohongshu.com/user/profile/";

    private static final String WORK_SHARE_URL = "/api/v1/xiaohongshu/web_v2/fetch_feed_notes_v2";

    public static final List<String> ONE_NOTE_URLS = Stream.of(
            "/api/v1/xiaohongshu/web/get_note_info_v4",
            "/api/v1/xiaohongshu/web/get_note_info"
    ).collect(Collectors.toList());

    public static final String ONE_NOTE_V7 = "/api/v1/xiaohongshu/web/get_note_info_v7";

    private static final String PROFILE_INFO_URI = "/api/v1/xiaohongshu/web_v2/fetch_user_info_app";

    private static final List<String> USER_INFO_URL = Stream.of(
            "/api/v1/xiaohongshu/web_v2/fetch_user_info_app"
//            , "/api/v1/xiaohongshu/web_v2/fetch_user_info"
    ).collect(Collectors.toList());

    private static final List<String> WORKS_URL = Stream.of(
            "/api/v1/xiaohongshu/web/get_user_notes_v2",
            "/api/v1/xiaohongshu/app/get_user_notes",
            "/api/v1/xiaohongshu/web_v2/fetch_home_notes",
            "/api/v1/xiaohongshu/web_v2/fetch_home_notes_app",
            "/api/v1/xiaohongshu/web/get_user_notes",
            "/api/v1/xiaohongshu/web/get_user_notes_v2"
    ).collect(Collectors.toList());

    @Autowired
    private DataSyncMessageQueue dataSyncMessageQueue;

    @Override
    public SocialMediaPlatformEnum platform() {
        return SocialMediaPlatformEnum.RED_NOTE;
    }

    @Override
    public SocialMediaUserInfo getSocialMediaUserInfo(String secUserId) {
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        JsonNode jsonNode = this.getUserProfile(host, token, secUserId);
        Assert.notNull(jsonNode, "获取用户信息失败");
        int code = jsonNode.path("code").asInt(500);
        Assert.isTrue(code == HttpStatus.HTTP_OK, "获取用户信息失败" + jsonNode);
        JsonNode dataNode = jsonNode.path("/data/result/data");
        String nickname;
        try {
            nickname = dataNode.get("nickname").asText();
        } catch (Exception e) {
            nickname = jsonNode.at("/data/nickname").asText();
        }
        String uniqueId;
        try {
            uniqueId = dataNode.get("red_id").asText();
        } catch (Exception e) {
            uniqueId = jsonNode.at("/data/red_id").asText();
        }
        SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
        socialMediaUserInfo.setNickname(nickname);
        socialMediaUserInfo.setUid(uniqueId);
        return socialMediaUserInfo;
    }

    public JsonNode getUserProfile(String host, String token, String secUserId) {
        AtomicReference<JsonNode> atomicReference = new AtomicReference<>();
        String key = "rednot:user_uri";
        String cacheUri = redisService.getCacheObject(key);
        Map<String, String> params = MapUtil.of("user_id", secUserId);
        if (StringUtils.isNotBlank(cacheUri)) {
            JsonNode request = this.request(host, cacheUri, token, params);
            int code = request.path("code").asInt(500);
            if (code == 200) {
                return request;
            }
        }
        CountDownLatch latch = new CountDownLatch(1);
        for (String uri : USER_INFO_URL) {
            ThreadHelper.execute(() -> {
                JsonNode request = this.request(host, uri, token, params);
                int code = request.path("code").asInt(500);
                if (code == 200) {
                    redisService.setCacheObject(key, uri);
                    atomicReference.set(request);
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return atomicReference.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> SocialMediaWorkDetail<T> getWork(DataSyncParamContext dataSyncParamContext) {
        if (TEST_DETAIL) {
            return this.getWork0(dataSyncParamContext);
        }
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        String shareLink = dataSyncParamContext.getShareLink();
        String redirectUrl = dataSyncParamContext.getRedirectUrl();
        URI uri = URLUtil.toURI(redirectUrl);
        String query = uri.getQuery();
        String noteId;
        String xsecToken;
        if (query.startsWith("redirectPath")) {
            Map<String, String> paramMap = HttpUtil.decodeParamMap(redirectUrl, StandardCharsets.UTF_8);
            URI redirectPath = URLUtil.toURI(paramMap.get("redirectPath"));
            String path = redirectPath.getPath();
            String[] split = path.split("/");
            noteId = split[split.length - 1];
            String redirectPathQuery = redirectPath.getQuery();
            Map<String, String> redirectParam = HttpUtil.decodeParamMap(redirectPathQuery, StandardCharsets.UTF_8);
            xsecToken = redirectParam.get("xsec_token");
        } else {
            Map<String, String> paramMap = HttpUtil.decodeParamMap(query, StandardCharsets.UTF_8);
            String path = uri.getPath();
            String[] split = path.split("/");
            noteId = split[split.length - 1];
            xsecToken = paramMap.get("xsec_token");
        }


        JsonNode jsonNode = this.request(host, ONE_NOTE_V7, token, MapUtil.of("share_text", shareLink));
        int code = jsonNode.path("code").asInt(500);
        if (code != HttpStatus.HTTP_OK) {
            log.error(" {} 获取作品失败: {}", noteId, jsonNode);
            return null;
        }
        JsonNode data = jsonNode.at("/data");
        if (data.isEmpty()) {
            log.error(" {} 获取作品空了: {}", noteId, jsonNode);
            return null;
        }
        JsonNode node = data.get(0);
        JsonNode userNode = node.get("user");
        String nickname = userNode.get("nickname").asText();
        String secUid = userNode.get("userid").asText();
        String redId = userNode.get("red_id").asText();

        JsonNode noteListNode = node.get("note_list");
        if (noteListNode.isEmpty()) {
            log.error(" {} 获取作品空了: {}", noteId, jsonNode);
            return null;
        }
        JsonNode noteNode = noteListNode.get(0);
        String desc = noteNode.get("desc").asText();
        int thumbNum = noteNode.get("liked_count").asInt(0);
        int collectNum = noteNode.get("collected_count").asInt(0);
        int shareNum = noteNode.get("shared_count").asInt(0);
        int commentNum = noteNode.get("comments_count").asInt(0);
        // 基于3.3%互动率估算,目前无法从 view_count获取浏览量
        int playNum = (thumbNum + collectNum + shareNum + commentNum) * 10;

        String customType = "";
        Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
        for (Map.Entry<String, String> entry : socialMediaCustomType.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (desc.contains(k)) {
                customType = v;
            }
        }

        //内容类型 （normal=图文笔记，video=视频笔记）
        String workType = "normal".equals(noteNode.get("type").asText()) ? WorkTypeEnum.RICH_TEXT.getType() : WorkTypeEnum.NORMAL_VIDEO.getType();
        //媒体类型 (2=图片, 4=视频)
        String mediaType = workType.equals(WorkTypeEnum.RICH_TEXT.getType()) ? MediaTypeEnum.PICTURE.getType() : MediaTypeEnum.VIDEO.getType();
        Date postTime = DateUtil.date(noteNode.get("time").asLong(0) * 1000L);
        String shareUrl = "https://www.xiaohongshu.com/explore/" + noteId + "?xsec_token=" + xsecToken;

        SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
        socialMediaUserInfo.setSecUid(secUid);
        socialMediaUserInfo.setNickname(nickname);
        socialMediaUserInfo.setUid(redId);


        SocialMediaWork socialMediaWork = new SocialMediaWork();
        socialMediaWork.setUrl(shareUrl);
        socialMediaWork.setPlatformId(SocialMediaPlatformEnum.RED_NOTE.getDomain());
        socialMediaWork.setDescription(desc);
        socialMediaWork.setWorkUid(noteId);
        socialMediaWork.setPostTime(postTime);
        socialMediaWork.setMediaType(mediaType);
        socialMediaWork.setType(workType);
        socialMediaWork.setThumbNum(thumbNum);
        socialMediaWork.setCollectNum(collectNum);
        socialMediaWork.setShareNum(shareNum);
        socialMediaWork.setCommentNum(commentNum);
        socialMediaWork.setLikeNum(thumbNum);
        socialMediaWork.setPlayNum(playNum);
        socialMediaWork.setCustomType(customType);
        socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
        return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, socialMediaUserInfo);
    }

    @Override
    public <T> SocialMediaWorkDetail<T> fetchWork(DataSyncParamContext dataSyncParamContext) {
        return dataSyncMessageQueue.retryWithExponentialBackoff(() -> this.fetchWork0(dataSyncParamContext), RED_NOTE_RETRY, dataSyncParamContext.getShareLink());
    }

    @SuppressWarnings("unchecked")
    public <T> SocialMediaWorkDetail<T> fetchWork0(DataSyncParamContext dataSyncParamContext) {
        String shareLink = dataSyncParamContext.getShareLink();
        PlaywrightBrowser playwrightBrowser = dataSyncParamContext.getPlaywrightBrowser();
        try (Page page = playwrightBrowser.newPage()) {
            Response navigate = page.navigate(shareLink, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED).setTimeout(BrowserConfig.LOAD_PAGE_TIMEOUT));
            Assert.isTrue(navigate.ok(), "小红书打开链接失败");
            InputStream inputStream = new ByteArrayInputStream(navigate.body());
            String globalJson = JsoupUtil.findContentInScript(inputStream, "window.__INITIAL_STATE__=");
            Assert.hasText(globalJson, "触发小红书限流了,开始重试");
            globalJson = globalJson.replace("undefined", "null");
            JsonNode jsonNode = JacksonUtil.toObj(globalJson).at("/note/noteDetailMap");
            if (jsonNode.isEmpty()) {
                log.error("无法获取小红书作品{}", shareLink);
                return null;
            }
            JsonNode detailNode = jsonNode.get(jsonNode.fieldNames().next());
            JsonNode noteNode = detailNode.get("note");
            if (noteNode.isEmpty()) {
                log.error("小红书笔记不存在{}", shareLink);
                SocialMediaWork socialMediaWork = new SocialMediaWork();
                socialMediaWork.setShareLink(shareLink);
                socialMediaWork.setWorkUid("-1");
                return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, null);
            }
            JsonNode userNode = noteNode.get("user");
            String nickname = userNode.get("nickname").asText();
            String secUid = userNode.get("userId").asText();
            String workUid = noteNode.get("noteId").asText();
            String desc = noteNode.get("desc").asText("");
            String title = noteNode.get("title").asText();
            String topics = MessageFormatUtils.extractHashtagsStr(desc);
            Date postTime = DateUtil.date(noteNode.get("time").asLong(0));
            //内容类型 （normal=图文笔记，video=视频笔记）
            String workType = "normal".equals(noteNode.get("type").asText()) ? WorkTypeEnum.RICH_TEXT.getType() : WorkTypeEnum.NORMAL_VIDEO.getType();
            //媒体类型 (2=图片, 4=视频)
            String mediaType = workType.equals(WorkTypeEnum.RICH_TEXT.getType()) ? MediaTypeEnum.PICTURE.getType() : MediaTypeEnum.VIDEO.getType();
            String shareUrl = "https://www.xiaohongshu.com/explore/" + workUid + "?xsec_token=" + noteNode.get("xsecToken").asText();
            JsonNode interactInfo = noteNode.get("interactInfo");

            int thumbNum = BrowserConfig.clearWord(interactInfo.get("likedCount").asText());
            int collectNum = BrowserConfig.clearWord(interactInfo.get("collectedCount").asText());
            int shareNum = BrowserConfig.clearWord(interactInfo.get("shareCount").asText());
            int commentNum = BrowserConfig.clearWord(interactInfo.get("commentCount").asText());
            // 基于3.3%互动率估算,目前无法从 view_count获取浏览量
            int playNum = (thumbNum + collectNum + shareNum + commentNum) * 10;
            String uid = "";
            if (!dataSyncParamContext.isScheduler()) {
                page.waitForSelector(".icon-btn-wrapper.close-button");
                page.click("div.icon-btn-wrapper.close-button");
                Page popupPage = page.waitForPopup(() -> {
                    ElementHandle element = page.querySelector(".outer-link-container #noteContainer .interaction-container .author-container .author-wrapper .info .username");
                    BoundingBox box = element.boundingBox();
                    double x = box.x + box.width / 2;
                    double y = box.y + box.height / 2;
                    page.mouse().move(x, y);
                    page.mouse().click(x, y);
                });
                popupPage.waitForLoadState();
                // 在弹窗页面执行 JS
                uid = String.valueOf(popupPage.evaluate("__INITIAL_STATE__.user.userPageData._rawValue.basicInfo.redId"));
                Assert.hasText(uid, "小红书号获取失败" + shareLink);
            }
            SocialMediaWork socialMediaWork = new SocialMediaWork();
            socialMediaWork.setUrl(shareUrl);
            socialMediaWork.setPlatformId(SocialMediaPlatformEnum.RED_NOTE.getDomain());
            socialMediaWork.setDescription(desc);
            socialMediaWork.setTitle(StringUtils.isNotBlank(title) ? title : MessageFormatUtils.cleanDescription(desc));
            socialMediaWork.setTopics(topics);
            socialMediaWork.setWorkUid(workUid);
            socialMediaWork.setPostTime(postTime);
            socialMediaWork.setMediaType(mediaType);
            socialMediaWork.setType(workType);
            socialMediaWork.setThumbNum(thumbNum);
            socialMediaWork.setCollectNum(collectNum);
            socialMediaWork.setShareNum(shareNum);
            socialMediaWork.setCommentNum(commentNum);
            socialMediaWork.setLikeNum(thumbNum);
            socialMediaWork.setPlayNum(playNum);
            socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
            SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
            socialMediaUserInfo.setSecUid(secUid);
            socialMediaUserInfo.setNickname(nickname);
            socialMediaUserInfo.setUid(uid);
            return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, socialMediaUserInfo);
        }
    }


    @SuppressWarnings("unchecked")
    public <T> SocialMediaWorkDetail<T> getWork0(DataSyncParamContext dataSyncParamContext) {
        BrowserContext browserContext = dataSyncParamContext.getBrowserContext();
        String shareLink = dataSyncParamContext.getShareLink();
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(browserContext)) {
            Page page = playwrightBrowser.getPage();
            APIResponse apiResponse = page.request().get(shareLink);
            String html = apiResponse.text();
            Document doc = Jsoup.parse(html, "", Parser.htmlParser());
            Element script = doc.select("script").stream().filter(i -> i.html(new StringBuilder()).toString().contains("window.__INITIAL_STATE__="))
                    .findFirst().orElse(null);
            if (script == null) {
                return null;
            }
            String globalJson = script.html().replace("window.__INITIAL_STATE__=", "").replace("undefined", "null");
            JsonNode jsonNode = JacksonUtil.toObj(globalJson).at("/note/noteDetailMap");

            JsonNode detailNode = jsonNode.get(jsonNode.fieldNames().next());
            JsonNode noteNode = detailNode.get("note");
            if (noteNode.isEmpty()) {
                log.error("小红书笔记不存在" + shareLink);
                SocialMediaWork socialMediaWork = new SocialMediaWork();
                socialMediaWork.setShareLink(shareLink);
                socialMediaWork.setWorkUid("-1");
                return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, null);
            }
            JsonNode userNode = noteNode.get("user");
            String nickname = userNode.get("nickname").asText();
            String secUid = userNode.get("userId").asText();
            String workUid = noteNode.get("noteId").asText();
            String desc = noteNode.get("desc").asText("");
            String title = noteNode.get("title").asText();
            String topics = MessageFormatUtils.extractHashtagsStr(desc);
            String customType = "";
            Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
            for (Map.Entry<String, String> entry : socialMediaCustomType.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (desc.contains(k)) {
                    customType = v;
                }
            }
            Date postTime = DateUtil.date(noteNode.get("time").asLong(0));
            //内容类型 （normal=图文笔记，video=视频笔记）
            String workType = "normal".equals(noteNode.get("type").asText()) ? WorkTypeEnum.RICH_TEXT.getType() : WorkTypeEnum.NORMAL_VIDEO.getType();
            //媒体类型 (2=图片, 4=视频)
            String mediaType = workType.equals(WorkTypeEnum.RICH_TEXT.getType()) ? MediaTypeEnum.PICTURE.getType() : MediaTypeEnum.VIDEO.getType();
            String shareUrl = "https://www.xiaohongshu.com/explore/" + workUid + "?xsec_token=" + noteNode.get("xsecToken").asText();
            JsonNode interactInfo = noteNode.get("interactInfo");

            int thumbNum = BrowserConfig.clearWord(interactInfo.get("likedCount").asText());
            int collectNum = BrowserConfig.clearWord(interactInfo.get("collectedCount").asText());
            int shareNum = BrowserConfig.clearWord(interactInfo.get("shareCount").asText());
            int commentNum = BrowserConfig.clearWord(interactInfo.get("commentCount").asText());


            // 基于3.3%互动率估算,目前无法从 view_count获取浏览量
            int playNum = (thumbNum + collectNum + shareNum + commentNum) * 10;

            SocialMediaWork socialMediaWork = new SocialMediaWork();
            socialMediaWork.setUrl(shareUrl);
            socialMediaWork.setPlatformId(SocialMediaPlatformEnum.RED_NOTE.getDomain());
            socialMediaWork.setDescription(desc);
            socialMediaWork.setTitle(StringUtils.isNotBlank(title) ? title : MessageFormatUtils.cleanDescription(desc));
            socialMediaWork.setTopics(topics);
            socialMediaWork.setWorkUid(workUid);
            socialMediaWork.setPostTime(postTime);
            socialMediaWork.setMediaType(mediaType);
            socialMediaWork.setType(workType);
            socialMediaWork.setThumbNum(thumbNum);
            socialMediaWork.setCollectNum(collectNum);
            socialMediaWork.setShareNum(shareNum);
            socialMediaWork.setCommentNum(commentNum);
            socialMediaWork.setLikeNum(thumbNum);
            socialMediaWork.setPlayNum(playNum);
            socialMediaWork.setCustomType(customType);
            socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
            SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
            socialMediaUserInfo.setSecUid(secUid);
            socialMediaUserInfo.setNickname(nickname);
            socialMediaUserInfo.setUid("");
            return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, socialMediaUserInfo);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> SocialMediaWorkResult<T> getWorks(SocialMediaAccountVo socialMediaAccount, String cursor, Integer count) {
        SocialMediaWorkResult<SocialMediaWork> socialMediaWorkResult = new SocialMediaWorkResult<>();
        Map<String, SocialMediaWork> socialMediaWorkMap = new HashMap<>();
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        String secUid = socialMediaAccount.getSecUid();
        JsonNode jsonNode = this.getRedNoteWorks(cursor, host, token, secUid);
        Assert.notNull(jsonNode, "获取作品失败");
        int code = jsonNode.path("code").asInt(500);
        Assert.isTrue(code == HttpStatus.HTTP_OK, "获取作品失败" + jsonNode);
        JsonNode dataNode = jsonNode.get("data");
        boolean hasMore;
        try {
            hasMore = dataNode.get("has_more").asBoolean();
        } catch (Exception e) {
            hasMore = dataNode.at("/data/has_more").asBoolean();
        }

        JsonNode path = dataNode.path("notes");
        if (path instanceof MissingNode) {
            path = dataNode.at("/data/notes");
        }
        String nextCursor;
        if (path.isEmpty()) {
            nextCursor = "-1";
        } else {
            JsonNode lastNode = path.get(path.size() - 1);
            long lastTime = lastNode.get("create_time").asLong() * 1000;
            hasMore = hubProperties.isValidDate(lastTime);
            nextCursor = hasMore ? lastNode.get("cursor").asText() : "-1";
        }
        for (JsonNode node : path) {
            this.buildWork(socialMediaAccount, node, socialMediaWorkMap);
        }

        socialMediaWorkResult.setHasMore(hasMore);
        socialMediaWorkResult.setNextCursor(nextCursor);
        if (MapUtils.isEmpty(socialMediaWorkMap)) {
            return (SocialMediaWorkResult<T>) socialMediaWorkResult;
        }

        List<SocialMediaWork> socialMediaWorks = socialMediaWorkMap.values().stream().map(i -> {
            String md5 = i.generateStatisticMd5();
            i.setStatisticMd5(md5);
            return i;
        }).collect(Collectors.toList());
        socialMediaWorkResult.setWorks(socialMediaWorks);
        return (SocialMediaWorkResult<T>) socialMediaWorkResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> SocialMediaWorkResult<T> getWorks(DataSyncWorksParams params) {
        if (TEST) {
            return getWorksV0(params);
        }
        boolean moreData = true;
        String nextCursor = null;
        SocialMediaWorkResult<SocialMediaWork> socialMediaWorkResult = new SocialMediaWorkResult<>();
        List<SocialMediaWork> socialMediaWorks = new ArrayList<>();
        SocialMediaAccountVo socialMediaAccountVo = new SocialMediaAccountVo();
        socialMediaAccountVo.setSecUid(params.getSecUid());
        Map<String, String> workUids = params.getWorkUids();
        while (moreData) {
            SocialMediaWorkResult<SocialMediaWork> result = this.getWorks(socialMediaAccountVo, nextCursor, null);
            moreData = result.isHasMore();
            nextCursor = result.getNextCursor();
            List<SocialMediaWork> works = result.getWorks();
            for (SocialMediaWork work : works) {
                if (workUids.containsKey(work.getWorkUid())) {
                    socialMediaWorks.add(work);
                }
            }
        }
        socialMediaWorkResult.setWorks(socialMediaWorks);
        return (SocialMediaWorkResult<T>) socialMediaWorkResult;
    }

    @SuppressWarnings("unchecked")
    public <T> SocialMediaWorkResult<T> getWorksV0(DataSyncWorksParams params) {
        Map<String, String> workUids = params.getWorkUids();
        BrowserContext browserContext = params.getBrowserContext();
        SocialMediaWorkResult<SocialMediaWork> socialMediaWorkResult = new SocialMediaWorkResult<>();
        List<SocialMediaWork> socialMediaWorks = new ArrayList<>();
        String storageState = null;
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
        socialMediaWorkResult.setStorageState(storageState);
        socialMediaWorkResult.setWorks(socialMediaWorks);
        return (SocialMediaWorkResult<T>) socialMediaWorkResult;
    }

    private JsonNode getRedNoteWorks(String cursor, String host, String token, String secUid) {
        AtomicReference<JsonNode> atomicReference = new AtomicReference<>();
        String key = "rednot:work_uri";
        String cacheUri = redisService.getCacheObject(key);
        if (StringUtils.isNotBlank(cacheUri)) {
            int i = WORKS_URL.indexOf(cacheUri);
            Map<String, String> params = this.createParams(cursor, secUid, i);
            JsonNode request = this.request(host, cacheUri, token, params);
            int code = request.path("code").asInt(500);
            if (code == 200) {
                return request;
            }
        }
        CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < WORKS_URL.size(); i++) {
            String uri = WORKS_URL.get(i);
            Map<String, String> params = this.createParams(cursor, secUid, i);
            ThreadHelper.execute(() -> {
                JsonNode request = this.request(host, uri, token, params);
                int code = request.path("code").asInt(500);
                if (code == 200) {
                    redisService.setCacheObject(key, uri);
                    atomicReference.set(request);
                    latch.countDown();
                }
            });
        }
        try {
            latch.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return atomicReference.get();
    }

    @NotNull
    private Map<String, String> createParams(String cursor, String secUid, int i) {
        Map<String, String> params = new HashMap<>();
        if (i == 0 || i == 4 || i == 5) {
            params.put("user_id", secUid);
            params.put("lastCursor", cursor);
        } else if (i == 1 || i == 2 || i == 3) {
            params.put("user_id", secUid);
            params.put("cursor", cursor);
        }
        return params;
    }


    private JsonNode request(String host, String uri, String token, Map<String, String> params) {
        try (HttpResponse execute = HttpUtil.createGet(host + uri).bearerAuth(token)
                .formStr(params)
                .execute()) {
            String body = execute.body();
            return JacksonUtil.toObj(body);
        }
    }


    public void buildWork(SocialMediaAccountVo socialMediaAccount, JsonNode node, Map<String, SocialMediaWork> socialMediaWorkMap) {
        String userId = socialMediaAccount.getUserId();
        String accountId = socialMediaAccount.getId();
        String accountType = socialMediaAccount.getType();
        Date postTime = DateUtil.date(node.get("create_time").asLong(0) * 1000L);
        //内容类型 （normal=图文笔记，video=视频笔记）
        String workType = "normal".equals(node.get("type").asText()) ? WorkTypeEnum.RICH_TEXT.getType() : WorkTypeEnum.NORMAL_VIDEO.getType();
        //媒体类型 (2=图片, 4=视频)
        String mediaType = workType.equals(WorkTypeEnum.RICH_TEXT.getType()) ? MediaTypeEnum.PICTURE.getType() : MediaTypeEnum.VIDEO.getType();
        int thumbNum = node.get("likes").asInt(0);
        int collectNum = node.get("collected_count").asInt(0);
        int shareNum = node.get("share_count").asInt(0);
        int commentNum = node.get("comments_count").asInt(0);
        // 基于3.3%互动率估算,目前无法从 view_count获取浏览量
        int playNum = (thumbNum + collectNum + shareNum + commentNum) * 10;
        String desc = node.get("display_title").asText("");
        String workUid = node.get("id").asText("");
        String hashtagName = node.get("desc").asText("");
        String customType = "";
        Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
        for (Map.Entry<String, String> entry : socialMediaCustomType.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (hashtagName.contains(k)) {
                customType = v;
            }
        }
        String shareUrl = "https://www.xiaohongshu.com/discovery/item/" + workUid;
        String platformId = socialMediaAccount.getPlatformId();
        String tenantId = socialMediaAccount.getTenantId();
        SocialMediaWork socialMediaWork = new SocialMediaWork();
        socialMediaWork.setUrl(shareUrl);
        socialMediaWork.setPlatformId(platformId);
        socialMediaWork.setUserId(userId);
        socialMediaWork.setAccountId(accountId);
        socialMediaWork.setTenantId(tenantId);
        socialMediaWork.setDescription(desc);
        socialMediaWork.setWorkUid(workUid);
        socialMediaWork.setPostTime(postTime);
        socialMediaWork.setMediaType(mediaType);
        socialMediaWork.setType(workType);
        socialMediaWork.setThumbNum(thumbNum);
        socialMediaWork.setCollectNum(collectNum);
        socialMediaWork.setShareNum(shareNum);
        socialMediaWork.setCommentNum(commentNum);
        socialMediaWork.setLikeNum(thumbNum);
        socialMediaWork.setPlayNum(playNum);
        socialMediaWork.setAccountType(accountType);
        socialMediaWork.setCustomType(customType);
        socialMediaWorkMap.put(workUid, socialMediaWork);
    }


}

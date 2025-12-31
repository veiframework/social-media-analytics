package com.chargehub.admin.datasync.tikhub;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlPath;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.DataSyncMessageQueue;
import com.chargehub.admin.datasync.DataSyncService;
import com.chargehub.admin.datasync.domain.*;
import com.chargehub.admin.enums.MediaTypeEnum;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.WorkTypeEnum;
import com.chargehub.admin.playwright.BrowserConfig;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.scheduler.DouYinWorkScheduler;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.core.utils.JsoupUtil;
import com.chargehub.common.core.utils.MessageFormatUtils;
import com.chargehub.common.security.utils.DictUtils;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.Proxy;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Slf4j
@Service
public class DataSyncDouYinServiceImpl implements DataSyncService {

    @Autowired
    private HubProperties hubProperties;

    @Autowired
    private DataSyncMessageQueue dataSyncMessageQueue;

    private static final String GET_USER_PROFILE = "/api/v1/douyin/web/handler_user_profile";
    private static final String GET_USER_WORKS = "/api/v1/douyin/app/v3/fetch_user_post_videos";
    public static final String GET_WORK_STATISTIC = "/api/v1/douyin/app/v3/fetch_multi_video_statistics";
    public static final String GET_ONE_WORK_STATISTIC = "/api/v1/douyin/app/v3/fetch_video_statistics";
    private static final String WORK_DETAIL_URL = "/api/v1/douyin/web/fetch_one_video_v2";

    private static final String WORK_DETAIL_SHARE_LINK = "/api/v1/douyin/app/v3/fetch_one_video_by_share_url";

    private static final boolean TIKHUB = true;

    private static final Integer RETRY = 4;

    public static final String DOUYIN_FETCH_WORK_JS = ((Supplier<String>) () -> {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(MessageFormat.format("classpath:{0}", "DouyinFetchWork.js"));
        try {
            return IoUtil.readUtf8(resource.getInputStream());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }).get();

    @Override
    public SocialMediaPlatformEnum platform() {
        return SocialMediaPlatformEnum.DOU_YIN;
    }

    @Override
    public SocialMediaUserInfo getSocialMediaUserInfo(String secUserId) {
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        try (HttpResponse execute = HttpUtil.createGet(host + GET_USER_PROFILE).bearerAuth(token)
                .form("sec_user_id", secUserId)
                .execute()) {
            String body = execute.body();
            JsonNode jsonNode = JacksonUtil.toObj(body);
            int code = jsonNode.path("code").asInt(500);
            Assert.isTrue(code == HttpStatus.HTTP_OK, "获取抖音用户信息失败");
            JsonNode path = jsonNode.at("/data/user");
            String nickname = path.get("nickname").asText();
            String sourceUniqueId = path.get("unique_id").asText("");
            String uniqueId = StringUtils.isBlank(sourceUniqueId) ? path.get("short_id").asText() : sourceUniqueId;
            SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
            socialMediaUserInfo.setNickname(nickname);
            socialMediaUserInfo.setUid(uniqueId);
            return socialMediaUserInfo;
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
        Long realCursor = cursor == null ? 0 : Long.parseLong(cursor);
        try (HttpResponse execute = HttpUtil.createGet(host + GET_USER_WORKS).bearerAuth(token)
                .form("sec_user_id", secUid)
                .form("max_cursor", realCursor)
                .form("count", count)
                .form("filter_type", 0)
                .execute()) {
            String body = execute.body();
            JsonNode jsonNode = JacksonUtil.toObj(body);
            int code = jsonNode.path("code").asInt(500);
            Assert.isTrue(code == HttpStatus.HTTP_OK, "获取作品失败" + body);
            boolean hasMore = jsonNode.at("/data/has_more").asInt() == 1;
            long nextCursor = jsonNode.at("/data/max_cursor").asLong(-1);
            JsonNode path = jsonNode.at("/data/aweme_list");
            if (!path.isEmpty()) {
                JsonNode lastNode = path.get(path.size() - 1);
                long lastTime = lastNode.get("create_time").asLong() * 1000;
                hasMore = hubProperties.isValidDate(lastTime);
                nextCursor = hasMore ? nextCursor : -1;
            }
            for (JsonNode node : path) {
                this.buildWork(socialMediaAccount, node, socialMediaWorkMap);
            }
            socialMediaWorkResult.setHasMore(hasMore);
            socialMediaWorkResult.setNextCursor(nextCursor + "");
            if (MapUtils.isEmpty(socialMediaWorkMap)) {
                return (SocialMediaWorkResult<T>) socialMediaWorkResult;
            }
        }
        String awemeIds = String.join(",", socialMediaWorkMap.keySet());
        dataSyncMessageQueue.syncDouyinExecute(() -> {
            try (HttpResponse multiWorksExecute = HttpUtil.createGet(host + GET_WORK_STATISTIC).bearerAuth(token).form("aweme_ids", awemeIds).execute()) {
                String result = multiWorksExecute.body();
                JsonNode multiWorkNode = JacksonUtil.toObj(result);
                int code = multiWorkNode.path("code").asInt(500);
                Assert.isTrue(code == HttpStatus.HTTP_OK, "获取作品详情失败" + result);
                JsonNode statisticsNode = multiWorkNode.at("/data/statistics_list");
                for (JsonNode node : statisticsNode) {
                    String workUid = node.get("aweme_id").asText("");
                    int playNum = node.at("/play_count").asInt(0);
                    SocialMediaWork socialMediaWork = socialMediaWorkMap.get(workUid);
                    if (socialMediaWork == null) {
                        continue;
                    }
                    socialMediaWork.setPlayNum(playNum);
                }
            }
        });
        List<SocialMediaWork> socialMediaWorks = socialMediaWorkMap.values().stream().map(i -> {
            String md5 = i.generateStatisticMd5();
            i.setStatisticMd5(md5);
            return i;
        }).collect(Collectors.toList());
        socialMediaWorkResult.setWorks(socialMediaWorks);
        return (SocialMediaWorkResult<T>) socialMediaWorkResult;
    }

    @SuppressWarnings("unchecked")
    public <T> SocialMediaWorkDetail<T> getWorkTikhub(DataSyncParamContext dataSyncParamContext) {
        String shareLink = dataSyncParamContext.getShareLink();
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        for (int i = 0; i <= RETRY; i++) {
            try (HttpResponse execute = HttpUtil.createGet(host + WORK_DETAIL_SHARE_LINK).bearerAuth(token)
                    .form("share_url", shareLink).execute()) {
                String body = execute.body();
                JsonNode jsonNode = JacksonUtil.toObj(body);
                int code = jsonNode.path("code").asInt(500);
                Assert.isTrue(code == HttpStatus.HTTP_OK, "获取作品信息失败" + jsonNode);
                JsonNode node = jsonNode.at("/data/aweme_detail");
                if (node == null || node.isEmpty()) {
                    log.error("抖音检测到对方已删除视频作品! {}", dataSyncParamContext.getShareLink());
                    SocialMediaWork socialMediaWork = new SocialMediaWork();
                    socialMediaWork.setShareLink(dataSyncParamContext.getShareLink());
                    socialMediaWork.setWorkUid("-1");
                    return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, null);
                }
                String uniqueId = node.at("/author/unique_id").asText();
                String nickname = node.at("/author/nickname").asText();
                String uid = StringUtils.isBlank(uniqueId) ? node.at("/author/short_id").asText() : uniqueId;
                String workUid = node.get("aweme_id").asText();
                Date postTime = DateUtil.date(node.get("create_time").asLong(0) * 1000L);
                //内容类型 (0=普通视频, 68=图文)
                String workType = node.get("aweme_type").asInt() == 0 ? WorkTypeEnum.NORMAL_VIDEO.getType() : WorkTypeEnum.RICH_TEXT.getType();
                //媒体类型 (2=图片, 4=视频)
                String mediaType = node.get("media_type").asInt() == 4 ? MediaTypeEnum.VIDEO.getType() : MediaTypeEnum.PICTURE.getType();
                int thumbNum = node.at("/statistics/digg_count").asInt(0);
                int collectNum = node.at("/statistics/collect_count").asInt(0);
                int shareNum = node.at("/statistics/share_count").asInt(0);
                int commentNum = node.at("/statistics/comment_count").asInt(0);
                int likeNum = node.at("/statistics/admire_count").asInt(0);
                JsonNode textExtra = node.get("text_extra");
                String customType = "";
                Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
                for (JsonNode textNode : textExtra) {
                    String hashtagName = textNode.at("/hashtag_name").asText("");
                    String type = socialMediaCustomType.get(hashtagName);
                    if (StringUtils.isNotBlank(type)) {
                        customType = type;
                    }
                }

                String desc = node.get("desc").asText("");
                String title = MessageFormatUtils.cleanDescription(desc);
                String topics = MessageFormatUtils.extractHashtagsStr(desc);
                String secUid = node.at("/author/sec_uid").asText();
                String shareUrl = "https://www.douyin.com/user/" + secUid + "?modal_id=" + workUid;
                SocialMediaWork socialMediaWork = new SocialMediaWork();
                socialMediaWork.setUrl(shareUrl);
                socialMediaWork.setPlatformId(this.platform().getDomain());
                socialMediaWork.setDescription(desc);
                socialMediaWork.setWorkUid(workUid);
                socialMediaWork.setPostTime(postTime);
                socialMediaWork.setMediaType(mediaType);
                socialMediaWork.setType(workType);
                socialMediaWork.setThumbNum(thumbNum);
                socialMediaWork.setCollectNum(collectNum);
                socialMediaWork.setShareNum(shareNum);
                socialMediaWork.setCommentNum(commentNum);
                socialMediaWork.setLikeNum(likeNum);
                socialMediaWork.setCustomType(customType);
                socialMediaWork.setTitle(title);
                socialMediaWork.setTopics(topics);
                if (!dataSyncParamContext.isScheduler()) {
                    dataSyncMessageQueue.syncDouyinExecuteSignal(() -> {
                        try (HttpResponse multiWorksExecute = HttpUtil.createGet(host + GET_ONE_WORK_STATISTIC).bearerAuth(token).form("aweme_ids", workUid).execute()) {
                            String result = multiWorksExecute.body();
                            JsonNode multiWorkNode = JacksonUtil.toObj(result);
                            int code0 = multiWorkNode.path("code").asInt(500);
                            if (code0 != HttpStatus.HTTP_OK) {
                                return new DataSyncMessageQueue.AsyncResult(false, result);
                            }
                            JsonNode statisticsNodes = multiWorkNode.at("/data/statistics_list");
                            JsonNode statisticsNode = statisticsNodes.get(0);
                            int playNum = statisticsNode.get("play_count").asInt(0);
                            socialMediaWork.setPlayNum(playNum);
                            return new DataSyncMessageQueue.AsyncResult(true, null);
                        } catch (Exception e) {
                            return new DataSyncMessageQueue.AsyncResult(false, workUid + ": " + e.getMessage());
                        }
                    }, RETRY);
                }
                socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
                SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
                socialMediaUserInfo.setNickname(nickname);
                socialMediaUserInfo.setUid(uid);
                socialMediaUserInfo.setSecUid(secUid);
                return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, socialMediaUserInfo);
            } catch (Exception e) {
                log.error("获取抖音视频详情失败,重试" + i, e);
            }
        }
        log.error("抖音视频详情重试失败" + shareLink);
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> SocialMediaWorkResult<T> getWorks(DataSyncWorksParams params) {
        Map<String, String> workUids = params.getWorkUids();
        String secUid = params.getSecUid();
        SocialMediaAccountVo socialMediaAccountVo = new SocialMediaAccountVo();
        socialMediaAccountVo.setSecUid(secUid);
        SocialMediaWorkResult<SocialMediaWork> socialMediaWorkResult = new SocialMediaWorkResult<>();
        Map<String, SocialMediaWork> socialMediaWorkMap = new HashMap<>();
        BrowserContext browserContext = params.getBrowserContext();
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(browserContext)) {
            Page page = playwrightBrowser.getPage();
            page.route(url -> url.contains(".jpeg") || url.contains(".webp"), Route::abort);
            AtomicBoolean hasMore = new AtomicBoolean(true);
            page.onResponse(res -> {
                if (res.url().contains("/aweme/v1/web/aweme/post/") && res.ok()) {
                    String body = new String(res.body());
                    if (StringUtils.isBlank(body)) {
                        log.error("抖音响应数据空了: " + secUid + "\n" + body);
                        return;
                    }
                    JsonNode jsonNode = JacksonUtil.toObj(body);
                    int more = jsonNode.get("has_more").asInt();
                    hasMore.set(more > 0);
                    JsonNode node = jsonNode.get("aweme_list");
                    if (node == null || node.isEmpty() || node.isNull()) {
                        return;
                    }
                    for (JsonNode item : node) {
                        String workUid = item.get("aweme_id").asText("");
                        if (!workUids.containsKey(workUid)) {
                            continue;
                        }
                        this.buildWork(socialMediaAccountVo, item, socialMediaWorkMap);
                    }
                }
            });
            String enterUrl = "https://www.douyin.com/user/" + secUid;
            page.navigate(enterUrl, new Page.NavigateOptions().setTimeout(60_000));
            if (page.isVisible("text=登陆")) {
                log.error("抖音需要重新登陆! {}", enterUrl);
            }
            for (int i = 0; i < 300; i++) {
                if (!hasMore.get()) {
                    break;
                }
                page.waitForTimeout(400);
                int randomInt = RandomUtil.randomInt(600, 800);
                page.mouse().wheel(0, randomInt);
                try {
                    page.evaluate("const container = document.querySelector(\"div[class*='parent-route-container']\"); container.scrollBy(0," + randomInt + ")");
                } catch (Exception e) {
                    //nothing to do
                }
            }
            List<SocialMediaWork> socialMediaWorks = socialMediaWorkMap.values().stream().map(i -> {
                String md5 = i.generateStatisticMd5();
                i.setStatisticMd5(md5);
                return i;
            }).collect(Collectors.toList());
            socialMediaWorkResult.setWorks(socialMediaWorks);
            socialMediaWorkResult.setStorageState(page.context().storageState());
            return (SocialMediaWorkResult<T>) socialMediaWorkResult;
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> SocialMediaWorkDetail<T> getWork(DataSyncParamContext dataSyncParamContext) {
        BrowserContext browserContext = dataSyncParamContext.getBrowserContext();
        String shareLink = dataSyncParamContext.getShareLink();
        String redirectUrl = dataSyncParamContext.getRedirectUrl();
        boolean isNote;
        if (StringUtils.isBlank(redirectUrl)) {
            isNote = dataSyncParamContext.getMediaType().equals(MediaTypeEnum.PICTURE.getType());
        } else {
            isNote = redirectUrl.contains("note");
        }
        if (isNote) {
            return this.getNoteWork(dataSyncParamContext);
        }
        if (TIKHUB) {
            return this.getWorkTikhub(dataSyncParamContext);
        }
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(browserContext)) {
            Page page = playwrightBrowser.getPage();
            page.route(url -> url.contains(".jpeg") || url.contains(".webp"), Route::abort);
            String body;
            try {
                Response response = page.waitForResponse(res -> res.url().contains("/aweme/detail/"), () -> {
                    page.navigate(shareLink, new Page.NavigateOptions().setTimeout(90_000));
                    if (page.isVisible("text=你要观看的视频不存在")) {
                        throw new IllegalArgumentException("return");
                    }
                });
                body = new String(response.body());
            } catch (Exception e) {
                if ("return".equals(e.getMessage())) {
                    log.error("抖音检测到对方已删除视频作品! {}", dataSyncParamContext.getShareLink());
                    SocialMediaWork socialMediaWork = new SocialMediaWork();
                    socialMediaWork.setShareLink(dataSyncParamContext.getShareLink());
                    socialMediaWork.setWorkUid("-1");
                    return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, null);
                }
                throw e;
            }
            if (StringUtils.isBlank(body)) {
                log.error("抖音获取作品为空" + shareLink);
                return null;
            }
            playwrightBrowser.randomMove();
            JsonNode jsonNode = JacksonUtil.toObj(body);
            JsonNode node = jsonNode.get("aweme_detail");
            if (node.isEmpty()) {
                log.error("抖音获取作品详情空了" + shareLink);
                return null;
            }
            String secUid = node.at("/author/sec_uid").asText();
            String nickname = node.at("/author/nickname").asText();
            String uniqueId = node.at("/author/unique_id").asText();
            String uid = StringUtils.isBlank(uniqueId) ? node.at("/author/short_id").asText() : uniqueId;
            String desc = node.get("desc").asText("");
            String title = MessageFormatUtils.cleanDescription(desc);
            String topics = MessageFormatUtils.extractHashtagsStr(desc);
            String workUid = node.get("aweme_id").asText();
            String shareUrl = "https://www.douyin.com/video/" + workUid;
            Date postTime = DateUtil.date(node.get("create_time").asLong(0) * 1000L);
            //内容类型 (0=普通视频, 68=图文)
            String workType = node.get("aweme_type").asInt() == 0 ? WorkTypeEnum.NORMAL_VIDEO.getType() : WorkTypeEnum.RICH_TEXT.getType();
            //媒体类型 (2=图片, 4=视频)
            String mediaType = node.get("media_type").asInt() == 4 ? MediaTypeEnum.VIDEO.getType() : MediaTypeEnum.PICTURE.getType();
            JsonNode textExtra = node.get("text_extra");
            String customType = "";
            Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
            for (JsonNode textNode : textExtra) {
                String hashtagName = textNode.at("/hashtag_name").asText("");
                String type = socialMediaCustomType.get(hashtagName);
                if (StringUtils.isNotBlank(type)) {
                    customType = type;
                }
            }
            int thumbNum = node.at("/statistics/digg_count").asInt(0);
            int collectNum = node.at("/statistics/collect_count").asInt(0);
            int shareNum = node.at("/statistics/share_count").asInt(0);
            int commentNum = node.at("/statistics/comment_count").asInt(0);
            SocialMediaWork socialMediaWork = new SocialMediaWork();
            socialMediaWork.setUrl(shareUrl);
            socialMediaWork.setPlatformId(SocialMediaPlatformEnum.DOU_YIN.getDomain());
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
            socialMediaWork.setTitle(title);
            socialMediaWork.setTopics(topics);
            socialMediaWork.setCustomType(customType);
            if (!dataSyncParamContext.isScheduler()) {
                log.debug("抖音开始获取播放量");
                HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
                String token = socialMediaDataApi.getToken();
                String host = socialMediaDataApi.getHost();
                dataSyncMessageQueue.syncDouyinExecuteSignal(() -> {
                    try (HttpResponse multiWorksExecute = HttpUtil.createGet(host + GET_ONE_WORK_STATISTIC).timeout(60_000).bearerAuth(token).form("aweme_ids", workUid).execute()) {
                        String result = multiWorksExecute.body();
                        JsonNode multiWorkNode = JacksonUtil.toObj(result);
                        int code0 = multiWorkNode.path("code").asInt(500);
                        if (code0 != HttpStatus.HTTP_OK) {
                            return new DataSyncMessageQueue.AsyncResult(false, result);
                        }
                        JsonNode statisticsNodes = multiWorkNode.at("/data/statistics_list");
                        JsonNode statisticsNode = statisticsNodes.get(0);
                        int playNum = statisticsNode.get("play_count").asInt(0);
                        socialMediaWork.setPlayNum(playNum);
                        return new DataSyncMessageQueue.AsyncResult(true, null);
                    } catch (Exception e) {
                        return new DataSyncMessageQueue.AsyncResult(false, workUid + ": " + e.getMessage());
                    }
                }, RETRY);
            }
            socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
            SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
            socialMediaUserInfo.setNickname(nickname);
            socialMediaUserInfo.setUid(uid);
            socialMediaUserInfo.setSecUid(secUid);
            return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, socialMediaUserInfo);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> SocialMediaWorkResult<T> fetchWorks(DataSyncWorksParams dataSyncWorksParams) {
        Map<String, SocialMediaWork> workMap = dataSyncWorksParams.getWorkMap();
        SocialMediaWorkResult<SocialMediaWork> socialMediaWorkResult = new SocialMediaWorkResult<>();
        Set<String> ids = workMap.keySet();
        Page page = dataSyncWorksParams.getPage();
        List<JsonNode> jsonNodes = PlaywrightBrowser.requests(ids, page, DOUYIN_FETCH_WORK_JS, DouYinWorkScheduler.DOUYIN_USER_PAGE);
        List<SocialMediaWork> socialMediaWorks = new ArrayList<>();
        for (JsonNode jsonNode : jsonNodes) {
            SocialMediaWorkDetail<SocialMediaWork> workDetail = this.buildWorkDetail(jsonNode, null);
            if (workDetail == null) {
                continue;
            }
            SocialMediaWork work = workDetail.getWork();
            String workUid = work.getWorkUid();
            if ("-1".equals(workUid)) {
                SocialMediaWork socialMediaWork = workMap.get(work.getShareLink());
                work.setShareLink(socialMediaWork.getShareLink());
            }
            socialMediaWorks.add(work);
        }
        socialMediaWorkResult.setWorks(socialMediaWorks);
        return (SocialMediaWorkResult<T>) socialMediaWorkResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> SocialMediaWorkDetail<T> fetchWork(DataSyncParamContext dataSyncParamContext) {
        String redirectUrl = dataSyncParamContext.getRedirectUrl();
        Page page = dataSyncParamContext.getPage();
        String workUid;
        if (StringUtils.isBlank(redirectUrl)) {
            workUid = dataSyncParamContext.getWorkUid();
        } else {
            UrlBuilder urlBuilder = UrlBuilder.of(redirectUrl);
            UrlPath urlPath = urlBuilder.getPath();
            workUid = urlPath.getSegment(1);
        }
        JsonNode jsonNode = PlaywrightBrowser.request(workUid, page, DOUYIN_FETCH_WORK_JS, DouYinWorkScheduler.DOUYIN_USER_PAGE);
        SocialMediaWorkDetail<SocialMediaWork> workDetail = this.buildWorkDetail(jsonNode, dataSyncParamContext.getShareLink());
        if (workDetail == null) {
            return null;
        }
        SocialMediaWork socialMediaWork = workDetail.getWork();
        if ("-1".equals(socialMediaWork.getWorkUid())) {
            return (SocialMediaWorkDetail<T>) workDetail;
        }
        log.debug("抖音开始获取播放量");
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        dataSyncMessageQueue.syncDouyinExecuteSignal(() -> {
            try (HttpResponse multiWorksExecute = HttpUtil.createGet(host + GET_ONE_WORK_STATISTIC).timeout(60_000).bearerAuth(token).form("aweme_ids", workUid).execute()) {
                String result = multiWorksExecute.body();
                JsonNode multiWorkNode = JacksonUtil.toObj(result);
                int code0 = multiWorkNode.path("code").asInt(500);
                if (code0 != HttpStatus.HTTP_OK) {
                    return new DataSyncMessageQueue.AsyncResult(false, result);
                }
                JsonNode statisticsNodes = multiWorkNode.at("/data/statistics_list");
                JsonNode statisticsNode = statisticsNodes.get(0);
                int playNum = statisticsNode.get("play_count").asInt(0);
                socialMediaWork.setPlayNum(playNum);
                return new DataSyncMessageQueue.AsyncResult(true, null);
            } catch (Exception e) {
                return new DataSyncMessageQueue.AsyncResult(false, workUid + ": " + e.getMessage());
            }
        }, RETRY);
        socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
        return (SocialMediaWorkDetail<T>) workDetail;
    }

    private SocialMediaWorkDetail<SocialMediaWork> buildWorkDetail(JsonNode jsonNode, String shareLink) {
        if (jsonNode == null) {
            return null;
        }
        JsonNode node = jsonNode.get("aweme_detail");
        if (node == null || node.isEmpty()) {
            SocialMediaWork socialMediaWork = new SocialMediaWork();
            if (StringUtils.isNotBlank(shareLink)) {
                socialMediaWork.setShareLink(shareLink);
            } else {
                String workUid = jsonNode.get("filter_detail").get("aweme_id").asText();
                socialMediaWork.setShareLink(workUid);
            }
            log.error("抖音检测到对方已删除此作品! {}", socialMediaWork.getShareLink());
            socialMediaWork.setWorkUid("-1");
            return new SocialMediaWorkDetail<>(socialMediaWork, null);
        }
        String workUid = node.get("aweme_id").asText();
        String secUid = node.at("/author/sec_uid").asText();
        String nickname = node.at("/author/nickname").asText();
        String uniqueId = node.at("/author/unique_id").asText();
        String uid = StringUtils.isBlank(uniqueId) ? node.at("/author/short_id").asText() : uniqueId;
        String desc = node.get("desc").asText("");
        String title = MessageFormatUtils.cleanDescription(desc);
        String topics = MessageFormatUtils.extractHashtagsStr(desc);
        String shareUrl = "https://www.douyin.com/video/" + workUid;
        Date postTime = DateUtil.date(node.get("create_time").asLong(0) * 1000L);
        //内容类型 (0=普通视频, 68=图文)
        String workType = node.get("aweme_type").asInt() == 0 ? WorkTypeEnum.NORMAL_VIDEO.getType() : WorkTypeEnum.RICH_TEXT.getType();
        //媒体类型 (2=图片, 4=视频)
        String mediaType = node.get("media_type").asInt() == 4 ? MediaTypeEnum.VIDEO.getType() : MediaTypeEnum.PICTURE.getType();
        String customType = "";
        Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
        for (Map.Entry<String, String> entry : socialMediaCustomType.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (desc.contains(k)) {
                customType = v;
            }
        }
        int thumbNum = node.at("/statistics/digg_count").asInt(0);
        int collectNum = node.at("/statistics/collect_count").asInt(0);
        int shareNum = node.at("/statistics/share_count").asInt(0);
        int commentNum = node.at("/statistics/comment_count").asInt(0);
        SocialMediaWork socialMediaWork = new SocialMediaWork();
        socialMediaWork.setUrl(shareUrl);
        socialMediaWork.setPlatformId(SocialMediaPlatformEnum.DOU_YIN.getDomain());
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
        socialMediaWork.setTitle(title);
        socialMediaWork.setTopics(topics);
        socialMediaWork.setCustomType(customType);

        socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
        SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
        socialMediaUserInfo.setNickname(nickname);
        socialMediaUserInfo.setUid(uid);
        socialMediaUserInfo.setSecUid(secUid);
        return new SocialMediaWorkDetail<>(socialMediaWork, socialMediaUserInfo);
    }


    public void buildWork(SocialMediaAccountVo socialMediaAccount, JsonNode node, Map<String, SocialMediaWork> socialMediaWorkMap) {
        String userId = socialMediaAccount.getUserId();
        String accountId = socialMediaAccount.getId();
        String accountType = socialMediaAccount.getType();
        Date postTime = DateUtil.date(node.get("create_time").asLong(0) * 1000L);
        //内容类型 (0=普通视频, 68=图文)
        String workType = node.get("aweme_type").asInt() == 0 ? WorkTypeEnum.NORMAL_VIDEO.getType() : WorkTypeEnum.RICH_TEXT.getType();
        //媒体类型 (2=图片, 4=视频)
        String mediaType = node.get("media_type").asInt() == 4 ? MediaTypeEnum.VIDEO.getType() : MediaTypeEnum.PICTURE.getType();
        int thumbNum = node.at("/statistics/digg_count").asInt(0);
        int collectNum = node.at("/statistics/collect_count").asInt(0);
        int shareNum = node.at("/statistics/share_count").asInt(0);
        int commentNum = node.at("/statistics/comment_count").asInt(0);
        int likeNum = node.at("/statistics/admire_count").asInt(0);
        JsonNode textExtra = node.get("text_extra");
        String customType = "";
        Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
        for (JsonNode jsonNode : textExtra) {
            String hashtagName = jsonNode.at("/hashtag_name").asText("");
            String type = socialMediaCustomType.get(hashtagName);
            if (StringUtils.isNotBlank(type)) {
                customType = type;
            }
        }
        String desc = node.get("desc").asText("");
        String title = MessageFormatUtils.cleanDescription(desc);
        String topics = MessageFormatUtils.extractHashtagsStr(desc);
        String workUid = node.get("aweme_id").asText("");
        String secUid = node.at("/author/sec_uid").asText();
        String shareUrl = "https://www.douyin.com/user/" + secUid + "?modal_id=" + workUid;
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
        socialMediaWork.setLikeNum(likeNum);
        socialMediaWork.setPlayNum(null);
        socialMediaWork.setAccountType(accountType);
        socialMediaWork.setCustomType(customType);
        socialMediaWork.setTitle(title);
        socialMediaWork.setTopics(topics);
        socialMediaWorkMap.put(workUid, socialMediaWork);
    }

    @SuppressWarnings("unchecked")
    public <T> SocialMediaWorkDetail<T> getNoteWork(DataSyncParamContext dataSyncParamContext) {
        boolean isScheduler = dataSyncParamContext.isScheduler();
        String shareLink = dataSyncParamContext.getShareLink();
        String storageState = dataSyncParamContext.getStorageState();
        StringBuilder collect = new StringBuilder();
        if (StringUtils.isNotBlank(storageState)) {
            JsonNode cookieNode = JacksonUtil.toObj(storageState);
            JsonNode cookies = cookieNode.get("cookies");
            cookies.forEach(k -> collect.append(k.get("name").asText()).append("=").append(k.get("value")).append(";"));
        }
        Proxy proxy = dataSyncParamContext.getProxy();
        try (HttpResponse response = HttpUtil.createGet(shareLink)
                .setProxy(proxy)
                .setFollowRedirects(true)
                .headerMap(BrowserConfig.BROWSER_HEADERS, true)
                .cookie(collect.toString())
                .execute()) {
            HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
            String token = socialMediaDataApi.getToken();
            String host = socialMediaDataApi.getHost();
            BrotliCompressorInputStream gzipInputStream = new BrotliCompressorInputStream(response.bodyStream());
            Document document = JsoupUtil.parse(gzipInputStream);
            if (document.text().contains("你要观看的图文不存在")) {
                log.error("抖音检测到对方已删除此作品! {}", dataSyncParamContext.getShareLink());
                SocialMediaWork socialMediaWork = new SocialMediaWork();
                socialMediaWork.setShareLink(dataSyncParamContext.getShareLink());
                socialMediaWork.setWorkUid("-1");
                return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, null);
            }
            List<String> scriptContents = JsoupUtil.findContentsInScript(document, "self.__pace_f.push(");
            if (CollectionUtils.isEmpty(scriptContents)) {
                log.error("抖音笔记数据解析空了:" + shareLink + ", " + document.text());
                return null;
            }
            String lastItem = scriptContents.get(scriptContents.size() - 1);
            String jsonArr = lastItem.substring(0, lastItem.length() - 1);
            JsonNode list = JacksonUtil.toObj(jsonArr);
            String read = list.get(list.size() - 1).asText();
            String[] split = read.split(":", 2);
            String array = split[1];
            JsonNode arrayNode = JacksonUtil.toObj(array);
            JsonNode readNode = arrayNode.get(arrayNode.size() - 1);
            if (!readNode.has("awemeId")) {
                log.warn("抖音笔记异常!" + dataSyncParamContext.getShareLink());
                return null;
            }
            String workUid = readNode.get("awemeId").asText();
            JsonNode detailNode = readNode.at("/aweme/detail");
            JsonNode authorInfo = detailNode.get("authorInfo");
            String nickname = authorInfo.get("nickname").asText();
            String secUid = authorInfo.get("secUid").asText();
            String desc = detailNode.get("desc").asText();
            String title = MessageFormatUtils.cleanDescription(desc);
            String topics = MessageFormatUtils.extractHashtagsStr(desc);
            Date postTime = DateUtil.date(detailNode.get("createTime").asLong(0) * 1000L);
            //内容类型 (0=普通视频, 68=图文)
            String workType = detailNode.get("awemeType").asInt() == 0 ? WorkTypeEnum.NORMAL_VIDEO.getType() : WorkTypeEnum.RICH_TEXT.getType();
            //媒体类型 (2=图片, 4=视频)
            String mediaType = MediaTypeEnum.PICTURE.getType();
            JsonNode textExtra = detailNode.get("textExtra");
            String customType = "";
            Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
            for (JsonNode jsonNode : textExtra) {
                String hashtagName = jsonNode.at("/hashtagName").asText("");
                String type = socialMediaCustomType.get(hashtagName);
                if (StringUtils.isNotBlank(type)) {
                    customType = type;
                }
            }

            int thumbNum = detailNode.at("/stats/diggCount").asInt(0);
            int collectNum = detailNode.at("/stats/collectCount").asInt(0);
            int shareNum = detailNode.at("/stats/shareCount").asInt(0);
            int commentNum = detailNode.at("/stats/commentCount").asInt(0);
            String shareUrl = "https://www.douyin.com/note/" + workUid;
            SocialMediaWork socialMediaWork = new SocialMediaWork();
            socialMediaWork.setUrl(shareUrl);
            socialMediaWork.setPlatformId(SocialMediaPlatformEnum.DOU_YIN.getDomain());
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
            socialMediaWork.setTitle(title);
            socialMediaWork.setTopics(topics);
            socialMediaWork.setCustomType(customType);
            if (!isScheduler) {
                dataSyncMessageQueue.syncDouyinExecuteSignal(() -> {
                    try (HttpResponse multiWorksExecute = HttpUtil.createGet(host + GET_ONE_WORK_STATISTIC).bearerAuth(token).form("aweme_ids", workUid).execute()) {
                        String result = multiWorksExecute.body();
                        JsonNode multiWorkNode = JacksonUtil.toObj(result);
                        int code0 = multiWorkNode.path("code").asInt(500);
                        if (code0 != HttpStatus.HTTP_OK) {
                            return new DataSyncMessageQueue.AsyncResult(false, result);
                        }
                        JsonNode statisticsNodes = multiWorkNode.at("/data/statistics_list");
                        JsonNode statisticsNode = statisticsNodes.get(0);
                        int playNum = statisticsNode.get("play_count").asInt(0);
                        socialMediaWork.setPlayNum(playNum);
                        return new DataSyncMessageQueue.AsyncResult(true, null);
                    } catch (Exception e) {
                        return new DataSyncMessageQueue.AsyncResult(false, workUid + ": " + e.getMessage());
                    }
                }, RETRY);
            }
            socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
            SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
            socialMediaUserInfo.setNickname(nickname);
            socialMediaUserInfo.setSecUid(secUid);
            if (!isScheduler) {
                String uid = dataSyncMessageQueue.retryExecute(() -> {
                    SocialMediaUserInfo result = getSocialMediaUserInfo(secUid);
                    return new DataSyncMessageQueue.AsyncResult(true, result.getUid());
                }, RETRY);
                socialMediaUserInfo.setUid(uid);
            }
            return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, socialMediaUserInfo);
        } catch (IOException ioException) {
            throw new IllegalArgumentException("读取抖音笔记流失败: " + ioException.getMessage());
        }
    }

    public static void main(String[] args) {
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(StringPool.EMPTY)) {
            Page page = DouYinWorkScheduler.navigateToDouYinUserPage(playwrightBrowser);
            page.navigate(DouYinWorkScheduler.DOUYIN_USER_PAGE, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED).setTimeout(120_000));
            Object object = page.evaluate(DOUYIN_FETCH_WORK_JS, "7581819255117647144");
            System.out.println(object);
            ThreadUtil.safeSleep(600_00000);
        }
    }

}

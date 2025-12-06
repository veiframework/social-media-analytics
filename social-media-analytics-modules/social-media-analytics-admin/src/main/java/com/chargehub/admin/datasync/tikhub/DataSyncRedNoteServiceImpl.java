package com.chargehub.admin.datasync.tikhub;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.DataSyncService;
import com.chargehub.admin.datasync.domain.DataSyncParamContext;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import com.chargehub.admin.datasync.domain.SocialMediaWorkDetail;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.MediaTypeEnum;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.WorkTypeEnum;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.core.utils.ThreadHelper;
import com.chargehub.common.redis.service.RedisService;
import com.chargehub.common.security.utils.DictUtils;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
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

    private static final String WORK_SHARE_URL = "/api/v1/xiaohongshu/web_v2/fetch_feed_notes_v2";

    private static final List<String> USER_INFO_URL = Stream.of(
            "/api/v1/xiaohongshu/web_v2/fetch_user_info_app",
            "/api/v1/xiaohongshu/web_v2/fetch_user_info").collect(Collectors.toList());

    private static final List<String> WORKS_URL = Stream.of(
            "/api/v1/xiaohongshu/web/get_user_notes_v2",
            "/api/v1/xiaohongshu/app/get_user_notes",
            "/api/v1/xiaohongshu/web_v2/fetch_home_notes",
            "/api/v1/xiaohongshu/web_v2/fetch_home_notes_app",
            "/api/v1/xiaohongshu/web/get_user_notes",
            "/api/v1/xiaohongshu/web/get_user_notes_v2"
    ).collect(Collectors.toList());

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
        BrowserContext browserContext = dataSyncParamContext.getBrowserContext();
        String shareLink = dataSyncParamContext.getShareLink();
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(browserContext)) {
            Page page = playwrightBrowser.getPage();
            page.navigate(shareLink, new Page.NavigateOptions().setTimeout(60_000));
            if (page.isVisible("text=登录后推荐更懂你的笔记")) {
                log.error("需要重新登录");
                dataSyncParamContext.setStorageState(null);
                return null;
            }
            String string = (String) page.evaluate("JSON.stringify(__INITIAL_STATE__.note.noteDetailMap)");
            JsonNode jsonNode = JacksonUtil.toObj(string);

            JsonNode detailNode = jsonNode.get(jsonNode.fieldNames().next());
            JsonNode noteNode = detailNode.get("note");
            if (noteNode.isEmpty()) {
                log.warn("小红书笔记不存在");
                return null;
            }
            JsonNode userNode = noteNode.get("user");
            String nickname = userNode.get("nickname").asText();
            String secUid = userNode.get("userId").asText();
            String workUid = noteNode.get("noteId").asText();
            String desc = noteNode.get("title").asText();
            String hashtagName = noteNode.get("desc").asText("");
            String customType = "";
            Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
            for (Map.Entry<String, String> entry : socialMediaCustomType.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (hashtagName.contains(k)) {
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
            int thumbNum = this.clearWord(interactInfo.get("likedCount").asText());
            int collectNum = this.clearWord(interactInfo.get("collectedCount").asText());
            int shareNum = this.clearWord(interactInfo.get("shareCount").asText());
            int commentNum = this.clearWord(interactInfo.get("commentCount").asText());
            // 基于3.3%互动率估算,目前无法从 view_count获取浏览量
            int playNum = (thumbNum + collectNum + shareNum + commentNum) * 10;
            String redId = "";
            if (!dataSyncParamContext.isScheduler()) {
                //不是定时任务则需要获取redId
                Page popupPage = page.waitForPopup(() -> {
                    ElementHandle element = page.querySelector(".outer-link-container #noteContainer .interaction-container .author-container .author-wrapper .info .username");
                    BoundingBox box = element.boundingBox();
                    double x = box.x + box.width / 2;
                    double y = box.y + box.height / 2;
                    page.mouse().move(x, y);
                    ThreadUtil.safeSleep(RandomUtil.randomInt(300, 1000));
                    page.mouse().click(x, y);
                });
                popupPage.waitForLoadState();
                // 在弹窗页面执行 JS
                redId = String.valueOf(popupPage.evaluate("__INITIAL_STATE__.user.userPageData._rawValue.basicInfo.redId"));
            } else {
                playwrightBrowser.randomMove();
            }

            SocialMediaWork socialMediaWork = new SocialMediaWork();
            socialMediaWork.setUrl(shareUrl);
            socialMediaWork.setPlatformId(SocialMediaPlatformEnum.RED_NOTE.getDomain());
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
            socialMediaWork.setCustomType(customType);
            socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
            SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
            socialMediaUserInfo.setSecUid(secUid);
            socialMediaUserInfo.setNickname(nickname);
            socialMediaUserInfo.setUid(redId);
            dataSyncParamContext.setStorageState(page.context().storageState());
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
            latch.await();
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

    public Integer clearWord(String text) {
        if (text.contains("万")) {
            String replace = text.replace("万", "");
            return new BigDecimal(replace).multiply(BigDecimal.valueOf(10000)).intValue();
        }
        return Integer.parseInt(text);
    }


}

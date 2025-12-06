package com.chargehub.admin.datasync.tikhub;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.DataSyncMessageQueue;
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
import com.chargehub.common.security.utils.DictUtils;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.BoundingBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static final String GET_WORK_STATISTIC = "/api/v1/douyin/app/v3/fetch_multi_video_statistics";
    private static final String GET_ONE_WORK_STATISTIC = "/api/v1/douyin/app/v3/fetch_video_statistics";
    private static final String WORK_DETAIL_URL = "/api/v1/douyin/web/fetch_one_video_v2";


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
            Assert.isTrue(code == HttpStatus.HTTP_OK, "获取用户信息失败" + body);
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
    @Override
    public <T> SocialMediaWorkDetail<T> getWork(DataSyncParamContext dataSyncParamContext) {
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        BrowserContext browserContext = dataSyncParamContext.getBrowserContext();
        String shareLink = dataSyncParamContext.getShareLink();
        String redirectUrl = dataSyncParamContext.getRedirectUrl();
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(browserContext)) {
            Page page = playwrightBrowser.getPage();
            boolean isNote;
            if (StringUtils.isBlank(redirectUrl)) {
                isNote = shareLink.contains("note");
            } else {
                isNote = redirectUrl.contains("note");
            }
            page.route(url -> url.contains(".jpeg") || url.contains(".webp"), Route::abort);
            if (isNote) {
                navigateToPage(page, shareLink);
                return this.getNoteWork(playwrightBrowser, dataSyncParamContext);
            }
            Response response = page.waitForResponse(res -> res.url().contains("/aweme/detail/"), new Page.WaitForResponseOptions().setTimeout(60_000), () -> page.navigate(shareLink, new Page.NavigateOptions().setTimeout(60_000)));
            playwrightBrowser.randomMove();
            String body = new String(response.body());
            if (StringUtils.isBlank(body)) {
                log.error("抖音获取作品为空");
                return null;
            }
            JsonNode jsonNode = JacksonUtil.toObj(body);
            JsonNode node = jsonNode.get("aweme_detail");
            if (node.isEmpty()) {
                log.warn("抖音获取作品不存在了" + body);
                return null;
            }
            String secUid = node.at("/author/sec_uid").asText();
            String nickname = node.at("/author/nickname").asText();
            String uniqueId = node.at("/author/unique_id").asText();
            String uid = StringUtils.isBlank(uniqueId) ? node.at("/author/short_id").asText() : uniqueId;
            String desc = node.get("desc").asText("");
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
            socialMediaWork.setCustomType(customType);
            log.debug("抖音开始获取播放量");
            dataSyncMessageQueue.syncDouyinExecute(() -> {
                try (HttpResponse multiWorksExecute = HttpUtil.createGet(host + GET_ONE_WORK_STATISTIC).bearerAuth(token).form("aweme_ids", workUid).execute()) {
                    String result = multiWorksExecute.body();
                    JsonNode multiWorkNode = JacksonUtil.toObj(result);
                    int code0 = multiWorkNode.path("code").asInt(500);
                    Assert.isTrue(code0 == HttpStatus.HTTP_OK, "获取作品详情失败" + result);
                    JsonNode statisticsNodes = multiWorkNode.at("/data/statistics_list");
                    JsonNode statisticsNode = statisticsNodes.get(0);
                    int playNum = statisticsNode.get("play_count").asInt(0);
                    socialMediaWork.setPlayNum(playNum);
                }
            });
            if (socialMediaWork.getPlayNum() == null) {
                return null;
            }
            socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
            SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
            socialMediaUserInfo.setNickname(nickname);
            socialMediaUserInfo.setUid(uid);
            socialMediaUserInfo.setSecUid(secUid);
            dataSyncParamContext.setStorageState(page.context().storageState());
            return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, socialMediaUserInfo);
        }
    }

    private static void navigateToPage(Page page, String shareLink) {
        //关闭登录弹窗
        page.navigate(shareLink, new Page.NavigateOptions().setTimeout(60_000));
        page.waitForSelector("input[placeholder='请输入手机号']");
        ElementHandle element = page.querySelector("article[id='douyin_login_comp_flat_panel'] svg[xmlns='http://www.w3.org/2000/svg']");
        BoundingBox box = element.boundingBox();
        double x = box.x + box.width / 2;
        double y = box.y + box.height / 2;
        page.mouse().move(x, y);
        page.mouse().click(x, y);
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
        socialMediaWork.setPlayNum(0);
        socialMediaWork.setAccountType(accountType);
        socialMediaWork.setCustomType(customType);
        socialMediaWorkMap.put(workUid, socialMediaWork);
    }

    @SuppressWarnings("unchecked")
    public <T> SocialMediaWorkDetail<T> getNoteWork(PlaywrightBrowser playwrightBrowser, DataSyncParamContext dataSyncParamContext) {
        boolean isScheduler = dataSyncParamContext.isScheduler();
        Page page = playwrightBrowser.getPage();
        if (page.isVisible("text='你要观看的图文不存在'")) {
            log.warn("抖音检测到对方已删除此作品!");
            return null;
        }
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        List<Object> list = (List<Object>) page.evaluate("__pace_f[__pace_f.length-1]");
        Assert.notEmpty(list, "获取抖音作品笔记失败");
        String read = (String) list.get(list.size() - 1);
        String[] split = read.split(":", 2);
        String array = split[1];
        JsonNode arrayNode = JacksonUtil.toObj(array);
        JsonNode readNode = arrayNode.get(arrayNode.size() - 1);
        String workUid = readNode.get("awemeId").asText();
        JsonNode detailNode = readNode.at("/aweme/detail");
        JsonNode authorInfo = detailNode.get("authorInfo");
        String nickname = authorInfo.get("nickname").asText();
        String secUid = authorInfo.get("secUid").asText();
        String uid = "";
        if (!isScheduler) {
            Page popupPage = page.waitForPopup(() -> {
                ElementHandle element = page.querySelector("div[data-e2e='user-info'] a[href*='www.douyin.com/user/']");
                BoundingBox box = element.boundingBox();
                double x = box.x + box.width / 2;
                double y = box.y + box.height / 2;
                page.mouse().move(x, y);
//                ThreadUtil.safeSleep(RandomUtil.randomInt(300, 1000));
                page.mouse().click(x, y);
            });
            popupPage.waitForSelector("input[placeholder='请输入手机号']");
            //关闭登录弹窗
            popupPage.click("article[id='douyin_login_comp_flat_panel'] svg[xmlns='http://www.w3.org/2000/svg']");
            String fullText = popupPage.textContent(":text-matches('抖音号：.*')");
            uid = fullText.replace("抖音号：", "");
        } else {
            playwrightBrowser.randomMove();
        }
        String desc = detailNode.get("desc").asText();
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
        socialMediaWork.setCustomType(customType);
        dataSyncMessageQueue.syncDouyinExecute(() -> {
            try (HttpResponse multiWorksExecute = HttpUtil.createGet(host + GET_ONE_WORK_STATISTIC).bearerAuth(token).form("aweme_ids", workUid).execute()) {
                String result = multiWorksExecute.body();
                JsonNode multiWorkNode = JacksonUtil.toObj(result);
                int code0 = multiWorkNode.path("code").asInt(500);
                Assert.isTrue(code0 == HttpStatus.HTTP_OK, "获取作品详情失败" + result);
                JsonNode statisticsNodes = multiWorkNode.at("/data/statistics_list");
                JsonNode statisticsNode = statisticsNodes.get(0);
                int playNum = statisticsNode.get("play_count").asInt(0);
                socialMediaWork.setPlayNum(playNum);
            }
        });
        if (socialMediaWork.getPlayNum() == null) {
            return null;
        }
        socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
        SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
        socialMediaUserInfo.setNickname(nickname);
        socialMediaUserInfo.setUid(uid);
        socialMediaUserInfo.setSecUid(secUid);
        return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, socialMediaUserInfo);
    }

}

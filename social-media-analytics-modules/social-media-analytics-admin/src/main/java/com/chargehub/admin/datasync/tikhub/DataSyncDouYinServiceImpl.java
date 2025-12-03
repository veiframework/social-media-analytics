package com.chargehub.admin.datasync.tikhub;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.DataSyncMessageQueue;
import com.chargehub.admin.datasync.DataSyncService;
import com.chargehub.admin.datasync.domain.DataSyncParamContext;
import com.chargehub.admin.datasync.domain.SocialMediaDetail;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
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
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
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

    @Override
    public SocialMediaDetail getSecUidByWorkUrl(String url) {
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(null)) {
            playwrightBrowser.init();
            Page page = playwrightBrowser.newPage();
            Response response = page.waitForResponse(res -> res.url().contains("/aweme/detail/"), new Page.WaitForResponseOptions().setTimeout(60_000), () -> {
                page.navigate(url, new Page.NavigateOptions().setTimeout(60_000));
            });
            Assert.isTrue(response.ok(), "获取作品失败!");
            String body = new String(response.body());
            JsonNode jsonNode = JacksonUtil.toObj(body);
            SocialMediaDetail socialMediaDetail = new SocialMediaDetail();
            String secUid = jsonNode.at("/aweme_detail/author/sec_uid").asText();
            String uid = jsonNode.at("/aweme_detail/author/unique_id").asText();
            String nickname = jsonNode.at("/aweme_detail/author/nickname").asText();
            String workUid = jsonNode.at("/aweme_detail/aweme_id").asText();
            socialMediaDetail.setSecUid(secUid);
            socialMediaDetail.setWorkUid(workUid);
            socialMediaDetail.setNickname(nickname);
            socialMediaDetail.setPlatformId(this.platform().getDomain());
            socialMediaDetail.setUid(uid);
            return socialMediaDetail;
        }
    }

    @Override
    public <T> T getWork(String workUid) {
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        try (HttpResponse execute = HttpUtil.createGet(host + WORK_DETAIL_URL).bearerAuth(token).form("aweme_id", workUid).execute()) {
            String body = execute.body();
            JsonNode jsonNode = JacksonUtil.toObj(body);
            int code = jsonNode.path("code").asInt(500);
            Assert.isTrue(code == HttpStatus.HTTP_OK, "获取作品信息失败" + jsonNode);
            JsonNode node = jsonNode.at("/data/aweme_detail");
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
            dataSyncMessageQueue.syncDouyinExecute(() -> {
                try (HttpResponse multiWorksExecute = HttpUtil.createGet(host + GET_WORK_STATISTIC).bearerAuth(token).form("aweme_ids", workUid).execute()) {
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
            socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
            return (T) socialMediaWork;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getWork(DataSyncParamContext context) {
        String workUid = context.getWorkUid();
        String storageState = context.getStorageState();
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(storageState)) {
            playwrightBrowser.init();
            Page page = playwrightBrowser.newPage();
            Response response = page.waitForResponse(res -> res.url().contains("/creator/item/mget"), new Page.WaitForResponseOptions().setTimeout(60_000), () -> {
                page.navigate("https://creator.douyin.com/creator-micro/work-management/work-detail/" + workUid, new Page.NavigateOptions().setTimeout(60_000));
                boolean visible = page.isVisible("text=登录");
                if (!visible) {
                    //清空登录状态
                    context.setStorageState(null);
                    throw new IllegalArgumentException("需要重新登录");
                }
            });
            Assert.isTrue(response.ok(), "获取作品失败!");
            String body = new String(response.body());
            JsonNode jsonNode = JacksonUtil.toObj(body);
            JsonNode items = jsonNode.get("items");
            if (items.isEmpty()) {
                return null;
            }
            JsonNode node = items.get(0);
            String desc = node.get("description").asText();
            String customType = "";
            Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
            for (Map.Entry<String, String> entry : socialMediaCustomType.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (desc.contains(k)) {
                    customType = v;
                }
            }

            //内容类型 （1=图文笔记，2=视频笔记）
            String workType = 1 == (node.get("type").asInt()) ? WorkTypeEnum.RICH_TEXT.getType() : WorkTypeEnum.NORMAL_VIDEO.getType();
            String mediaType = workType.equals(WorkTypeEnum.RICH_TEXT.getType()) ? MediaTypeEnum.PICTURE.getType() : MediaTypeEnum.VIDEO.getType();
            Date postTime = DateUtil.date(node.get("create_time").asLong(0) * 1000L);
            int thumbNum = node.at("/metrics/like_count").asInt(0);
            int collectNum = node.at("/metrics/favorite_count").asInt(0);
            int shareNum = node.at("/metrics/share_count").asInt(0);
            int commentNum = node.at("/metrics/comment_count").asInt(0);
            int playNum = node.at("/metrics/view_count").asInt(0);
            String shareUrl = "https://www.douyin.com/video/" + workUid;
            SocialMediaWork socialMediaWork = new SocialMediaWork();
            socialMediaWork.setUrl(shareUrl);
            socialMediaWork.setPlatformId(this.platform().getDomain());
            socialMediaWork.setDescription(desc);
            socialMediaWork.setWorkUid(workUid);
            socialMediaWork.setPostTime(postTime);
            socialMediaWork.setThumbNum(thumbNum);
            socialMediaWork.setCollectNum(collectNum);
            socialMediaWork.setShareNum(shareNum);
            socialMediaWork.setCommentNum(commentNum);
            socialMediaWork.setPlayNum(playNum);
            socialMediaWork.setCustomType(customType);
            socialMediaWork.setType(workType);
            socialMediaWork.setMediaType(mediaType);
            socialMediaWork.setLikeNum(0);
            socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
            return (T) socialMediaWork;
        } catch (Exception e) {
            log.error("获取作品失败", e);
            return null;
        }
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


}

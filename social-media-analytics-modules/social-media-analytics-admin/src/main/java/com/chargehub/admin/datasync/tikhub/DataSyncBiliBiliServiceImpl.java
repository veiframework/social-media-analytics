package com.chargehub.admin.datasync.tikhub;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.DataSyncMessageQueue;
import com.chargehub.admin.datasync.DataSyncService;
import com.chargehub.admin.datasync.domain.SocialMediaDetail;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.MediaTypeEnum;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.WorkTypeEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.security.utils.DictUtils;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Service
public class DataSyncBiliBiliServiceImpl implements DataSyncService {


    @Autowired
    private HubProperties hubProperties;

    @Autowired
    private DataSyncMessageQueue dataSyncMessageQueue;

    private static final String GET_USER_PROFILE = "/api/v1/bilibili/web/fetch_user_profile";
    private static final String GET_USER_WORKS = "/api/v1/bilibili/web/fetch_user_post_videos";
    private static final String GET_WORKS_DETAIL = "/api/v1/bilibili/web/fetch_video_detail";
    private static final String ONE_VIDEO = "/api/v1/bilibili/web/fetch_one_video";

    @Override
    public SocialMediaPlatformEnum platform() {
        return SocialMediaPlatformEnum.BILI_BILI;
    }

    @Override
    public SocialMediaUserInfo getSocialMediaUserInfo(String secUserId) {
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        try (HttpResponse execute = HttpUtil.createGet(host + GET_USER_PROFILE).bearerAuth(token)
                .form("uid", secUserId)
                .execute()) {
            String body = execute.body();
            JsonNode jsonNode = JacksonUtil.toObj(body);
            int code = jsonNode.path("code").asInt(500);
            Assert.isTrue(code == HttpStatus.HTTP_OK, "获取用户信息失败" + body);
            JsonNode path = jsonNode.at("/data/data");
            String nickname = path.get("name").asText();
            SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
            socialMediaUserInfo.setNickname(nickname);
            socialMediaUserInfo.setUid(secUserId);
            return socialMediaUserInfo;
        }
    }

    @Override
    public SocialMediaDetail getSecUidByWorkUrl(String url) {
        SocialMediaDetail socialMediaDetail = new SocialMediaDetail();
        String location;
        try (HttpResponse execute = HttpUtil.createGet(url).execute()) {
            location = execute.header("Location");
        }
        if (StringUtils.isBlank(location)) {
            return null;
        }
        URI uri = URLUtil.toURI(location);
        String query = "?" + uri.getQuery();
        String path = uri.getPath();
        Map<String, String> paramMap = HttpUtil.decodeParamMap(query, StandardCharsets.UTF_8);
        String[] split = path.split("/");
        String workUid = split[split.length - 1];
        socialMediaDetail.setSecUid(paramMap.get("up_id"));
        socialMediaDetail.setUid(paramMap.get("up_id"));
        socialMediaDetail.setWorkUid(workUid);
        socialMediaDetail.setPlatformId(this.platform().getDomain());
        return socialMediaDetail;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getWork(String workUid) {
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        try (HttpResponse execute = HttpUtil.createGet(host + ONE_VIDEO).bearerAuth(token).form("bv_id", workUid).execute()) {
            String body = execute.body();
            JsonNode jsonNode = JacksonUtil.toObj(body);
            int code = jsonNode.path("code").asInt(500);
            Assert.isTrue(code == HttpStatus.HTTP_OK, "获取作品信息失败" + jsonNode);
            JsonNode dataNode = jsonNode.at("/data/data");
            JsonNode statNode = jsonNode.at("/data/data/stat");
            //媒体类型 (2=图片, 4=视频)
            String workType = WorkTypeEnum.NORMAL_VIDEO.getType();
            String mediaType = MediaTypeEnum.VIDEO.getType();
            int shareNum = statNode.get("share").asInt(0);
            int thumbNum = statNode.get("like").asInt(0);
            int collectNum = statNode.get("favorite").asInt(0);
            int commentNum = statNode.get("reply").asInt(0);
            int playNum = statNode.get("view").asInt(0);
            String desc = dataNode.get("title").asText("");
            Date postTime = DateUtil.date(dataNode.get("pubdate").asLong(0) * 1000L);
            String customType = "";
            Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
            for (Map.Entry<String, String> entry : socialMediaCustomType.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (desc.contains(k)) {
                    customType = v;
                }
            }
            String shareUrl = "https://www.bilibili.com/video/" + workUid;
            SocialMediaWork socialMediaWork = new SocialMediaWork();
            socialMediaWork.setUrl(shareUrl);
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
            socialMediaWork.setPlatformId(this.platform().getDomain());
            socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
            return (T) socialMediaWork;
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
        long realCursor = cursor == null ? 1 : Long.parseLong(cursor);
        try (HttpResponse execute = HttpUtil.createGet(host + GET_USER_WORKS).bearerAuth(token)
                .form("uid", secUid)
                .form("pn", realCursor)
                .form("order", "pubdate")
                .execute()) {
            String body = execute.body();
            JsonNode jsonNode = JacksonUtil.toObj(body);
            int code = jsonNode.path("code").asInt(500);
            Assert.isTrue(code == HttpStatus.HTTP_OK, "获取作品失败" + body);
            JsonNode path = jsonNode.at("/data/data/list/vlist");
            boolean hasMore = !path.isEmpty();
            long nextCursor = realCursor + 1;
            if (!path.isEmpty()) {
                JsonNode lastNode = path.get(path.size() - 1);
                long lastTime = lastNode.get("created").asLong() * 1000;
                hasMore = hubProperties.isValidDate(lastTime);
                nextCursor = hasMore ? nextCursor : -1;
            }
            for (JsonNode node : path) {
                this.dataSyncMessageQueue.syncBiliBiliExecute(() -> this.buildWork(socialMediaAccount, node, socialMediaWorkMap));
            }
            socialMediaWorkResult.setHasMore(hasMore);
            socialMediaWorkResult.setNextCursor(nextCursor + "");
            if (MapUtils.isEmpty(socialMediaWorkMap)) {
                return (SocialMediaWorkResult<T>) socialMediaWorkResult;
            }
        }
        List<SocialMediaWork> socialMediaWorks = socialMediaWorkMap.values().stream().map(i -> {
            String md5 = i.generateStatisticMd5();
            i.setStatisticMd5(md5);
            return i;
        }).collect(Collectors.toList());
        socialMediaWorkResult.setWorks(socialMediaWorks);
        return (SocialMediaWorkResult<T>) socialMediaWorkResult;
    }

    public void buildWork(SocialMediaAccountVo socialMediaAccount, JsonNode node, Map<String, SocialMediaWork> socialMediaWorkMap) {
        String userId = socialMediaAccount.getUserId();
        String accountId = socialMediaAccount.getId();
        String accountType = socialMediaAccount.getType();
        Date postTime;
        //媒体类型 (2=图片, 4=视频)
        String workType = WorkTypeEnum.NORMAL_VIDEO.getType();
        String mediaType = MediaTypeEnum.VIDEO.getType();
        int thumbNum;
        int collectNum;
        int shareNum;
        int commentNum = node.get("comment").asInt(0);
        int playNum = node.get("play").asInt(0);
        String workUid = node.get("bvid").asText();
        String desc = node.get("title").asText("");
        long aid = node.get("aid").asLong();
        String customType = "";
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        try (HttpResponse multiWorksExecute = HttpUtil.createGet(host + GET_WORKS_DETAIL).bearerAuth(token).form("aid", aid).execute()) {
            String body = multiWorksExecute.body();
            JsonNode jsonNode = JacksonUtil.toObj(body);
            int code = jsonNode.path("code").asInt(500);
            Assert.isTrue(code == HttpStatus.HTTP_OK, "获取作品详情失败" + body);
            JsonNode dataNode = jsonNode.at("/data/data");
            Set<String> tags = new HashSet<>();
            for (JsonNode tagsNode : dataNode.get("Tags")) {
                tags.add(tagsNode.get("tag_name").asText());
            }
            Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
            for (Map.Entry<String, String> entry : socialMediaCustomType.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (tags.contains(k)) {
                    customType = v;
                }
            }
            JsonNode statNode = dataNode.at("/View/stat");
            shareNum = statNode.get("share").asInt(0);
            thumbNum = statNode.get("like").asInt(0);
            collectNum = statNode.get("favorite").asInt(0);
            postTime = DateUtil.date(dataNode.at("/View/pubdate").asLong(0) * 1000L);
        }
        String shareUrl = "https://www.bilibili.com/video/" + workUid;
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

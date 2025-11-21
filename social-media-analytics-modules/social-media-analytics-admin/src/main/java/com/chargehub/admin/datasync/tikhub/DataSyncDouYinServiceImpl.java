package com.chargehub.admin.datasync.tikhub;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.DataSyncService;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.MediaTypeEnum;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.WorkTypeEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.collections.MapUtils;
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
@Service
public class DataSyncDouYinServiceImpl implements DataSyncService {

    @Autowired
    private HubProperties hubProperties;


    private static final String GET_USER_PROFILE = "/api/v1/douyin/web/handler_user_profile";
    private static final String GET_USER_WORKS = "/api/v1/douyin/app/v3/fetch_user_post_videos";
    private static final String GET_WORK_STATISTIC = "/api/v1/douyin/app/v3/fetch_multi_video_statistics";


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
            String uniqueId = path.get("unique_id").asText();
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
            Long nextCursor = jsonNode.at("/data/max_cursor").asLong(-1);
            JsonNode path = jsonNode.at("/data/aweme_list");
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
        try (HttpResponse multiWorksExecute = HttpUtil.createGet(host + GET_WORK_STATISTIC).bearerAuth(token).form("aweme_ids", awemeIds).execute()) {
            String result = multiWorksExecute.body();
            JsonNode multiWorkNode = JacksonUtil.toObj(result);
            int code = multiWorkNode.path("code").asInt(500);
            Assert.isTrue(code == HttpStatus.HTTP_OK, "获取作品详情失败" + result);
            JsonNode statisticsNode = multiWorkNode.at("/data/statistics_list");
            for (JsonNode node : statisticsNode) {
                String workUid = node.get("aweme_id").asText("");
                int playNum = node.get("play_count").asInt(0);
                SocialMediaWork socialMediaWork = socialMediaWorkMap.get(workUid);
                if (socialMediaWork == null) {
                    continue;
                }
                socialMediaWork.setPlayNum(playNum);
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
        Date postTime = DateUtil.date(node.get("create_time").asLong(0) * 1000L);
        String shareUrl = node.at("/share_info/share_url").asText("");
        //内容类型 (0=普通视频, 68=图文)
        String workType = node.get("aweme_type").asInt() == 0 ? WorkTypeEnum.NORMAL_VIDEO.getType() : WorkTypeEnum.RICH_TEXT.getType();
        //媒体类型 (2=图片, 4=视频)
        String mediaType = node.get("media_type").asInt() == 4 ? MediaTypeEnum.VIDEO.getType() : MediaTypeEnum.PICTURE.getType();
        int thumbNum = node.at("/statistics/digg_count").asInt(0);
        int collectNum = node.at("/statistics/collect_count").asInt(0);
        int shareNum = node.at("/statistics/share_count").asInt(0);
        int commentNum = node.at("/statistics/comment_count").asInt(0);
        int likeNum = node.at("/statistics/admire_count").asInt(0);
        String desc = node.get("desc").asText("");
        String workUid = node.get("aweme_id").asText("");
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
        socialMediaWorkMap.put(workUid, socialMediaWork);
    }


}

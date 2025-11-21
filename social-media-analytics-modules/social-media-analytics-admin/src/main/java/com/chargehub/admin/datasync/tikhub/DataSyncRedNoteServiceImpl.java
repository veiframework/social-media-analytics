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
public class DataSyncRedNoteServiceImpl implements DataSyncService {

    @Autowired
    private HubProperties hubProperties;

    private static final String GET_USER_PROFILE = "/api/v1/xiaohongshu/web_v2/fetch_user_info_app";
    private static final String GET_USER_WORKS = "/api/v1/xiaohongshu/app/get_user_notes";


    @Override
    public SocialMediaPlatformEnum platform() {
        return SocialMediaPlatformEnum.RED_NOTE;
    }

    @Override
    public SocialMediaUserInfo getSocialMediaUserInfo(String secUserId) {
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        try (HttpResponse execute = HttpUtil.createGet(host + GET_USER_PROFILE).bearerAuth(token)
                .form("user_id", secUserId)
                .execute()) {
            String body = execute.body();
            JsonNode jsonNode = JacksonUtil.toObj(body);
            int code = jsonNode.path("code").asInt(500);
            Assert.isTrue(code == HttpStatus.HTTP_OK, "获取用户信息失败" + body);
            String nickname = jsonNode.at("/data/data/nickname").asText();
            String uniqueId = jsonNode.at("/data/data/red_id").asText();
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
        try (HttpResponse execute = HttpUtil.createGet(host + GET_USER_WORKS).bearerAuth(token)
                .form("user_id", secUid)
                .form("cursor", cursor)
                .execute()) {
            String body = execute.body();
            JsonNode jsonNode = JacksonUtil.toObj(body);
            int code = jsonNode.path("code").asInt(500);
            Assert.isTrue(code == HttpStatus.HTTP_OK, "获取作品失败" + body);
            boolean hasMore = jsonNode.at("/data/data/has_more").asBoolean();
            JsonNode path = jsonNode.at("/data/data/notes");
            String nextCursor = path.isEmpty() ? "-1" : path.get(path.size() - 1).get("cursor").asText();
            for (JsonNode node : path) {
                this.buildWork(socialMediaAccount, node, socialMediaWorkMap);
            }

            socialMediaWorkResult.setHasMore(hasMore);
            socialMediaWorkResult.setNextCursor(nextCursor);
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
        Date postTime = DateUtil.date(node.get("create_time").asLong(0) * 1000L);
        String shareUrl = "";
        //内容类型 （normal=图文笔记，video=视频笔记）
        String workType = node.get("type").asText().equals("normal") ? WorkTypeEnum.RICH_TEXT.getType() : WorkTypeEnum.NORMAL_VIDEO.getType();
        //媒体类型 (2=图片, 4=视频)
        String mediaType = workType.equals(WorkTypeEnum.RICH_TEXT.getType()) ? MediaTypeEnum.PICTURE.getType() : MediaTypeEnum.VIDEO.getType();
        int thumbNum = node.get("likes").asInt(0);
        int collectNum = node.get("collected_count").asInt(0);
        int shareNum = node.get("share_count").asInt(0);
        int commentNum = node.get("comments_count").asInt(0);
        // 基于3.3%互动率估算,目前无法从 view_count获取浏览量
        int playNum = (thumbNum + collectNum + shareNum + commentNum) * 30;
        String desc = node.get("display_title").asText("");
        String workUid = node.get("id").asText("");
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
        socialMediaWorkMap.put(workUid, socialMediaWork);
    }


}

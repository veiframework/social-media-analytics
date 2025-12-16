package com.chargehub.admin.datasync.tikhub;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.DataSyncService;
import com.chargehub.admin.datasync.domain.*;
import com.chargehub.admin.enums.MediaTypeEnum;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.WorkTypeEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.core.utils.MessageFormatUtils;
import com.chargehub.common.security.utils.DictUtils;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@Service
public class DataSyncWechatVideoServiceImpl implements DataSyncService {

    @Autowired
    private HubProperties hubProperties;

    private static final String GET_USER_PROFILE = "/api/v1/wechat_channels/fetch_user_search";

    private static final String GET_USER_WORKS = "/api/v1/wechat_channels/fetch_home_page";

    private static final String ONE_VIDEO = "/api/v1/wechat_channels/fetch_video_detail";

    @Override
    public SocialMediaPlatformEnum platform() {
        return SocialMediaPlatformEnum.WECHAT_VIDEO;
    }

    @Override
    public SocialMediaUserInfo getSocialMediaUserInfo(String secUserId) {
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        try (HttpResponse execute = HttpUtil.createGet(host + GET_USER_PROFILE).bearerAuth(token)
                .form("keywords", secUserId)
                .form("page", 1)
                .execute()) {
            String body = execute.body();
            JsonNode jsonNode = JacksonUtil.toObj(body);
            int code = jsonNode.path("code").asInt(500);
            Assert.isTrue(code == HttpStatus.HTTP_OK, "获取用户信息失败" + body);
            JsonNode dataNode = jsonNode.get("data");
            JsonNode targetNode = null;
            for (JsonNode node : dataNode) {
                JsonNode contactNode = node.get("contact");
                String text = contactNode.get("nickname").asText();
                if (secUserId.equals(text)) {
                    targetNode = contactNode;
                    break;
                }
            }
            Assert.notNull(targetNode, "获取用户信息失败" + body);
            String uniqueId = targetNode.get("username").asText();
            SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
            socialMediaUserInfo.setNickname(secUserId);
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
        ThreadUtil.safeSleep(RandomUtil.randomInt(200, 500));
        try (HttpResponse execute = HttpUtil.createGet(host + GET_USER_WORKS).timeout(60_000).bearerAuth(token)
                .form("username", secUid)
                .form("last_buffer", cursor)
                .execute()) {
            String body = execute.body();
            JsonNode jsonNode = JacksonUtil.toObj(body);
            int code = jsonNode.path("code").asInt(500);
            JsonNode path = jsonNode.at("/data/object");
            boolean hasMore = !path.isEmpty();
            String nextCursor = code == 500 ? "-1" : jsonNode.at("/data/last_buffer").asText();
            if (!path.isEmpty()) {
                JsonNode lastNode = path.get(path.size() - 1);
                long lastTime = lastNode.get("createtime").asLong() * 1000;
                hasMore = hubProperties.isValidDate(lastTime);
                nextCursor = hasMore ? nextCursor : "-1";
            }
            socialMediaWorkResult.setHasMore(hasMore);
            socialMediaWorkResult.setNextCursor(nextCursor);
            if (hasMore) {
                for (JsonNode node : path) {
                    this.buildWork(socialMediaAccount, node, socialMediaWorkMap);
                }
            }
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
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> SocialMediaWorkResult<T> getWorks(DataSyncWorksParams params) {
        boolean moreData = true;
        String nextCursor = null;
        SocialMediaWorkResult<SocialMediaWork> socialMediaWorkResult = new SocialMediaWorkResult<>();
        List<SocialMediaWork> socialMediaWorks = new ArrayList<>();
        SocialMediaAccountVo socialMediaAccountVo = new SocialMediaAccountVo();
        socialMediaAccountVo.setSecUid(params.getSecUid());
        while (moreData) {
            SocialMediaWorkResult<SocialMediaWork> result = this.getWorks(socialMediaAccountVo, nextCursor, null);
            moreData = result.isHasMore();
            nextCursor = result.getNextCursor();
            List<SocialMediaWork> works = result.getWorks();
            socialMediaWorks.addAll(works);
        }
        socialMediaWorkResult.setWorks(socialMediaWorks);
        return (SocialMediaWorkResult<T>) socialMediaWorkResult;
    }

    @Override
    public <T> SocialMediaWorkDetail<T> getWork(DataSyncParamContext dataSyncParamContext) {
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        String exportId = dataSyncParamContext.getShareLink();
        try (HttpResponse execute = HttpUtil.createGet(host + ONE_VIDEO).bearerAuth(token).form("exportId", exportId).execute()) {
            String body = execute.body();
            JsonNode jsonNode = JacksonUtil.toObj(body);
            int code = jsonNode.path("code").asInt(500);
            if (code != HttpStatus.HTTP_OK) {
                log.error("获取作品失败: {}", jsonNode);
                return null;
            }
            JsonNode node = jsonNode.at("/data");
            String nickname = node.get("nickname").asText();
            String username = node.get("username").asText();
            Date postTime = DateUtil.date(node.get("createtime").asLong(0) * 1000L);
            //媒体类型 (2=图片, 4=视频)
            JsonNode objectDescNode = node.at("/object_desc");
            String workType = 4 == objectDescNode.get("media_type").asInt() ? WorkTypeEnum.NORMAL_VIDEO.getType() : WorkTypeEnum.RICH_TEXT.getType();
            String mediaType = workType.equals(WorkTypeEnum.RICH_TEXT.getType()) ? MediaTypeEnum.PICTURE.getType() : MediaTypeEnum.VIDEO.getType();
            int thumbNum = node.get("fav_count").asInt(0);
            int collectNum = node.get("like_count").asInt(0);
            int shareNum = node.get("forward_count").asInt(0);
            int commentNum = node.get("comment_count").asInt(0);
            // 基于3.3%互动率估算,目前无法从 view_count获取浏览量
            int playNum = (thumbNum + collectNum + shareNum + commentNum) * 10;
            String desc = objectDescNode.get("description").asText("");
            String customType = "";
            Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
            for (Map.Entry<String, String> entry : socialMediaCustomType.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (desc.contains(k)) {
                    customType = v;
                }
            }
            JsonNode mediaNode = objectDescNode.get("media");
            String shareUrl = mediaNode.get(0).get("thumb_url").asText();
            SocialMediaWork socialMediaWork = new SocialMediaWork();
            socialMediaWork.setUrl(shareUrl);
            socialMediaWork.setPlatformId(platform().getDomain());
            socialMediaWork.setDescription(desc);
            socialMediaWork.setWorkUid(exportId);
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
            socialMediaUserInfo.setSecUid(username);
            socialMediaUserInfo.setUid(username);
            socialMediaUserInfo.setNickname(nickname);
            return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, socialMediaUserInfo);
        }
    }

    public void buildWork(SocialMediaAccountVo socialMediaAccount, JsonNode node, Map<String, SocialMediaWork> socialMediaWorkMap) {
        String userId = socialMediaAccount.getUserId();
        String accountId = socialMediaAccount.getId();
        String accountType = socialMediaAccount.getType();
        Date postTime = DateUtil.date(node.get("createtime").asLong(0) * 1000L);
        //媒体类型 (2=图片, 4=视频)
        JsonNode objectDescNode = node.at("/object_desc");
        String workType = 4 == objectDescNode.get("media_type").asInt() ? WorkTypeEnum.NORMAL_VIDEO.getType() : WorkTypeEnum.RICH_TEXT.getType();
        String mediaType = workType.equals(WorkTypeEnum.RICH_TEXT.getType()) ? MediaTypeEnum.PICTURE.getType() : MediaTypeEnum.VIDEO.getType();
        int thumbNum = node.get("fav_count").asInt(0);
        int collectNum = node.get("like_count").asInt(0);
        int shareNum = node.get("forward_count").asInt(0);
        int commentNum = node.get("comment_count").asInt(0);
        // 基于3.3%互动率估算,目前无法从 view_count获取浏览量
        int playNum = (thumbNum + collectNum + shareNum + commentNum) * 10;
        String desc = objectDescNode.get("description").asText("");
        String title = MessageFormatUtils.cleanDescription(desc);
        String topics = MessageFormatUtils.extractHashtagsStr(desc);
        String workUid = node.at("/object_extend/export_id").asText("");
        String customType = "";
        Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
        for (Map.Entry<String, String> entry : socialMediaCustomType.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (desc.contains(k)) {
                customType = v;
            }
        }
        JsonNode mediaNode = objectDescNode.get("media");
        String shareUrl = mediaNode.get(0).get("thumb_url").asText();
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
        socialMediaWork.setTitle(title);
        socialMediaWork.setTopics(topics);
        socialMediaWorkMap.put(workUid, socialMediaWork);
    }


}

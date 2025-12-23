package com.chargehub.admin.datasync.tikhub;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.DataSyncService;
import com.chargehub.admin.datasync.domain.*;
import com.chargehub.admin.enums.MediaTypeEnum;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.WorkTypeEnum;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.common.core.utils.MessageFormatUtils;
import com.chargehub.common.security.utils.DictUtils;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@Service
public class KuaiShouSyncServiceImpl implements DataSyncService {


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
            storageState = dataSyncParamContext.getStorageState();
            socialMediaWorks.add(workDetail.getWork());
        }
        socialMediaWorkResult.setStorageState(storageState);
        socialMediaWorkResult.setWorks(socialMediaWorks);
        return (SocialMediaWorkResult<T>) socialMediaWorkResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> SocialMediaWorkDetail<T> getWork(DataSyncParamContext dataSyncParamContext) {
        boolean isScheduler = dataSyncParamContext.isScheduler();
        BrowserContext browserContext = dataSyncParamContext.getBrowserContext();
        String shareLink = dataSyncParamContext.getShareLink();
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(browserContext)) {
            Page page = playwrightBrowser.getPage();
            AtomicInteger atomicInteger = new AtomicInteger(-1);
            page.onResponse(response -> {
                boolean graphql = response.url().contains("graphql");
                if (!graphql) {
                    return;
                }
                String postData = response.request().postData();
                if (!postData.contains("commentListQuery")) {
                    return;
                }
                byte[] body = response.body();
                Integer commentCountV2 = JacksonUtil.readField(body, "commentCountV2", Integer.class);
                if (commentCountV2 == null) {
                    return;
                }
                atomicInteger.set(commentCountV2);
            });
            page.navigate(shareLink, new Page.NavigateOptions().setTimeout(90_000));
            String currentUrl = page.url();
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
            String customType;
            String workUid = "";
            Date postTime = null;
            String mediaType = "";
            String workType = "";
            String secUid = "";
            if (currentUrl.contains("short-video")) {
                if (page.isVisible("text=立即登录")) {
                    log.error("快手需要重新登陆");
                    dataSyncParamContext.setStorageState(null);
                    return null;
                }
                if (page.isVisible("text=作品已失效")) {
                    log.error("快手检测到对方已删除此作品! {}", dataSyncParamContext.getShareLink());
                    SocialMediaWork socialMediaWork = new SocialMediaWork();
                    socialMediaWork.setShareLink(dataSyncParamContext.getShareLink());
                    socialMediaWork.setWorkUid("-1");
                    return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, null);
                }
                for (int i = 0; i < 60; i++) {
                    int integer = atomicInteger.get();
                    if (integer != -1) {
                        commentNum = integer;
                        break;
                    }
                    page.waitForTimeout(1000);
                }
                page.waitForFunction("typeof __APOLLO_STATE__ !== 'undefined'", null, new Page.WaitForFunctionOptions().setTimeout(30_000L));
                String json = (String) page.evaluate("JSON.stringify(__APOLLO_STATE__)");
                Assert.hasText(json, "快手获取作品失败" + shareLink);
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
                        playNum = this.clearWord(value.get("viewCount").asText());
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
                        if (!isScheduler) {
                            Page popupPage = page.waitForPopup(() -> {
                                ElementHandle element = page.querySelector("span[class*='profile-user-name-title']");
                                BoundingBox box = element.boundingBox();
                                double x = box.x + box.width / 2;
                                double y = box.y + box.height / 2;
                                page.mouse().move(x, y);
                                page.mouse().click(x, y);
                            });
                            String fullText = popupPage.textContent(":text-matches(' 快手号：.*')");
                            uid = fullText.replace(" 快手号：", "");
                        }
                    }
                }
            } else {
                if (page.isVisible("text=作品不存在，可能已经被删除。")) {
                    log.error("快手检测到对方已删除此作品! {}", dataSyncParamContext.getShareLink());
                    SocialMediaWork socialMediaWork = new SocialMediaWork();
                    socialMediaWork.setShareLink(dataSyncParamContext.getShareLink());
                    socialMediaWork.setWorkUid("-1");
                    return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, null);
                }
                page.waitForFunction("typeof INIT_STATE !== 'undefined'", null, new Page.WaitForFunctionOptions().setTimeout(30_000L));
                String json = (String) page.evaluate("JSON.stringify(INIT_STATE)");
                Assert.hasText(json, "快手获取作品失败" + shareLink);
                JsonNode obj = JacksonUtil.toObj(json);
                JsonNode jsonNode = null;
                int idx = 0;
                for (JsonNode node : obj) {
                    if (idx == 2) {
                        jsonNode = node;
                        break;
                    }
                    idx++;
                }
                Assert.notNull(jsonNode, "快手获取作品失败" + shareLink);
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
            customType = "";
            Map<String, String> socialMediaCustomType = DictUtils.getDictLabelMap("social_media_custom_type");
            for (Map.Entry<String, String> entry : socialMediaCustomType.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (desc.contains(k)) {
                    customType = v;
                }
            }
            SocialMediaWork socialMediaWork = new SocialMediaWork();
            socialMediaWork.setUrl(currentUrl);
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
            socialMediaWork.setCustomType(customType);
            socialMediaWork.setStatisticMd5(socialMediaWork.generateStatisticMd5());
            SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
            socialMediaUserInfo.setSecUid(secUid);
            socialMediaUserInfo.setNickname(nickname);
            socialMediaUserInfo.setUid(uid);
            dataSyncParamContext.setStorageState(page.context().storageState());
            return (SocialMediaWorkDetail<T>) new SocialMediaWorkDetail<>(socialMediaWork, socialMediaUserInfo);
        }
    }

    public Integer clearWord(String text) {
        if (text.contains("万")) {
            String replace = text.replace("万", "");
            return new BigDecimal(replace).multiply(BigDecimal.valueOf(10000)).intValue();
        }
        return Integer.parseInt(text);
    }


}

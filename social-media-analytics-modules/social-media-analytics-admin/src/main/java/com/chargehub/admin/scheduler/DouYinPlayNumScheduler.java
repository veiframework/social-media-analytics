package com.chargehub.admin.scheduler;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.chargehub.admin.datasync.DataSyncMessageQueue;
import com.chargehub.admin.datasync.tikhub.DataSyncDouYinServiceImpl;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.domain.SocialMediaWorkCreateTaskEvent;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.redis.service.RedisService;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Component
public class DouYinPlayNumScheduler {

    @Autowired
    private DataSyncMessageQueue dataSyncMessageQueue;


    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Autowired
    private HubProperties hubProperties;

    @Autowired
    private RedisService redisService;

    private static final String DOUYIN_PLAY_NUM_TASK = "douyin_play_num_task";

    public void execute() {
        List<SocialMediaWork> latestWork = socialMediaWorkService.getDouYinLatestWork();
        if (CollectionUtils.isEmpty(latestWork)) {
            return;
        }
        Set<String> workIds = redisService.getCacheSet(DOUYIN_PLAY_NUM_TASK);
        List<SocialMediaWork> workTasks = socialMediaWorkService.getWorkByIds(workIds);
        if (CollectionUtils.isNotEmpty(workTasks)) {
            latestWork.addAll(workTasks);
        }
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi().get("tikhub");
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        List<List<SocialMediaWork>> partition = Lists.partition(latestWork, 50);
        for (List<SocialMediaWork> works : partition) {
            boolean less = works.size() < 50;
            String url = less ? DataSyncDouYinServiceImpl.GET_ONE_WORK_STATISTIC : DataSyncDouYinServiceImpl.GET_WORK_STATISTIC;
            List<List<SocialMediaWork>> miniPartition = Lists.partition(works, less ? 2 : 50);
            for (List<SocialMediaWork> socialMediaWorks : miniPartition) {
                this.execute(socialMediaWorks, host, token, url);
            }
        }
    }

    public void execute(List<SocialMediaWork> works, String host, String token, String url) {
        if (CollectionUtils.isEmpty(works)) {
            return;
        }
        Map<String, SocialMediaWork> collect = works.stream().collect(Collectors.toMap(SocialMediaWork::getWorkUid, Function.identity()));
        Set<String> awemeIds = collect.keySet();
        Set<String> ids = works.stream().map(SocialMediaWork::getId).collect(Collectors.toSet());
        dataSyncMessageQueue.syncDouyinExecuteSignal(() -> {
            List<SocialMediaWork> updateWorks = new ArrayList<>();
            try (HttpResponse multiWorksExecute = HttpUtil.createGet(host + url)
                    .timeout(60_000).bearerAuth(token).form("aweme_ids", awemeIds).execute()) {
                String result = multiWorksExecute.body();
                JsonNode multiWorkNode = JacksonUtil.toObj(result);
                int code = multiWorkNode.path("code").asInt(500);
                if (code != HttpStatus.HTTP_OK) {
                    return new DataSyncMessageQueue.AsyncResult(false, result);
                }
                JsonNode statisticsNode = multiWorkNode.at("/data/statistics_list");
                for (JsonNode node : statisticsNode) {
                    String workUid = node.get("aweme_id").asText("");
                    int playNum = node.at("/play_count").asInt();
                    SocialMediaWork socialMediaWork = collect.get(workUid);
                    SocialMediaWork newWork = new SocialMediaWork();
                    newWork.setPlayNum(playNum);
                    SocialMediaWork update = new SocialMediaWork();
                    update.setId(socialMediaWork.getId());
                    socialMediaWork.computePlayNum(newWork, update);
                    if (update.getPlayNum() != null) {
                        updateWorks.add(update);
                    }
                }
                socialMediaWorkService.updateBatchById(updateWorks);
                redisService.deleteCacheSet(DOUYIN_PLAY_NUM_TASK, ids);
                return new DataSyncMessageQueue.AsyncResult(true, null);
            } catch (Exception e) {
                return new DataSyncMessageQueue.AsyncResult(false, String.join(",", awemeIds) + ": " + e.getMessage());
            }
        }, 5);

    }

    @EventListener
    public void listenCreateWorkTask(SocialMediaWorkCreateTaskEvent event) {
        Collection<SocialMediaWork> data = event.getData();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        String platformId = SocialMediaPlatformEnum.DOU_YIN.getDomain();
        Set<String> ids = data.stream().filter(i -> i.getPlatformId().equals(platformId)).map(SocialMediaWork::getId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        redisService.setCacheSet(DOUYIN_PLAY_NUM_TASK, ids);
    }

}

package com.chargehub.admin.scheduler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.admin.account.dto.SocialMediaAccountQueryDto;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.DataSyncMessageQueue;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.SyncWorkStatusEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.common.redis.service.RedisService;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Slf4j
@Component("dataSyncWorkScheduler")
public class DataSyncWorkScheduler {

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Autowired
    private DataSyncManager dataSyncManager;

    @Autowired
    private RedisService redisService;

    @Autowired
    private DataSyncMessageQueue dataSyncMessageQueue;

    private static final ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(10);


    public void asyncExecute(Set<String> accountId) {
        dataSyncMessageQueue.execute(() -> this.execute(accountId));
    }

    public void execute() {
        this.execute(null);
    }

    @SuppressWarnings("unchecked")
    public void execute(Set<String> accountId) {
        StopWatch stopWatch = new StopWatch("作品同步任务");
        try {
            stopWatch.start();
            log.info("作品同步任务开始 {}", DateUtil.now());
            String tag = "";
            if (CollectionUtils.isNotEmpty(accountId)) {
                tag = ":" + String.join("", accountId);
            }
            redisService.lock("lock:sync-work" + tag, lock -> {
                if (BooleanUtils.isFalse(lock)) {
                    return null;
                }
                List<CompletableFuture<Void>> allFutures = new ArrayList<>();
                boolean hasMore = true;
                long pageNum = 1;
                while (hasMore) {
                    SocialMediaAccountQueryDto socialMediaAccountQueryDto = new SocialMediaAccountQueryDto(pageNum, 10L, false);
                    socialMediaAccountQueryDto.setSyncWorkStatus(Sets.newHashSet(SyncWorkStatusEnum.WAIT.ordinal(), SyncWorkStatusEnum.COMPLETE.ordinal(), SyncWorkStatusEnum.ERROR.ordinal()));
                    if (CollectionUtils.isNotEmpty(accountId)) {
                        socialMediaAccountQueryDto.setId(accountId);
                    }
                    IPage<SocialMediaAccountVo> page = (IPage<SocialMediaAccountVo>) socialMediaAccountService.getPage(socialMediaAccountQueryDto);
                    List<SocialMediaAccountVo> records = page.getRecords();
                    hasMore = CollectionUtils.isNotEmpty(records);
                    pageNum++;
                    for (SocialMediaAccountVo socialMediaAccountVo : records) {
                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> this.fetchWorks(socialMediaAccountVo), FIXED_THREAD_POOL);
                        allFutures.add(future);
                    }
                }
                // 等待所有任务完成
                if (!allFutures.isEmpty()) {
                    //无限期等待, 可通过CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).get(30, TimeUnit.MINUTES); 设置超时时间
                    CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();
                }
                return null;
            });
        } finally {
            stopWatch.stop();
            log.info("作品同步任务结束 {}秒", stopWatch.getTotalTimeSeconds());
        }
    }

    private void fetchWorks(SocialMediaAccountVo socialMediaAccountVo) {
        String accountId = socialMediaAccountVo.getId();
        try {
            this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.SYNCING);
            boolean moreData = true;
            String nextCursor = null;
            while (moreData) {
                SocialMediaWorkResult<SocialMediaWork> result = this.dataSyncManager.getWorks(socialMediaAccountVo, nextCursor, 10);
                moreData = result.isHasMore();
                nextCursor = result.getNextCursor();
                List<SocialMediaWork> works = result.getWorks();
                List<String> workUidList = works.stream().map(SocialMediaWork::getWorkUid).collect(Collectors.toList());
                Map<String, SocialMediaWork> existMap = this.socialMediaWorkService.getByWorkUidList(workUidList);
                List<SocialMediaWork> updateList = new ArrayList<>();
                for (SocialMediaWork work : works) {
                    SocialMediaWork existWork = existMap.get(work.getWorkUid());
                    if (existWork == null) {
                        updateList.add(work);
                    } else {
                        SocialMediaWork updateWork = existWork.computeMd5(work);
                        if (updateWork != null) {
                            updateList.add(updateWork);
                        }
                    }
                }
                this.socialMediaWorkService.saveOrUpdateBatch(updateList);
            }
        } catch (Exception e) {
            log.error("作品同步任务异常", e);
            this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.ERROR);
            return;
        }
        this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.COMPLETE);

    }


}

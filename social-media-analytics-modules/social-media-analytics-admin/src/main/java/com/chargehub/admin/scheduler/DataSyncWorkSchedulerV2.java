package com.chargehub.admin.scheduler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.DataSyncMessageQueue;
import com.chargehub.admin.enums.SyncWorkStatusEnum;
import com.chargehub.admin.playwright.PlaywrightCrawlHelper;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.dto.SocialMediaWorkQueryDto;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
import com.chargehub.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@Component("dataSyncWorkSchedulerV2")
public class DataSyncWorkSchedulerV2 {


    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Autowired
    private DataSyncManager dataSyncManager;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PlaywrightCrawlHelper playwrightCrawlHelper;


    private static final ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(10);


    public void asyncExecute(Set<String> accountId) {
        FIXED_THREAD_POOL.execute(() -> this.execute(accountId));
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
                SocialMediaWorkQueryDto queryDto = new SocialMediaWorkQueryDto();
                queryDto.setAccountId(accountId);
                List<SocialMediaWorkVo> page = (List<SocialMediaWorkVo>) socialMediaWorkService.getAll(queryDto);
                List<CompletableFuture<Void>> allFutures = new ArrayList<>();
                for (SocialMediaWorkVo vo : page) {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> this.fetchWorks(vo), DataSyncMessageQueue.FIXED_THREAD_POOL);
                    allFutures.add(future);
                }
                // 等待所有任务完成
                if (allFutures.isEmpty()) {
                    return null;
                }
                try {
                    CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).get(1, TimeUnit.HOURS);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    log.error("作品同步任务异常", e);
                }
                return null;
            });
        } finally {
            stopWatch.stop();
            log.info("作品同步任务结束 {}秒", stopWatch.getTotalTimeSeconds());
        }
    }

    private void fetchWorks(SocialMediaWorkVo vo) {
        String accountId = vo.getAccountId();
        String workUid = vo.getWorkUid();
        String platformId = vo.getPlatformId();
        try {
            this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.SYNCING);
            SocialMediaWork work;
            String loginState = this.playwrightCrawlHelper.getLoginState(accountId);
            if (StringUtils.isBlank(loginState)) {
                work = this.dataSyncManager.getWork(platformId, workUid);
            } else {
                work = this.dataSyncManager.getWork(accountId, workUid, platformId);
            }
            List<String> workUidList = Stream.of(workUid).collect(Collectors.toList());
            Map<String, SocialMediaWork> existMap = this.socialMediaWorkService.getByWorkUidList(workUidList);
            List<SocialMediaWork> updateList = new ArrayList<>();
            SocialMediaWork existWork = existMap.get(workUid);
            if (existWork == null) {
                return;
            }
            SocialMediaWork updateWork = existWork.computeMd5(work);
            if (updateWork != null) {
                updateList.add(updateWork);
            }
            this.socialMediaWorkService.saveOrUpdateBatch(updateList);
        } catch (Exception e) {
            log.error("作品同步任务异常", e);
            this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.ERROR);
            return;
        }
        this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.COMPLETE);

    }

}

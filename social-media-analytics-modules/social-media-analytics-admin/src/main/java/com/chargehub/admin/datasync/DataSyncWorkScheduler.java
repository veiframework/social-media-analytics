package com.chargehub.admin.datasync;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.admin.account.dto.SocialMediaAccountQueryDto;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.service.SocialMediaWorkService;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private static final ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(10);


    public void asyncExecute(String accountId) {
        ThreadUtil.execute(() -> this.execute(accountId));
    }

    @SuppressWarnings("unchecked")
    public void execute(String accountId) {
        log.info("作品同步任务开始 {}", DateUtil.now());
        redisService.lock("lock:sync-work", lock -> {
            if (BooleanUtils.isFalse(lock)) {
                return null;
            }
            List<CompletableFuture<Void>> allFutures = new ArrayList<>();
            boolean hasMore = true;
            long pageNum = 1;
            while (hasMore) {
                SocialMediaAccountQueryDto socialMediaAccountQueryDto = new SocialMediaAccountQueryDto(pageNum, 10L, false);
                if (StringUtils.isNotBlank(accountId)) {
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
        }, 30);
        log.info("作品同步任务结束 {}", DateUtil.now());
    }

    private void fetchWorks(SocialMediaAccountVo socialMediaAccountVo) {
        try {

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
        }
    }


}

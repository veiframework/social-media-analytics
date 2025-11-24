package com.chargehub.admin.datasync;

import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Slf4j
@Component
public class DataSyncMessageQueue {

    private static final ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(10);


    private static final ExecutorService FIXED_GET_USER_INFO_POOL = Executors.newFixedThreadPool(10);

    public void execute(Runnable runnable) {
        FIXED_THREAD_POOL.execute(runnable);
    }

    public SocialMediaUserInfo syncExecute(Supplier<SocialMediaUserInfo> runnable) {
        CompletableFuture<SocialMediaUserInfo> future = CompletableFuture.supplyAsync(() -> {
            try {
                return runnable.get();
            } catch (Exception e) {
                log.error("执行异步任务失败: ", e);
                return null;
            }
        }, FIXED_GET_USER_INFO_POOL);
        return future.join();
    }

}

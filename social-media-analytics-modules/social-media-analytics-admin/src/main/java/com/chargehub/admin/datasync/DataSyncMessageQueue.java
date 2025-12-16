package com.chargehub.admin.datasync;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
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

    public static final ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(10);


    private static final ExecutorService FIXED_GET_USER_INFO_POOL = Executors.newFixedThreadPool(5);

    private static final ExecutorService FIXED_BILIBILI_THREAD_POOL = Executors.newFixedThreadPool(10);

    private static final ExecutorService FIXED_DOUYIN_DETAIL_THREAD_POOL = Executors.newFixedThreadPool(2);


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

    public void syncBiliBiliExecute(Runnable runnable) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                log.error("执行异步任务失败: ", e);
            }
        }, FIXED_BILIBILI_THREAD_POOL);
        future.join();
    }

    public <T> T syncBiliBiliExecute(Supplier<T> supplier) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                log.error("执行异步任务失败: ", e);
                return null;
            }
        }, FIXED_BILIBILI_THREAD_POOL);
        return future.join();
    }

    public void syncDouyinExecute(Runnable runnable) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                ThreadUtil.safeSleep(RandomUtil.randomInt(100, 500));
                runnable.run();
            } catch (Exception e) {
                log.error("执行异步任务失败: ", e);
            }
        }, FIXED_DOUYIN_DETAIL_THREAD_POOL);
        future.join();
    }

    public void syncDouyinExecuteSignal(Callable<AsyncResult> callable, int retry) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                int realRetry = retry + 1;
                String result = "";
                for (int i = 0; i <= realRetry; i++) {
                    ThreadUtil.safeSleep(RandomUtil.randomInt(200, 300));
                    AsyncResult call = callable.call();
                    result = call.getResult();
                    if (call.isSuccess()) {
                        return;
                    }
                }
                log.error("任务重试失败: {}", result);
            } catch (Exception e) {
                log.error("执行异步任务失败: ", e);
            }
        }, FIXED_DOUYIN_DETAIL_THREAD_POOL);
        future.join();
    }

    @Data
    public static class AsyncResult {
        private boolean success;

        private String result;

        public AsyncResult(boolean success, String result) {
            this.success = success;
            this.result = result;
        }
    }


}

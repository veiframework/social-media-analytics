package com.chargehub.admin.datasync;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
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

    private static final ExecutorService WECHAT_VIDEO_POOL = Executors.newFixedThreadPool(1);


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

    public void syncWechatVideoSignal(Callable<AsyncResult> callable, int retry) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                int realRetry = retry + 1;
                String result = "";
                for (int i = 0; i <= realRetry; i++) {
                    ThreadUtil.safeSleep(RandomUtil.randomInt(500, 1000));
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
        }, WECHAT_VIDEO_POOL);
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
                    ThreadUtil.safeSleep(RandomUtil.randomInt(300, 500));
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

    public String retryExecute(Callable<AsyncResult> callable, int retry) {
        int realRetry = retry + 1;
        String result = "";
        for (int i = 0; i <= realRetry; i++) {
            try {
                ThreadUtil.safeSleep(RandomUtil.randomInt(300, 500));
                AsyncResult call = callable.call();
                result = call.getResult();
                if (call.isSuccess()) {
                    return result;
                }
            } catch (Exception e) {
                result = e.getMessage();
            }
        }
        log.error("任务重试失败: {}", result);
        return null;
    }


    public <T> T retryWithExponentialBackoff(Supplier<T> supplier, int maxRetries, String businessType) {
        return retryWithExponentialBackoff(supplier, maxRetries, 1.5, businessType);
    }

    /**
     * 指数退避重试工具方法
     *
     * @param supplier   要执行的操作（返回结果）
     * @param maxRetries 最大重试次数（总尝试 = maxRetries + 1）
     * @param multiplier 指数倍数，例如 2.0
     * @param <T>        返回类型
     * @return 成功时的结果
     * @throws RuntimeException 如果所有重试都失败
     */
    public <T> T retryWithExponentialBackoff(Supplier<T> supplier, int maxRetries, double multiplier, String businessType) {
        long delayMs = RandomUtil.randomInt(300, 1000);
        T result = null;
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                result = supplier.get();
                break;
            } catch (Exception e) {
                if (attempt == maxRetries) {
                    log.error(businessType + " attempts failed " + (maxRetries + 1), e);
                    return null;
                }
                // Full Jitter: 随机 [0, delayMs]
                long jitterDelay = ThreadLocalRandom.current().nextLong(0, delayMs + 1);
                ThreadUtil.safeSleep(jitterDelay);
                // 指数增长（防止溢出）限制最大延迟 30s
                delayMs = Math.min(delayMs * (long) multiplier, 30_000);
            }
        }
        return result;
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

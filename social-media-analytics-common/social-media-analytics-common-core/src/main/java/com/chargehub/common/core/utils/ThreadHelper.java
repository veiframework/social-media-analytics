package com.chargehub.common.core.utils;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Zhanghaowei
 * @date 2024/10/14 10:20
 */
public class ThreadHelper {

    private static final ThreadPoolTaskExecutor EXECUTOR = new ThreadPoolTaskExecutor();


    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        EXECUTOR.setMaxPoolSize(availableProcessors * 2);
        EXECUTOR.setCorePoolSize(availableProcessors + 1);
        EXECUTOR.setQueueCapacity(availableProcessors * 100);
        EXECUTOR.setKeepAliveSeconds(60);
        EXECUTOR.setAwaitTerminationSeconds(60);
        EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        EXECUTOR.initialize();
    }

    public static void execute(Runnable runnable) {
        EXECUTOR.execute(runnable);
    }



}

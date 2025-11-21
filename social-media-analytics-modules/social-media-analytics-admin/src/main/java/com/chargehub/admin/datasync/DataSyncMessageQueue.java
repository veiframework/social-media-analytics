package com.chargehub.admin.datasync;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Component
public class DataSyncMessageQueue {

    private static final ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(10);


    public void execute(Runnable runnable) {
        FIXED_THREAD_POOL.execute(runnable);
    }

}

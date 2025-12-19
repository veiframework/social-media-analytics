package com.chargehub.admin.account.service;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.domain.SocialMediaAccountTask;
import com.chargehub.admin.account.mapper.SocialMediaAccountTaskMapper;
import com.chargehub.common.datasource.mybatis.MybatisDbUtils;
import com.chargehub.common.redis.service.RedisService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SocialMediaAccountTaskService {

    public static final String BATCH_ADD_TASK_LOCK = "batch-add-task";

    @Autowired
    private SocialMediaAccountTaskMapper socialMediaAccountTaskMapper;

    @Autowired
    private RedisService redisService;

    public synchronized void batchAddTask(Collection<SocialMediaAccount> accounts) {
        if (CollectionUtils.isEmpty(accounts)) {
            return;
        }
        redisService.lock(BATCH_ADD_TASK_LOCK, locked -> {
            Assert.isTrue(locked, "当前操作用户过多请稍后再试!");
            List<SocialMediaAccountTask> tasks = accounts.stream().map(i -> {
                SocialMediaAccountTask task = new SocialMediaAccountTask();
                task.setAccountId(i.getId());
                task.setPlatformId(i.getPlatformId());
                return task;
            }).collect(Collectors.toList());
            MybatisDbUtils.saveOrIgnoreBatch(tasks, 1000);
            return null;
        }, 60);
    }

    public List<SocialMediaAccountTask> getAll() {
        List<SocialMediaAccountTask> accounts = socialMediaAccountTaskMapper.lambdaQuery().list();
        Map<String, Queue<SocialMediaAccountTask>> platformQueues = new HashMap<>();
        // 按平台分组
        for (SocialMediaAccountTask account : accounts) {
            platformQueues.computeIfAbsent(account.getPlatformId(), k -> new LinkedList<>()).offer(account);
        }
        List<SocialMediaAccountTask> result = new ArrayList<>();
        // 使用固定的平台顺序，确保一致性
        List<String> platforms = platformQueues.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
        boolean hasMore = true;
        while (hasMore) {
            hasMore = false;
            for (String platform : platforms) {
                Queue<SocialMediaAccountTask> queue = platformQueues.get(platform);
                if (queue == null || queue.isEmpty()) {
                    continue;
                }
                result.add(queue.poll());
                hasMore = true;
            }
        }
        return result;
    }

    public void deleteTaskById(String accountId) {
        this.socialMediaAccountTaskMapper.lambdaUpdate().eq(SocialMediaAccountTask::getAccountId, accountId).remove();
    }

}

package com.chargehub.admin.work.service;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.mapper.SocialMediaAccountMapper;
import com.chargehub.admin.account.service.SocialMediaAccountTaskService;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.domain.SocialMediaWorkTask;
import com.chargehub.admin.work.mapper.SocialMediaWorkTaskMapper;
import com.chargehub.common.datasource.mybatis.MybatisDbUtils;
import com.chargehub.common.redis.service.RedisService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SocialMediaWorkTaskService {

    public static final String BATCH_ADD_TASK_LOCK = "batch-add-work-task:";

    @Autowired
    private RedisService redisService;

    @Autowired
    private SocialMediaWorkTaskMapper socialMediaWorkTaskMapper;

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Autowired
    private SocialMediaAccountMapper socialMediaAccountMapper;

    @Autowired
    private SocialMediaAccountTaskService socialMediaAccountTaskService;

    public synchronized void addTask(String workId) {
        SocialMediaWork socialMediaWork = this.socialMediaWorkService.getById(workId);
        if (socialMediaWork == null) {
            return;
        }
        String platformId = socialMediaWork.getPlatformId();
        if (!SocialMediaPlatformEnum.WECHAT_VIDEO.getDomain().equals(platformId)) {
            this.batchAddTask(Lists.newArrayList(socialMediaWork), false);
            return;
        }
        SocialMediaAccount socialMediaAccount = this.socialMediaAccountMapper.doGetDetailById(socialMediaWork.getAccountId());
        if (socialMediaAccount == null) {
            log.error("同步作品时找不到账号了");
            return;
        }
        socialMediaAccountTaskService.batchAddTask(Lists.newArrayList(socialMediaAccount), false);
    }

    public synchronized void batchAddTask(Collection<SocialMediaWork> works, boolean scheduler) {
        if (CollectionUtils.isEmpty(works)) {
            return;
        }
        String userId = new ArrayList<>(works).get(0).getUserId();
        redisService.lock(BATCH_ADD_TASK_LOCK + userId, locked -> {
            Assert.isTrue(locked, "当前操作用户过多请稍后再试!");
            List<SocialMediaWorkTask> tasks = works.stream().map(i -> {
                SocialMediaWorkTask task = new SocialMediaWorkTask();
                task.setWorkId(i.getId());
                task.setPlatformId(i.getPlatformId());
                return task;
            }).collect(Collectors.toList());
            if (scheduler) {
                MybatisDbUtils.saveOrIgnoreBatch(tasks, 1000);
            } else {
                Db.saveOrUpdateBatch(tasks);
            }
            return null;
        }, 60);
    }

    @SuppressWarnings("unchecked")
    public List<SocialMediaWorkTask> getAllByPlatformId(String platformId, Integer limit) {
        return socialMediaWorkTaskMapper.lambdaQuery().eq(SocialMediaWorkTask::getPlatformId, platformId)
                .orderByDesc(SocialMediaWorkTask::getCreator, SocialMediaWorkTask::getCreateTime)
                .last(limit != null, "LIMIT " + limit)
                .list();
    }

    public void deleteTaskByIds(List<String> workIds) {
        if (CollectionUtils.isEmpty(workIds)) {
            return;
        }
        workIds.sort(Comparator.naturalOrder());
        this.socialMediaWorkTaskMapper.lambdaUpdate().in(SocialMediaWorkTask::getWorkId, workIds).remove();
    }


}

package com.chargehub.admin.work.service;

import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.mapper.SocialMediaAccountMapper;
import com.chargehub.admin.account.service.SocialMediaAccountTaskService;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.domain.SocialMediaWorkCreateTaskEvent;
import com.chargehub.common.redis.service.RedisService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@Service
public class SocialMediaWorkTaskService implements ApplicationContextAware {


    public static final String DEFAULT_SYNC_WORK_TASK = "default_sync_work_task_";

    public static final String COOKIE_SYNC_WORK_TASK = "cookie_sync_work_task_";

    @Autowired
    private RedisService redisService;

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Autowired
    private SocialMediaAccountMapper socialMediaAccountMapper;

    @Autowired
    private SocialMediaAccountTaskService socialMediaAccountTaskService;

    private ApplicationContext applicationContext;

    public synchronized void addTask(String workId) {
        SocialMediaWork socialMediaWork = this.socialMediaWorkService.getById(workId);
        if (socialMediaWork == null) {
            return;
        }
        SocialMediaAccount socialMediaAccount = this.socialMediaAccountMapper.doGetDetailById(socialMediaWork.getAccountId());
        if (socialMediaAccount == null) {
            log.error("同步作品时找不到账号了");
            return;
        }
        String platformId = socialMediaWork.getPlatformId();
        if (SocialMediaPlatformEnum.WECHAT_VIDEO.getDomain().equals(platformId)) {
            socialMediaAccountTaskService.batchAddTask(Lists.newArrayList(socialMediaAccount), false);
            return;
        }
        boolean isLogin = StringUtils.isNotBlank(socialMediaAccount.getStorageState());
        List<SocialMediaWork> list = Lists.newArrayList(socialMediaWork);
        this.batchAddTask(list, isLogin);
    }

    public synchronized void batchAddTask(Collection<SocialMediaWork> works, boolean isLogin) {
        if (CollectionUtils.isEmpty(works)) {
            return;
        }
        Map<String, Set<String>> collect = works.stream().collect(Collectors.groupingBy(SocialMediaWork::getPlatformId, Collectors.mapping(SocialMediaWork::getId, Collectors.toSet())));
        collect.forEach((platformId, ids) -> {
            String key = isLogin ? COOKIE_SYNC_WORK_TASK + platformId : DEFAULT_SYNC_WORK_TASK + platformId;
            redisService.setCacheSet(key, ids);
        });
        this.applicationContext.publishEvent(new SocialMediaWorkCreateTaskEvent(works));
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

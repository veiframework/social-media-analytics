package com.chargehub.admin.scheduler;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.api.domain.SysUser;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import com.chargehub.admin.datasync.domain.SocialMediaWorkDetail;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.WorkCreateStatusEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.dto.SocialMediaWorkCreateQueryDto;
import com.chargehub.admin.work.service.SocialMediaWorkCreateService;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.vo.SocialMediaWorkCreateVo;
import com.chargehub.biz.admin.service.ISysUserService;
import com.chargehub.common.core.constant.CacheConstants;
import com.chargehub.common.core.utils.ExceptionUtil;
import com.chargehub.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Component("createWorkScheduler")
@Slf4j
public class CreateWorkScheduler {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    @Autowired
    private SocialMediaWorkCreateService socialMediaWorkCreateService;

    @Autowired
    private DataSyncManager dataSyncManager;

    public static final String CREATE_WORK_LOCK = "social_media_work_create_lock";

    public static final ExecutorService CREATE_WORK_POOL = Executors.newFixedThreadPool(5);


    @SuppressWarnings("unchecked")
    public void execute() {
        Boolean hasKey = redisService.hasKey(CacheConstants.SYNCING_WORK_LOCK);
        if (BooleanUtils.isTrue(hasKey)) {
            return;
        }
        SocialMediaWorkCreateQueryDto queryDto = new SocialMediaWorkCreateQueryDto();
        queryDto.setRetryCount(0);
        List<SocialMediaWorkCreateVo> all = (List<SocialMediaWorkCreateVo>) socialMediaWorkCreateService.getAll(queryDto);
        if (CollectionUtils.isEmpty(all)) {
            return;
        }
        List<CompletableFuture<Void>> allFutures = new ArrayList<>();
        for (SocialMediaWorkCreateVo socialMediaWorkCreateVo : all) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> this.create(socialMediaWorkCreateVo), CREATE_WORK_POOL);
            allFutures.add(future);
        }
        DateTime now = DateUtil.date();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("作品创建任务开始 {}", now);
        try {
            CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).get(30, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("作品创建任务异常 {}", e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            stopWatch.stop();
            log.info("作品创建任务结束 {}秒", stopWatch.getTotalTimeSeconds());
        }
    }

    private void create(SocialMediaWorkCreateVo socialMediaWorkCreateVo) {
        String id = socialMediaWorkCreateVo.getId();
        try {
            this.socialMediaWorkCreateService.updateCreateStatus(id, WorkCreateStatusEnum.PROCESSING, null, null, false);
            this.createWorkByShareUrl(socialMediaWorkCreateVo);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (StringUtils.isNotBlank(errorMsg) && (errorMsg.contains("重复") || errorMsg.contains("下架"))) {
                this.socialMediaWorkCreateService.updateStatusNoRetry(id, WorkCreateStatusEnum.FAIL, errorMsg, null);
                return;
            }
            String errorStack = com.chargehub.common.core.utils.StringUtils.substring(ExceptionUtil.getExceptionMessage(e), 0, 2000);
            this.socialMediaWorkCreateService.updateCreateStatus(id, WorkCreateStatusEnum.FAIL, errorMsg, errorStack, true);
        }
    }

    public void createWorkByShareUrl(SocialMediaWorkCreateVo dto) {
        String id = dto.getId();
        String shareLink = dto.getShareLink();
        String userId = dto.getCreator();
        SysUser sysUser = sysUserService.selectUserById(Long.parseLong(userId));
        if (sysUser == null) {
            log.error("用户找不到了");
            return;
        }
        String tenantId = sysUser.getShopId() + "";
        String accountType = dto.getAccountType();
        String customType = dto.getCustomType();
        SocialMediaPlatformEnum.PlatformExtra platformEnum = SocialMediaPlatformEnum.getPlatformByWorkUrl(shareLink);
        SocialMediaWorkDetail<SocialMediaWork> socialMediaWorkDetail = this.dataSyncManager.fetchWork(shareLink, platformEnum);
        Assert.notNull(socialMediaWorkDetail, "获取作品失败,请重试");
        SocialMediaWork socialMediaWork = socialMediaWorkDetail.getWork();
        Assert.isTrue(!"-1".equals(socialMediaWork.getWorkUid()), "作品已经下架,无法添加");
        SocialMediaUserInfo socialMediaUserInfo = socialMediaWorkDetail.getSocialMediaUserInfo();
        redisService.lock(CREATE_WORK_LOCK, locked -> {
            Assert.isTrue(locked, "创建作品获取锁失败");
            SocialMediaAccount socialMediaAccount = socialMediaAccountService.getAndSave(socialMediaUserInfo, userId, accountType, platformEnum.getPlatformEnum(), tenantId);
            socialMediaWork.setUserId(socialMediaAccount.getUserId());
            socialMediaWork.setAccountId(socialMediaAccount.getId());
            socialMediaWork.setTenantId(socialMediaAccount.getTenantId());
            socialMediaWork.setAccountType(socialMediaAccount.getType());
            socialMediaWork.setShareLink(shareLink);
            socialMediaWork.setTenantId(tenantId);
            socialMediaWork.setCustomType(customType);
            socialMediaWork.setSyncWorkDate(new Date());
            String workId = socialMediaWorkService.getAndSave(socialMediaWork);
            String dbUserId = socialMediaAccount.getUserId();
            boolean equals = dbUserId.equals(userId);
            String errorMsg = equals ? null : "注意!该作品归属于员工" + dbUserId + ",如您不是组长无权限查看作品";
            this.socialMediaWorkCreateService.updateStatusNoRetry(id, WorkCreateStatusEnum.SUCCESS, errorMsg, workId);
            return null;
        }, 120);
    }


}

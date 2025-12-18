package com.chargehub.admin.scheduler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import com.chargehub.admin.datasync.domain.SocialMediaWorkDetail;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.WorkCreateStatusEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.dto.SocialMediaWorkCreateQueryDto;
import com.chargehub.admin.work.dto.SocialMediaWorkDto;
import com.chargehub.admin.work.service.SocialMediaWorkCreateService;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.vo.SocialMediaWorkCreateVo;
import com.chargehub.common.core.utils.ExceptionUtil;
import com.chargehub.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Component("createWorkScheduler")
@Slf4j
public class CreateWorkScheduler {

    private static final String CREATE_WORK_TASK_LOCK = "CREATE_WORK_TASK_LOCK";

    @Autowired
    private RedisService redisService;

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    @Autowired
    private SocialMediaWorkCreateService socialMediaWorkCreateService;

    @Autowired
    private DataSyncManager dataSyncManager;

    @SuppressWarnings("unchecked")
    public void execute() {
        redisService.lock(CREATE_WORK_TASK_LOCK, locked -> {
            if (BooleanUtils.isFalse(locked)) {
                return null;
            }
            String lastSyncDate = redisService.getCacheObject(DataSyncWorkSchedulerV3.LAST_TASK_DATE);
            if (StringUtils.isNotBlank(lastSyncDate)) {
                LocalDateTime lastLocalDateTime = LocalDateTime.parse(lastSyncDate, DatePattern.NORM_DATETIME_FORMATTER).plusMinutes(30);
                LocalDateTime nextTime = lastLocalDateTime.plusMinutes(110);
                Date date = new Date();
                boolean in = DateUtil.isIn(date, DateUtil.date(lastLocalDateTime), DateUtil.date(nextTime));
                if (!in) {
                    return null;
                }
            }
            SocialMediaWorkCreateQueryDto queryDto = new SocialMediaWorkCreateQueryDto();
            queryDto.setRetryCount(0);
            List<SocialMediaWorkCreateVo> all = (List<SocialMediaWorkCreateVo>) socialMediaWorkCreateService.getAll(queryDto);
            for (SocialMediaWorkCreateVo socialMediaWorkCreateVo : all) {
                this.create(socialMediaWorkCreateVo);
            }
            return null;
        });
    }

    private void create(SocialMediaWorkCreateVo socialMediaWorkCreateVo) {
        String id = socialMediaWorkCreateVo.getId();
        try {
            this.socialMediaWorkCreateService.updateCreateStatus(id, WorkCreateStatusEnum.PROCESSING, null, null, false);
            ThreadUtil.safeSleep(RandomUtil.randomInt(300, 700));
            this.createWorkByShareUrl(socialMediaWorkCreateVo);
            this.socialMediaWorkCreateService.updateStatusNoRetry(id, WorkCreateStatusEnum.SUCCESS, null);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg.contains("重复") || errorMsg.contains("下架")) {
                this.socialMediaWorkCreateService.updateStatusNoRetry(id, WorkCreateStatusEnum.FAIL, errorMsg);
            } else {
                String errorStack = com.chargehub.common.core.utils.StringUtils.substring(ExceptionUtil.getExceptionMessage(e), 0, 2000);
                this.socialMediaWorkCreateService.updateCreateStatus(id, WorkCreateStatusEnum.FAIL, errorMsg, errorStack, true);
            }
        }
    }

    public void createWorkByShareUrl(SocialMediaWorkCreateVo dto) {
        String shareLink = dto.getShareLink();
        String userId = dto.getCreator();
        String accountType = dto.getAccountType();
        SocialMediaPlatformEnum.PlatformExtra platformEnum = SocialMediaPlatformEnum.getPlatformByWorkUrl(shareLink);
        SocialMediaWorkDetail<SocialMediaWork> socialMediaWorkDetail = this.dataSyncManager.getWork("", shareLink, platformEnum);
        Assert.notNull(socialMediaWorkDetail, "获取作品失败,请重试");
        SocialMediaWork socialMediaWork = socialMediaWorkDetail.getWork();
        Assert.isTrue(!"-1".equals(socialMediaWork.getWorkUid()), "作品已经下架,无法添加");
        SocialMediaUserInfo socialMediaUserInfo = socialMediaWorkDetail.getSocialMediaUserInfo();
        SocialMediaAccount socialMediaAccount = socialMediaAccountService.getAndSave(socialMediaUserInfo, userId, accountType, platformEnum.getPlatformEnum());
        socialMediaWork.setUserId(socialMediaAccount.getUserId());
        socialMediaWork.setAccountId(socialMediaAccount.getId());
        socialMediaWork.setTenantId(socialMediaAccount.getTenantId());
        socialMediaWork.setAccountType(socialMediaAccount.getType());
        socialMediaWork.setShareLink(shareLink);
        SocialMediaWorkDto socialMediaWorkDto = BeanUtil.copyProperties(socialMediaWork, SocialMediaWorkDto.class);
        socialMediaWorkService.create(socialMediaWorkDto);
    }


}

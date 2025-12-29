package com.chargehub.admin.scheduler;

import cn.hutool.core.bean.BeanUtil;
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
import com.chargehub.admin.work.dto.SocialMediaWorkDto;
import com.chargehub.admin.work.service.SocialMediaWorkCreateService;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.vo.SocialMediaWorkCreateVo;
import com.chargehub.biz.admin.service.ISysUserService;
import com.chargehub.common.core.utils.ExceptionUtil;
import com.chargehub.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

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

    @SuppressWarnings("unchecked")
    public void execute() {
        Boolean hasKey = redisService.hasKey(AbstractWorkScheduler.SYNCING_WORK_LOCK);
        if (BooleanUtils.isTrue(hasKey)) {
            return;
        }
        SocialMediaWorkCreateQueryDto queryDto = new SocialMediaWorkCreateQueryDto();
        queryDto.setRetryCount(0);
        List<SocialMediaWorkCreateVo> all = (List<SocialMediaWorkCreateVo>) socialMediaWorkCreateService.getAll(queryDto);
        for (SocialMediaWorkCreateVo socialMediaWorkCreateVo : all) {
            this.create(socialMediaWorkCreateVo);
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
                this.socialMediaWorkCreateService.updateStatusNoRetry(id, WorkCreateStatusEnum.FAIL, errorMsg);
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
        SocialMediaPlatformEnum.PlatformExtra platformEnum = SocialMediaPlatformEnum.getPlatformByWorkUrl(shareLink);
        SocialMediaWorkDetail<SocialMediaWork> socialMediaWorkDetail = this.dataSyncManager.fetchWork(shareLink, platformEnum);
        Assert.notNull(socialMediaWorkDetail, "获取作品失败,请重试");
        SocialMediaWork socialMediaWork = socialMediaWorkDetail.getWork();
        Assert.isTrue(!"-1".equals(socialMediaWork.getWorkUid()), "作品已经下架,无法添加");
        SocialMediaUserInfo socialMediaUserInfo = socialMediaWorkDetail.getSocialMediaUserInfo();
        SocialMediaAccount socialMediaAccount = socialMediaAccountService.getAndSave(socialMediaUserInfo, userId, accountType, platformEnum.getPlatformEnum(), tenantId);
        socialMediaWork.setUserId(socialMediaAccount.getUserId());
        socialMediaWork.setAccountId(socialMediaAccount.getId());
        socialMediaWork.setTenantId(socialMediaAccount.getTenantId());
        socialMediaWork.setAccountType(socialMediaAccount.getType());
        socialMediaWork.setShareLink(shareLink);
        socialMediaWork.setTenantId(tenantId);
        SocialMediaWorkDto socialMediaWorkDto = BeanUtil.copyProperties(socialMediaWork, SocialMediaWorkDto.class);
        socialMediaWorkService.create(socialMediaWorkDto);
        String dbUserId = socialMediaAccount.getUserId();
        boolean equals = dbUserId.equals(userId);
        String errorMsg = equals ? null : "注意!该作品归属于员工" + dbUserId + ",如您不是组长无权限查看作品";
        this.socialMediaWorkCreateService.updateStatusNoRetry(id, WorkCreateStatusEnum.SUCCESS, errorMsg);
    }


}

package com.chargehub.admin.account.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.dto.*;
import com.chargehub.admin.account.mapper.SocialMediaAccountMapper;
import com.chargehub.admin.account.vo.SocialMediaAccountStatisticVo;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.DataSyncMessageQueue;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import com.chargehub.admin.datasync.domain.SocialMediaWorkDetail;
import com.chargehub.admin.enums.AutoSyncEnum;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.SyncWorkStatusEnum;
import com.chargehub.admin.scheduler.DataSyncWorkSchedulerV3;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.dto.SocialMediaWorkDto;
import com.chargehub.admin.work.dto.SocialMediaWorkShareLinkDto;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.common.redis.service.RedisService;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import com.chargehub.common.security.utils.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SocialMediaAccountService extends AbstractZ9CrudServiceImpl<SocialMediaAccountMapper, SocialMediaAccount> {

    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;

    @Autowired
    private DataSyncManager dataSyncManager;

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Autowired
    private DataSyncMessageQueue dataSyncMessageQueue;

    @Autowired
    private RedisService redisService;

    public SocialMediaAccountService(SocialMediaAccountMapper baseMapper) {
        super(baseMapper);
    }

    public synchronized SocialMediaAccount getAndSave(SocialMediaUserInfo socialMediaUserInfo, String userId, String type, SocialMediaPlatformEnum platformEnum) {
        String uid = socialMediaUserInfo.getUid();
        String secUid = socialMediaUserInfo.getSecUid();
        String nickname = socialMediaUserInfo.getNickname();
        return redisService.lock("lock:social-media-account:" + secUid, locked -> {
            Assert.isTrue(locked, "操作人数过多请稍后再试");
            SocialMediaAccount socialMediaAccount = this.getBySecUid(secUid);
            if (socialMediaAccount == null) {
                socialMediaAccount = new SocialMediaAccount();
                socialMediaAccount.setPlatformId(platformEnum.getDomain());
                socialMediaAccount.setType(type);
                socialMediaAccount.setUserId(userId);
                socialMediaAccount.setNickname(nickname);
                socialMediaAccount.setSecUid(secUid);
                socialMediaAccount.setUid(uid);
                socialMediaAccount.setAutoSync(AutoSyncEnum.ENABLE.getDesc());
                this.baseMapper.insert(socialMediaAccount);
            } else {
                Assert.isTrue(userId.equals(socialMediaAccount.getUserId()), "用户" + userId + "请联系组长交接账号,该作品账号归属于其他人");
            }
            return socialMediaAccount;
        });
    }

    public SocialMediaAccount getBySecUid(String secUid) {
        return this.baseMapper.lambdaQuery().eq(SocialMediaAccount::getSecUid, secUid).one();
    }


    public void updateAutoSync(String id, String autoSync) {
        this.baseMapper.lambdaUpdate()
                .set(SocialMediaAccount::getAutoSync, autoSync)
                .eq(SocialMediaAccount::getId, id)
                .update();
    }

    @SuppressWarnings("unchecked")
    public Set<String> getAccountIdsByUserIds(Collection<String> userIds) {
        if (userIds != null && userIds.isEmpty()) {
            return new HashSet<>();
        }
        List<SocialMediaAccount> list = this.baseMapper.lambdaQuery().select(SocialMediaAccount::getId).in(userIds != null, SocialMediaAccount::getUserId, userIds).list();
        return list.stream().map(SocialMediaAccount::getId).collect(Collectors.toSet());
    }

    @Override
    public void deleteByIds(String ids) {
        redisService.lock(DataSyncWorkSchedulerV3.SYNC_ACCOUNT_LOCK + ids, locked -> {
            Assert.isTrue(locked, "账号正在同步数据,请稍后操作");
            String[] split = ids.split(",");
            if (ArrayUtils.isEmpty(split)) {
                return null;
            }
            List<String> idList = Arrays.stream(split).collect(Collectors.toList());
            for (String s : idList) {
                SocialMediaAccount mediaAccount = this.baseMapper.lambdaQuery().eq(SocialMediaAccount::getId, s).eq(SocialMediaAccount::getCrawler, 1).one();
                Assert.isNull(mediaAccount, "该账号被设置无法删除，如有疑问请联系管理员");
                this.baseMapper.deleteById(s);
            }
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    public String getUidByAccountId(String accountId) {
        SocialMediaAccount mediaAccount = this.baseMapper.lambdaQuery().select(SocialMediaAccount::getUid).eq(SocialMediaAccount::getId, accountId).one();
        if (mediaAccount == null) {
            return "";
        }
        return mediaAccount.getUid();
    }

    public SocialMediaAccount getById(String id) {
        return this.baseMapper.doGetDetailById(id);
    }

    public void updateSyncWorkStatus(String accountId, SyncWorkStatusEnum syncWorkStatusEnum) {
        this.baseMapper.lambdaUpdate()
                .set(SocialMediaAccount::getSyncWorkStatus, syncWorkStatusEnum.ordinal())
                .set(syncWorkStatusEnum == SyncWorkStatusEnum.COMPLETE, SocialMediaAccount::getSyncWorkDate, new Date())
                .eq(SocialMediaAccount::getId, accountId)
                .ne(SocialMediaAccount::getSyncWorkStatus, syncWorkStatusEnum.ordinal())
                .update();
    }

    public void updateSyncWorkError(int minute) {
        String now = DateUtil.now();
        this.baseMapper.lambdaUpdate()
                .set(SocialMediaAccount::getSyncWorkStatus, SyncWorkStatusEnum.ERROR.ordinal())
                .eq(SocialMediaAccount::getSyncWorkStatus, SyncWorkStatusEnum.SYNCING.ordinal())
                .isNotNull(SocialMediaAccount::getSyncWorkDate)
                .apply("TIMESTAMPDIFF(MINUTE, sync_work_date, '" + now + "') > " + minute)
                .update();
    }

    @SuppressWarnings("unchecked")
    public List<SocialMediaAccount> getAccountIds(SocialMediaAccountQueryDto dto) {
        Set<String> ids = dto.getId();
        Set<Integer> syncWorkStatus = dto.getSyncWorkStatus();
        Integer crawler = dto.getCrawler();
        Set<String> platformId = dto.getPlatformId();
        String autoSync = dto.getAutoSync();
        return this.baseMapper.lambdaQuery().select(SocialMediaAccount::getId, SocialMediaAccount::getPlatformId)
                .eq(StringUtils.isNotBlank(autoSync), SocialMediaAccount::getAutoSync, autoSync)
                .in(CollectionUtils.isNotEmpty(ids), SocialMediaAccount::getId, ids)
                .in(CollectionUtils.isNotEmpty(syncWorkStatus), SocialMediaAccount::getSyncWorkStatus, syncWorkStatus)
                .in(CollectionUtils.isNotEmpty(platformId), SocialMediaAccount::getPlatformId, platformId)
                .eq(crawler != null, SocialMediaAccount::getCrawler, crawler)
                .orderByAsc(SocialMediaAccount::getSyncWorkDate)
                .list();
    }

    public Integer getMaxAccountNum() {
        return Integer.parseInt(this.baseMapper.lambdaQuery().count() + "");
    }

    public IPage<SocialMediaAccountStatisticVo> getAccountStatistic(SocialMediaAccountQueryDto queryDto) {
        Set<String> userId = queryDto.getUserId();
        List<SocialMediaWork> socialMediaWorks = socialMediaWorkService.groupByAccountId(userId, queryDto.getAscFields(), queryDto.getDescFields());
        if (CollectionUtils.isEmpty(socialMediaWorks)) {
            return new Page<>();
        }
        Set<String> accountIds = socialMediaWorks.stream().map(SocialMediaWork::getAccountId).collect(Collectors.toSet());
        queryDto.setUserId(null);
        queryDto.setId(accountIds);
        queryDto.setAscFields(null);
        queryDto.setDescFields(null);
        IPage<SocialMediaAccountStatisticVo> page = this.baseMapper.doGetPage(queryDto).convert(i -> BeanUtil.copyProperties(i, SocialMediaAccountStatisticVo.class));
        List<SocialMediaAccountStatisticVo> records = page.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return page;
        }
        Map<String, SocialMediaAccountStatisticVo> collect = records.stream().collect(Collectors.toMap(SocialMediaAccountStatisticVo::getId, Function.identity()));
        List<SocialMediaAccountStatisticVo> list = new ArrayList<>();
        socialMediaWorks.forEach(socialMediaWork -> {
            SocialMediaAccountStatisticVo statisticVo = collect.get(socialMediaWork.getAccountId());
            if (statisticVo == null) {
                return;
            }
            BeanUtil.copyProperties(socialMediaWork, statisticVo, CopyOptions.create().setIgnoreNullValue(true));
            list.add(statisticVo);
        });
        page.setRecords(list);
        return page;
    }


    @Override
    public void create(Z9CrudDto<SocialMediaAccount> dto) {
        Long userId = SecurityUtils.getUserId();
        SocialMediaAccountDto socialMediaAccountDto = (SocialMediaAccountDto) dto;
        if (StringUtils.isBlank(socialMediaAccountDto.getUserId())) {
            socialMediaAccountDto.setUserId(userId + "");
        }
        super.create(dto);
    }

    public void createByShareLink(SocialMediaAccountShareLinkDto dto) {
        String shareLink = dto.getShareLink();
        String type = dto.getType();
        SocialMediaPlatformEnum.SecUser secUser = SocialMediaPlatformEnum.parseSecUserId(shareLink);
        Assert.notNull(secUser, "解析社交平台用户信息失败，请联系管理员");
        String secUserIdId = secUser.getId();
        //获取社交平台信息, 限流10个
        SocialMediaUserInfo socialMediaUserInfo = dataSyncMessageQueue.syncExecute(() -> dataSyncManager.getSocialMediaUserInfo(secUser.getPlatformEnum(), secUserIdId));
        Assert.notNull(socialMediaUserInfo, "获取社交平台用户信息失败，请联系管理员");
        //TODO 直接获取社交平台accessToken
        String nickname = socialMediaUserInfo.getNickname();
        String uid = socialMediaUserInfo.getUid();
        SocialMediaAccountDto socialMediaAccountDto = new SocialMediaAccountDto();
        socialMediaAccountDto.setSecUid(secUserIdId);
        socialMediaAccountDto.setPlatformId(secUser.getPlatform());
        socialMediaAccountDto.setUserId(dto.getUserId());
        socialMediaAccountDto.setUid(uid);
        socialMediaAccountDto.setNickname(nickname);
        socialMediaAccountDto.setType(type);
        this.create(socialMediaAccountDto);
    }

    public void createByWechatVideoNickname(SocialMediaAccountWechatVideoNicknameDto dto) {
        String nickname = dto.getNickname();
        String type = dto.getType();
        //获取社交平台信息, 限流10个
        SocialMediaUserInfo socialMediaUserInfo = dataSyncMessageQueue.syncExecute(() -> dataSyncManager.getSocialMediaUserInfo(SocialMediaPlatformEnum.WECHAT_VIDEO, nickname));
        Assert.notNull(socialMediaUserInfo, "获取社交平台用户信息失败，请联系管理员");
        //TODO 直接获取社交平台accessToken
        String uid = socialMediaUserInfo.getUid();
        SocialMediaAccountDto socialMediaAccountDto = new SocialMediaAccountDto();
        socialMediaAccountDto.setSecUid(socialMediaUserInfo.getUid());
        socialMediaAccountDto.setPlatformId(SocialMediaPlatformEnum.WECHAT_VIDEO.getDomain());
        socialMediaAccountDto.setUserId(dto.getUserId());
        socialMediaAccountDto.setUid(uid);
        socialMediaAccountDto.setNickname(nickname);
        socialMediaAccountDto.setType(type);
        socialMediaAccountDto.setAutoSync(AutoSyncEnum.ENABLE.getDesc());
        this.create(socialMediaAccountDto);
    }

    public void createWorkByShareUrl(SocialMediaWorkShareLinkDto dto) {
        String shareLink = dto.getShareLink();
        String userId = dto.getUserId();
        String accountType = dto.getAccountType();
        SocialMediaPlatformEnum.PlatformExtra platformEnum = dto.getPlatformEnum() == null ? SocialMediaPlatformEnum.getPlatformByWorkUrl(shareLink) : new SocialMediaPlatformEnum.PlatformExtra(dto.getPlatformEnum());
        SocialMediaWorkDetail<SocialMediaWork> socialMediaWorkDetail = this.dataSyncManager.getWork("", shareLink, platformEnum);
        Assert.notNull(socialMediaWorkDetail, "获取作品失败,请检查作品是否下架再重试");
        SocialMediaUserInfo socialMediaUserInfo = socialMediaWorkDetail.getSocialMediaUserInfo();
        SocialMediaAccount socialMediaAccount = this.getAndSave(socialMediaUserInfo, userId, accountType, platformEnum.getPlatformEnum());
        SocialMediaWork socialMediaWork = socialMediaWorkDetail.getWork();
        socialMediaWork.setUserId(userId);
        socialMediaWork.setAccountId(socialMediaAccount.getId());
        socialMediaWork.setTenantId(socialMediaAccount.getTenantId());
        socialMediaWork.setAccountType(socialMediaAccount.getType());
        socialMediaWork.setShareLink(shareLink);
        SocialMediaWorkDto socialMediaWorkDto = BeanUtil.copyProperties(socialMediaWork, SocialMediaWorkDto.class);
        socialMediaWorkService.create(socialMediaWorkDto);
    }

    public void transferAccount(SocialMediaTransferAccountDto transferAccountDto) {
        this.baseMapper.lambdaUpdate()
                .set(SocialMediaAccount::getUserId, transferAccountDto.getUserId())
                .eq(SocialMediaAccount::getId, transferAccountDto.getAccountId())
                .update();
        this.socialMediaWorkService.transferAccount(transferAccountDto);
    }


    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return SocialMediaAccountDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return SocialMediaAccountVo.class;
    }

    @Override
    public Class<? extends Z9CrudQueryDto<SocialMediaAccount>> queryDto() {
        return SocialMediaAccountQueryDto.class;
    }

    @Override
    public String excelName() {
        return "社交帐号管理";
    }
}

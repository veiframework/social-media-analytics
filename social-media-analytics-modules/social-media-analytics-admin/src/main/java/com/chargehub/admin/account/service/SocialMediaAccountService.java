package com.chargehub.admin.account.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
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
import com.chargehub.admin.enums.AutoSyncEnum;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.SyncWorkStatusEnum;
import com.chargehub.admin.scheduler.DataSyncWorkMonitorScheduler;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.redis.service.RedisService;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import com.chargehub.common.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

    @Autowired
    private HubProperties hubProperties;

    public SocialMediaAccountService(SocialMediaAccountMapper baseMapper) {
        super(baseMapper);
    }

    public synchronized SocialMediaAccount getAndSave(SocialMediaUserInfo socialMediaUserInfo, String userId, String type, SocialMediaPlatformEnum platformEnum, String tenantId) {
        String uid = socialMediaUserInfo.getUid();
        String secUid = socialMediaUserInfo.getSecUid();
        String nickname = socialMediaUserInfo.getNickname();
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
            socialMediaAccount.setSyncWorkDate(new Date());
            socialMediaAccount.setTenantId(tenantId);
            this.baseMapper.insert(socialMediaAccount);
        }
        return socialMediaAccount;
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

    public List<SocialMediaAccount> getAccountIdsByUserIds(Collection<String> userIds, String tenantId) {
        SocialMediaAccountQueryDto socialMediaAccountQueryDto = new SocialMediaAccountQueryDto();
        socialMediaAccountQueryDto.setCrawler(0);
        socialMediaAccountQueryDto.setAutoSync(AutoSyncEnum.ENABLE.getDesc());
        socialMediaAccountQueryDto.setTenantId(tenantId);
        if (CollectionUtils.isNotEmpty(userIds)) {
            socialMediaAccountQueryDto.setUserId(new HashSet<>(userIds));
        }
        return this.getAccountIds(socialMediaAccountQueryDto);
    }

    @Override
    public void deleteByIds(String ids) {
        Boolean hasKey = redisService.hasKey(DataSyncWorkMonitorScheduler.SYNCING_WORK_LOCK);
        cn.hutool.core.lang.Assert.isFalse(hasKey, "账号正在同步数据,请稍后操作");
        String[] split = ids.split(",");
        if (ArrayUtils.isEmpty(split)) {
            return;
        }
        List<String> idList = Arrays.stream(split).collect(Collectors.toList());
        for (String s : idList) {
            SocialMediaAccount mediaAccount = this.baseMapper.lambdaQuery().eq(SocialMediaAccount::getId, s).eq(SocialMediaAccount::getCrawler, 1).one();
            Assert.isNull(mediaAccount, "该账号被设置无法删除，如有疑问请联系管理员");
            Long num = socialMediaWorkService.getWorkNumByAccountId(s);
            if (num > 0) {
                this.baseMapper.lambdaUpdate().set(SocialMediaAccount::getAutoSync, AutoSyncEnum.DISABLE.getDesc()).eq(SocialMediaAccount::getId, s).update();
            } else {
                this.baseMapper.deleteById(s);
            }
        }
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

    @SuppressWarnings("unchecked")
    public List<SocialMediaAccount> getAccountIds(SocialMediaAccountQueryDto dto) {
        Set<String> ids = dto.getId();
        Set<Integer> syncWorkStatus = dto.getSyncWorkStatus();
        Integer crawler = dto.getCrawler();
        Set<String> platformId = dto.getPlatformId();
        String autoSync = dto.getAutoSync();
        Set<String> userId = dto.getUserId();
        long recentDays = hubProperties.getRecentDays();
        return this.baseMapper.lambdaQuery().select(SocialMediaAccount::getId, SocialMediaAccount::getPlatformId, SocialMediaAccount::getSyncWorkDate)
                .eq(StringUtils.isNotBlank(autoSync), SocialMediaAccount::getAutoSync, autoSync)
                .in(CollectionUtils.isNotEmpty(ids), SocialMediaAccount::getId, ids)
                .in(CollectionUtils.isNotEmpty(userId), SocialMediaAccount::getUserId, userId)
                .in(CollectionUtils.isNotEmpty(syncWorkStatus), SocialMediaAccount::getSyncWorkStatus, syncWorkStatus)
                .in(CollectionUtils.isNotEmpty(platformId), SocialMediaAccount::getPlatformId, platformId)
                .inSql(SocialMediaAccount::getId, "SELECT account_id FROM social_media_work WHERE state != 'deleted' AND TIMESTAMPDIFF(DAY, create_time, NOW()) <= " + recentDays)
                .eq(crawler != null, SocialMediaAccount::getCrawler, crawler)
                .orderByAsc(SocialMediaAccount::getSyncWorkDate)
                .list();
    }

    public Integer getMaxAccountNum() {
        return Integer.parseInt(this.baseMapper.lambdaQuery().count() + "");
    }

    public IPage<SocialMediaAccountStatisticVo> getAccountStatistic(SocialMediaAccountQueryDto queryDto) {
        Set<String> userId = queryDto.getUserId();
        Page<SocialMediaWork> pagination = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        IPage<SocialMediaWork> socialMediaWorksPage = socialMediaWorkService.groupByAccountId(pagination, userId, queryDto.getAscFields(), queryDto.getDescFields(), queryDto.getTenantId());
        if (CollectionUtils.isEmpty(socialMediaWorksPage.getRecords())) {
            return new Page<>();
        }
        List<SocialMediaWork> socialMediaWorks = socialMediaWorksPage.getRecords();
        Set<String> accountIds = socialMediaWorks.stream().map(SocialMediaWork::getAccountId).collect(Collectors.toSet());
        queryDto.setUserId(null);
        queryDto.setId(accountIds);
        queryDto.setAscFields(null);
        queryDto.setDescFields(null);
        queryDto.setSearchCount(false);
        List<SocialMediaAccountStatisticVo> records = this.baseMapper.doGetAll(queryDto).stream().map(i -> BeanUtil.copyProperties(i, SocialMediaAccountStatisticVo.class)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(records)) {
            return new Page<>();
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
        Page<SocialMediaAccountStatisticVo> page = new Page<>(socialMediaWorksPage.getCurrent(), socialMediaWorksPage.getSize(), socialMediaWorksPage.getTotal());
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
        socialMediaAccountDto.setSyncWorkDate(new Date());
        socialMediaAccountDto.setTenantId(dto.getTenantId());
        this.create(socialMediaAccountDto);
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

package com.chargehub.admin.account.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.dto.SocialMediaAccountDto;
import com.chargehub.admin.account.dto.SocialMediaAccountQueryDto;
import com.chargehub.admin.account.dto.SocialMediaAccountShareLinkDto;
import com.chargehub.admin.account.dto.SocialMediaAccountWechatVideoNicknameDto;
import com.chargehub.admin.account.mapper.SocialMediaAccountMapper;
import com.chargehub.admin.account.vo.SocialMediaAccountStatisticVo;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.DataSyncMessageQueue;
import com.chargehub.admin.datasync.domain.DataSyncParamContext;
import com.chargehub.admin.datasync.domain.SocialMediaDetail;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.SyncWorkStatusEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.dto.SocialMediaWorkDto;
import com.chargehub.admin.work.dto.SocialMediaWorkShareLinkDto;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import com.chargehub.common.security.utils.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
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


    public SocialMediaAccountService(SocialMediaAccountMapper baseMapper) {
        super(baseMapper);
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
        super.deleteByIds(ids);
//        this.socialMediaWorkService.deleteByAccountIds(ids);
    }

    @SuppressWarnings("unchecked")
    public String getUidByAccountId(String accountId) {
        SocialMediaAccount mediaAccount = this.baseMapper.lambdaQuery().select(SocialMediaAccount::getUid).eq(SocialMediaAccount::getId, accountId).one();
        if (mediaAccount == null) {
            return "";
        }
        return mediaAccount.getUid();
    }

    public void updateSyncWorkStatus(String accountId, SyncWorkStatusEnum syncWorkStatusEnum) {
        this.baseMapper.lambdaUpdate()
                .set(SocialMediaAccount::getSyncWorkStatus, syncWorkStatusEnum.ordinal())
                .set(SocialMediaAccount::getSyncWorkDate, new Date())
                .eq(SocialMediaAccount::getId, accountId)
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

    public IPage<SocialMediaAccountStatisticVo> getAccountStatistic(SocialMediaAccountQueryDto queryDto) {
        Set<String> userId = queryDto.getUserId();
        Map<String, SocialMediaWork> socialMediaWorkMap = socialMediaWorkService.groupByAccountId(userId, queryDto.getAscFields(), queryDto.getDescFields());
        if (MapUtils.isEmpty(socialMediaWorkMap)) {
            return new Page<>();
        }
        Set<String> accountIds = socialMediaWorkMap.keySet();
        queryDto.setUserId(null);
        queryDto.setId(accountIds);
        queryDto.setAscFields(null);
        queryDto.setDescFields(null);
        IPage<SocialMediaAccountStatisticVo> page = this.baseMapper.doGetPage(queryDto).convert(i -> BeanUtil.copyProperties(i, SocialMediaAccountStatisticVo.class));
        List<SocialMediaAccountStatisticVo> records = page.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return page;
        }
        for (SocialMediaAccountStatisticVo statisticVo : records) {
            String accountId = statisticVo.getId();
            SocialMediaWork socialMediaWork = socialMediaWorkMap.get(accountId);
            if (socialMediaWork == null) {
                continue;
            }
            BeanUtil.copyProperties(socialMediaWork, statisticVo, CopyOptions.create().setIgnoreNullValue(true));
        }
        records.sort(Comparator.comparing(SocialMediaAccountStatisticVo::getPlayNum).reversed());
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
        this.create(socialMediaAccountDto);
    }

    public void createWorkByShareUrl(SocialMediaWorkShareLinkDto dto) {
        String shareLink = dto.getShareLink();
        String userId = dto.getUserId();
        SocialMediaPlatformEnum platformEnum = dto.getPlatformEnum();
        SocialMediaDetail socialMediaDetail = platformEnum != null ? dataSyncManager.getSecUidByWorkUrl(platformEnum, shareLink) : dataSyncManager.getSecUidByWorkUrl(shareLink);
        String secUid = socialMediaDetail.getSecUid();
        String workUid = socialMediaDetail.getWorkUid();
        SocialMediaAccount socialMediaAccount = this.getBySecUid(secUid);
        Assert.notNull(socialMediaAccount, "请先添加账号");
        String platformId = socialMediaAccount.getPlatformId();
        SocialMediaWork socialMediaWork;
        if (StringUtils.isBlank(socialMediaAccount.getStorageState())) {
            socialMediaWork = dataSyncManager.getWork(platformId, workUid);
        } else {
            DataSyncParamContext dataSyncParamContext = new DataSyncParamContext();
            dataSyncParamContext.setStorageState(socialMediaAccount.getStorageState());
            dataSyncParamContext.setWorkUid(workUid);
            socialMediaWork = dataSyncManager.getWork(dataSyncParamContext, platformId);
        }
        Assert.notNull(socialMediaWork, "获取作品失败,请联系管理员");
        socialMediaWork.setUserId(userId);
        socialMediaWork.setAccountId(socialMediaAccount.getId());
        socialMediaWork.setTenantId(socialMediaAccount.getTenantId());
        socialMediaWork.setAccountType(socialMediaAccount.getType());
        SocialMediaWorkDto socialMediaWorkDto = BeanUtil.copyProperties(socialMediaWork, SocialMediaWorkDto.class);
        socialMediaWorkService.create(socialMediaWorkDto);
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

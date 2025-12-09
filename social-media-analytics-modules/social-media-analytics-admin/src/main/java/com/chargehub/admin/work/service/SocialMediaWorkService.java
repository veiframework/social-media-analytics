package com.chargehub.admin.work.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.chargehub.admin.account.dto.SocialMediaTransferAccountDto;
import com.chargehub.admin.enums.SyncWorkStatusEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.dto.SocialMediaWorkDto;
import com.chargehub.admin.work.dto.SocialMediaWorkQueryDto;
import com.chargehub.admin.work.mapper.SocialMediaWorkMapper;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.redis.service.RedisService;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SocialMediaWorkService extends AbstractZ9CrudServiceImpl<SocialMediaWorkMapper, SocialMediaWork> {

    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;

    @Autowired
    private HubProperties hubProperties;

    @Autowired
    private RedisService redisService;

    protected SocialMediaWorkService(SocialMediaWorkMapper baseMapper) {
        super(baseMapper);
    }

    public Map<String, SocialMediaWork> getByWorkUidList(Collection<String> workUidList) {
        if (CollectionUtils.isEmpty(workUidList)) {
            return new HashMap<>();
        }
        return this.baseMapper.lambdaQuery().in(SocialMediaWork::getWorkUid, workUidList)
                .list().stream()
                .collect(Collectors.toMap(SocialMediaWork::getWorkUid, Function.identity()));
    }

    public List<SocialMediaWorkVo> groupByUserIdAndPlatform(Collection<String> userIds) {
        List<SocialMediaWork> socialMediaWorks = this.baseMapper.groupByUserIdAndPlatform(userIds);
        return BeanUtil.copyToList(socialMediaWorks, SocialMediaWorkVo.class);
    }


    public Map<String, SocialMediaWork> groupByAccountId(Collection<String> userIds, Set<String> ascFields, Set<String> descFields) {
        List<SocialMediaWork> socialMediaWorks = this.baseMapper.groupByAccountId(userIds, ascFields, descFields);
        if (CollectionUtils.isEmpty(socialMediaWorks)) {
            return new HashMap<>();
        }
        return socialMediaWorks.stream().collect(Collectors.toMap(SocialMediaWork::getAccountId, Function.identity()));
    }

    public void deleteByAccountIds(String accountIds) {
        if (StringUtils.isBlank(accountIds)) {
            return;
        }
        Set<String> collect = Arrays.stream(accountIds.split(",")).collect(Collectors.toSet());
        this.baseMapper.lambdaUpdate()
                .in(SocialMediaWork::getAccountId, collect)
                .remove();
    }

    public SocialMediaWork getDomainById(String id) {
        return this.baseMapper.lambdaQuery().eq(SocialMediaWork::getId, id).one();
    }

    @SuppressWarnings("all")
    public List<SocialMediaWork> getWorkIds(Set<String> accountIds) {
        long recentDays = hubProperties.getRecentDays();
        return this.baseMapper.lambdaQuery()
                .select(SocialMediaWork::getId, SocialMediaWork::getPlatformId, SocialMediaWork::getMediaType, SocialMediaWork::getAccountId)
                .in(accountIds != null, SocialMediaWork::getAccountId, accountIds)
                .apply("TIMESTAMPDIFF(DAY, post_time, NOW()) <= " + recentDays)
                .list();
    }

    @SuppressWarnings("all")
    public List<SocialMediaWork> getLatestWork(String accountId) {
        long recentDays = hubProperties.getRecentDays();
        return this.baseMapper.lambdaQuery().eq(SocialMediaWork::getAccountId, accountId)
                .apply("TIMESTAMPDIFF(DAY, post_time, NOW()) <= " + recentDays)
                .list();
    }

    public void updateOne(SocialMediaWork socialMediaWork) {
        this.baseMapper.updateById(socialMediaWork);
    }

    public void transferAccount(SocialMediaTransferAccountDto dto) {
        this.baseMapper.lambdaUpdate()
                .set(SocialMediaWork::getUserId, dto.getUserId())
                .eq(SocialMediaWork::getAccountId, dto.getAccountId())
                .update();
    }

    public void saveOrUpdateBatch(Collection<SocialMediaWork> works) {
        Db.saveOrUpdateBatch(works);
    }

    public IPage<SocialMediaWorkVo> getPurviewPage(SocialMediaWorkQueryDto queryDto) {
        return this.baseMapper.doGetPage(queryDto).convert(i -> BeanUtil.copyProperties(i, voClass()));
    }

    public void updateSyncWorkStatus(String workId, SyncWorkStatusEnum syncWorkStatusEnum) {
        this.baseMapper.lambdaUpdate()
                .set(SocialMediaWork::getSyncWorkStatus, syncWorkStatusEnum.ordinal())
                .set(SocialMediaWork::getSyncWorkDate, new Date())
                .eq(SocialMediaWork::getId, workId)
                .ne(SocialMediaWork::getSyncWorkStatus, syncWorkStatusEnum.ordinal())
                .update();

//        Runnable runnable = () -> this.baseMapper.lambdaUpdate()
//                .set(SocialMediaWork::getSyncWorkStatus, syncWorkStatusEnum.ordinal())
//                .set(SocialMediaWork::getSyncWorkDate, new Date())
//                .eq(SocialMediaWork::getId, workId)
//                .ne(SocialMediaWork::getSyncWorkStatus, syncWorkStatusEnum.ordinal())
//                .update();
//        if (TransactionSynchronizationManager.isActualTransactionActive()) {
//            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//                @Override
//                public void afterCompletion(int status) {
//                    runnable.run();
//                }
//            });
//            return;
//        }
//        runnable.run();
    }


    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return SocialMediaWorkDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return SocialMediaWorkVo.class;
    }

    @Override
    public Class<? extends Z9CrudQueryDto<SocialMediaWork>> queryDto() {
        return SocialMediaWorkQueryDto.class;
    }

    @Override
    public String excelName() {
        return "作品列表";
    }
}

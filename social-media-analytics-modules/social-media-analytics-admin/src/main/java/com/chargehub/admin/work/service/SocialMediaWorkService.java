package com.chargehub.admin.work.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.chargehub.admin.account.dto.SocialMediaTransferAccountDto;
import com.chargehub.admin.enums.WorkPriorityEnum;
import com.chargehub.admin.enums.WorkStateEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.domain.SocialMediaWorkCreate;
import com.chargehub.admin.work.dto.SocialMediaWorkDto;
import com.chargehub.admin.work.dto.SocialMediaWorkPlayNumDto;
import com.chargehub.admin.work.dto.SocialMediaWorkQueryDto;
import com.chargehub.admin.work.mapper.SocialMediaWorkMapper;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
import com.chargehub.common.core.constant.CacheConstants;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.redis.service.RedisService;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.event.Z9BeforeCreateEvent;
import com.chargehub.common.security.template.event.Z9CreateEvent;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class SocialMediaWorkService extends AbstractZ9CrudServiceImpl<SocialMediaWorkMapper, SocialMediaWork> {

    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;

    @Autowired
    private RedisService redisService;

    private static long recentDays;

    protected SocialMediaWorkService(SocialMediaWorkMapper baseMapper,
                                     HubProperties hubProperties) {
        super(baseMapper);
        recentDays = hubProperties.getRecentDays();
        log.info("采集数据间隔天数: " + recentDays);
    }


    public void updateViewNum(SocialMediaWorkPlayNumDto dto) {
        String workId = dto.getWorkId();
        Integer playNum = dto.getPlayNum();
        Boolean hasKey = redisService.hasKey(CacheConstants.SYNCING_WORK_LOCK);
        cn.hutool.core.lang.Assert.isFalse(hasKey, "该作品账号正在同步数据请稍后再修改");
        SocialMediaWork work = this.baseMapper.lambdaQuery().eq(SocialMediaWork::getId, workId).one();
        int playNumUp = 0;
        int playNumUpChange = 0;
        if (work.getPlayFixed().equals(1)) {
            playNumUp = playNum - work.getPlayNum();
            playNumUpChange = playNumUp - work.getPlayNumUp();
        }
        this.baseMapper.lambdaUpdate()
                .set(SocialMediaWork::getPlayNum, playNum)
                .set(SocialMediaWork::getPlayNumUp, playNumUp)
                .set(SocialMediaWork::getPlayNumChange, playNumUpChange)
                .set(SocialMediaWork::getPlayFixed, 1)
                .eq(SocialMediaWork::getId, workId)
                .update();
    }

    public Map<String, SocialMediaWork> getByWorkUidList(Collection<String> workUidList) {
        if (CollectionUtils.isEmpty(workUidList)) {
            return new HashMap<>();
        }
        return this.baseMapper.lambdaQuery().in(SocialMediaWork::getWorkUid, workUidList)
                .list().stream()
                .collect(Collectors.toMap(SocialMediaWork::getWorkUid, Function.identity()));
    }

    public List<SocialMediaWorkVo> groupByUserIdAndPlatform(Collection<String> userIds, String tenantId) {
        List<SocialMediaWork> socialMediaWorks = this.baseMapper.groupByUserIdAndPlatform(userIds, tenantId);
        return BeanUtil.copyToList(socialMediaWorks, SocialMediaWorkVo.class);
    }


    public IPage<SocialMediaWork> groupByAccountId(Page<SocialMediaWork> page, Collection<String> userIds, Set<String> ascFields, Set<String> descFields, String tenantId) {
        return this.baseMapper.groupByAccountId(page, userIds, ascFields, descFields, tenantId);
    }

    public SocialMediaWorkVo getWorkDetail(String id, Set<String> roles, String userId) {
        SocialMediaWork work = this.baseMapper.lambdaQuery().eq(SocialMediaWork::getId, id).one();
        Assert.notNull(work, "作品不存在了: " + id);
        String targetUserId = work.getUserId();
        if (roles.contains("员工") && !targetUserId.equals(userId)) {
            throw new IllegalArgumentException("该作品账号归属于其他员工，请联系组长或主管");
        }
        return BeanUtil.copyProperties(work, SocialMediaWorkVo.class);
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

    public List<SocialMediaWork> getWorkByIds(Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return this.baseMapper.lambdaQuery().in(SocialMediaWork::getId, ids).list();
    }


    public List<SocialMediaWork> getLatestWork(String accountId, boolean isLogin) {
        return this.getLatestWork(accountId, null, isLogin);
    }


    public List<SocialMediaWork> getLatestWork(String accountId, Set<String> userIds, boolean isLogin) {
        long timestamp = Long.parseLong(LocalDateTime.now().format(DatePattern.PURE_DATETIME_FORMATTER));
        Set<String> members = redisService.getZSetMembers(CacheConstants.WORK_NEXT_CRAWL_TIME, true, timestamp);
        if (CollectionUtils.isEmpty(members)) {
            return new ArrayList<>();
        }
        List<String> ids = new ArrayList<>(members);
        List<List<String>> partition = Lists.partition(ids, 500);
        return partition.stream().flatMap(batch -> this.baseMapper.lambdaQuery()
                .eq(StringUtils.isNotBlank(accountId), SocialMediaWork::getAccountId, accountId)
                .in(CollectionUtils.isNotEmpty(userIds), SocialMediaWork::getUserId, userIds)
                .ne(SocialMediaWork::getState, WorkStateEnum.DELETED.getDesc())
                .inSql(isLogin, SocialMediaWork::getAccountId, "SELECT id FROM social_media_account WHERE storage_state IS NOT NULL")
                .inSql(!isLogin, SocialMediaWork::getAccountId, "SELECT id FROM social_media_account WHERE storage_state IS NULL")
                .in(SocialMediaWork::getId, batch)
                .in(SocialMediaWork::getPriority, WorkPriorityEnum.IMPORTANT.getCode(), WorkPriorityEnum.ACTIVE.getCode(), WorkPriorityEnum.NORMAL.getCode())
                .list().stream()).collect(Collectors.toList());
    }

    /**
     * 刷缓存
     *
     * @param accountId
     * @param isLogin
     * @return
     */
    public List<SocialMediaWork> getLatestWork0(String accountId, boolean isLogin) {
        LocalDate[] period = SocialMediaWorkService.getValidDatePeriod(LocalDateTime.now());
        return this.baseMapper.lambdaQuery()
                .eq(StringUtils.isNotBlank(accountId), SocialMediaWork::getAccountId, accountId)
                .ne(SocialMediaWork::getState, WorkStateEnum.DELETED.getDesc())
                .inSql(isLogin, SocialMediaWork::getAccountId, "SELECT id FROM social_media_account WHERE storage_state IS NOT NULL")
                .inSql(!isLogin, SocialMediaWork::getAccountId, "SELECT id FROM social_media_account WHERE storage_state IS NULL")
                .ge(SocialMediaWork::getCreateTime, period[0])
                .lt(SocialMediaWork::getCreateTime, period[1])
                .list();
    }

    public static LocalDate[] getValidDatePeriod(LocalDateTime now) {
        LocalDate today = now.toLocalDate();
        boolean isMidnight = now.getHour() == 5;
        if (isMidnight) {
            LocalDate start = today.minusDays(recentDays);
            return new LocalDate[]{start, today};
        }
        LocalDate end = today.plusDays(1);
        return new LocalDate[]{today, end};
    }

    public void transferAccount(SocialMediaTransferAccountDto dto) {
        this.baseMapper.lambdaUpdate()
                .set(SocialMediaWork::getUserId, dto.getUserId())
                .eq(SocialMediaWork::getAccountId, dto.getAccountId())
                .update();
    }

    public void updateBatchById(List<SocialMediaWork> works) {
        if (CollectionUtils.isEmpty(works)) {
            return;
        }
        //批量更新前按照id升序排序,防止死锁
        works.sort(Comparator.comparing(SocialMediaWork::getId));
        Db.updateBatchById(works);
    }

    public void saveOrUpdateBatch(List<SocialMediaWork> works) {
        Db.saveOrUpdateBatch(works);
    }

    public void deletedWechatVideo(String id) {
        this.baseMapper.lambdaUpdate()
                .eq(SocialMediaWork::getId, id)
                .set(SocialMediaWork::getState, WorkStateEnum.DELETED.getDesc())
                .update();
    }

    public void updateStateByShareLink(String shareLink, WorkStateEnum workStateEnum) {
        this.baseMapper.lambdaUpdate()
                .eq(SocialMediaWork::getShareLink, shareLink)
                .set(SocialMediaWork::getState, workStateEnum.getDesc())
                .update();
    }

    public IPage<SocialMediaWorkVo> getPurviewPage(SocialMediaWorkQueryDto queryDto) {
        return this.baseMapper.doGetPage(queryDto).convert(i -> BeanUtil.copyProperties(i, voClass()));
    }

    public Long getWorkNumByAccountId(String accountId) {
        return this.baseMapper.lambdaQuery().eq(SocialMediaWork::getAccountId, accountId).count();
    }

    public synchronized String getAndSave(SocialMediaWork socialMediaWork) {
        String workId;
        SocialMediaWork exist = this.baseMapper.lambdaQuery().eq(SocialMediaWork::getWorkUid, socialMediaWork.getWorkUid()).one();
        if (exist == null) {
            this.baseMapper.insert(socialMediaWork);
            workId = socialMediaWork.getId();
            SocialMediaWorkDto socialMediaWorkDto = BeanUtil.copyProperties(socialMediaWork, SocialMediaWorkDto.class);
            this.publishEvent(new Z9CreateEvent<>(socialMediaWork, socialMediaWorkDto, SocialMediaWork.class));
        } else {
            workId = exist.getId();
        }
        return workId;
    }

    @EventListener
    public void workListenCreateTaskBeforeCreate(Z9BeforeCreateEvent<SocialMediaWorkCreate> event) {
        List<SocialMediaWorkCreate> data = event.getData();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        for (SocialMediaWorkCreate datum : data) {
            String shareLink = datum.getShareLink();
            Long count = this.baseMapper.lambdaQuery().eq(SocialMediaWork::getShareLink, shareLink).count();
            Assert.isTrue(count == 0, "该作品已被添加,请搜索分享链接查看");
        }
    }

    public SocialMediaWork getById(String id) {
        return this.baseMapper.doGetDetailById(id);
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
        return "作品列表(仅供分析不得传播©lumengshop.com)";
    }
}

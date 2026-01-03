package com.chargehub.admin.work.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.lang.Assert;
import com.chargehub.admin.enums.WorkCreateStatusEnum;
import com.chargehub.admin.work.domain.SocialMediaWorkCreate;
import com.chargehub.admin.work.dto.SocialMediaWorkCreateDto;
import com.chargehub.admin.work.mapper.SocialMediaWorkCreateMapper;
import com.chargehub.admin.work.vo.SocialMediaWorkCreateVo;
import com.chargehub.common.redis.service.RedisService;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SocialMediaWorkCreateService extends AbstractZ9CrudServiceImpl<SocialMediaWorkCreateMapper, SocialMediaWorkCreate> {

    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;

    @Autowired
    private RedisService redisService;

    private static final String LOCK_CREATE_SHARE_LINK = "lockCreateShareLink:";

    protected SocialMediaWorkCreateService(SocialMediaWorkCreateMapper baseMapper) {
        super(baseMapper);
    }

    @Override
    public void create(Z9CrudDto<SocialMediaWorkCreate> dto) {
        SocialMediaWorkCreateDto socialMediaWorkCreateDto = (SocialMediaWorkCreateDto) dto;
        redisService.lock(LOCK_CREATE_SHARE_LINK + socialMediaWorkCreateDto.getShareLink(), locked -> {
            Assert.isTrue(locked, "请求过多,请稍后再试");
            super.create(dto);
            return null;
        });
    }

    public void retryCreate(String id) {
        this.baseMapper.lambdaUpdate()
                .eq(SocialMediaWorkCreate::getId, id)
                .set(SocialMediaWorkCreate::getCreateStatus, WorkCreateStatusEnum.WAIT.getDesc())
                .set(SocialMediaWorkCreate::getRetryCount, 4)
                .update();
    }

    public void updateStatusNoRetry(String id, WorkCreateStatusEnum status, String errorMsg, String workId) {
        this.baseMapper.lambdaUpdate()
                .set(SocialMediaWorkCreate::getCreateStatus, status.getDesc())
                .set(SocialMediaWorkCreate::getErrorMsg, errorMsg)
                .set(StringUtils.isNotBlank(workId), SocialMediaWorkCreate::getWorkId, workId)
                .set(errorMsg == null, SocialMediaWorkCreate::getErrorStack, null)
                .set(SocialMediaWorkCreate::getRetryCount, 0)
                .eq(SocialMediaWorkCreate::getId, id)
                .update();
    }

    public void updateCreateStatus(String id, WorkCreateStatusEnum status, String errorMsg, String errorStack, boolean updateRetryCount) {
        this.baseMapper.lambdaUpdate()
                .eq(SocialMediaWorkCreate::getId, id)
                .set(SocialMediaWorkCreate::getCreateStatus, status.getDesc())
                .set(StringUtils.isNotBlank(errorMsg), SocialMediaWorkCreate::getErrorMsg, errorMsg)
                .set(StringUtils.isNotBlank(errorStack), SocialMediaWorkCreate::getErrorStack, errorStack)
                .setSql(updateRetryCount, "retry_count = retry_count - 1")
                .update();
    }

    public boolean hasTask() {
        return this.baseMapper.lambdaQuery().gt(SocialMediaWorkCreate::getRetryCount, 0).count() > 0;
    }

    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return SocialMediaWorkCreateDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return SocialMediaWorkCreateVo.class;
    }

    @Override
    public String excelName() {
        return "创建作品任务";
    }
}

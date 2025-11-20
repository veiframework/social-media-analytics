package com.chargehub.admin.work.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.dto.SocialMediaWorkDto;
import com.chargehub.admin.work.dto.SocialMediaWorkQueryDto;
import com.chargehub.admin.work.mapper.SocialMediaWorkMapper;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
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

    public List<SocialMediaWorkVo> groupByUserIdAndPlatform(String userIds) {
        if (StringUtils.isBlank(userIds)) {
            return new ArrayList<>();
        }
        List<SocialMediaWork> socialMediaWorks = this.baseMapper.groupByUserIdAndPlatform(userIds);
        return BeanUtil.copyToList(socialMediaWorks, SocialMediaWorkVo.class);
    }


    public Map<String, SocialMediaWork> groupByAccountId(Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new HashMap<>();
        }
        String collect = userIds.stream().map(i -> "'" + i + "'").collect(Collectors.joining(","));
        List<SocialMediaWork> socialMediaWorks = this.baseMapper.groupByAccountId(collect);
        if (CollectionUtils.isEmpty(socialMediaWorks)) {
            return new HashMap<>();
        }
        return socialMediaWorks.stream().collect(Collectors.toMap(SocialMediaWork::getAccountId, Function.identity()));
    }

    public void saveOrUpdateBatch(Collection<SocialMediaWork> works) {
        Db.saveOrUpdateBatch(works);
    }

    public IPage<SocialMediaWorkVo> getPurviewPage(SocialMediaWorkQueryDto queryDto) {
        return this.baseMapper.doGetPage(queryDto).convert(i -> BeanUtil.copyProperties(i, voClass()));
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

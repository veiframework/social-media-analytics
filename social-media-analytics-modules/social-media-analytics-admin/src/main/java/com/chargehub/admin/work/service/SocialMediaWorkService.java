package com.chargehub.admin.work.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.admin.groupuser.dto.GroupUserQueryDto;
import com.chargehub.admin.groupuser.service.GroupUserService;
import com.chargehub.admin.groupuser.vo.GroupUserVo;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.dto.SocialMediaWorkDto;
import com.chargehub.admin.work.dto.SocialMediaWorkQueryDto;
import com.chargehub.admin.work.mapper.SocialMediaWorkMapper;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
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
    private GroupUserService groupUserService;

    protected SocialMediaWorkService(SocialMediaWorkMapper baseMapper) {
        super(baseMapper);
    }


    @SuppressWarnings("unchecked")
    public IPage<SocialMediaWorkVo> getPurviewPage(SocialMediaWorkQueryDto queryDto, LoginUser loginUser) {
        Set<String> roles = loginUser.getRoles();
        String userid = loginUser.getUserid() + "";
        GroupUserQueryDto groupUserQueryDto = new GroupUserQueryDto();
        groupUserQueryDto.setParentUserId(userid);
        List<GroupUserVo> all = (List<GroupUserVo>) groupUserService.getAll(groupUserQueryDto);
        Set<String> userIds = queryDto.getUserId();
        if (!loginUser.isAdmin()) {
            userIds.add(userid);
        }
        if (CollectionUtils.isNotEmpty(all)) {
            Set<String> collect = all.stream().map(GroupUserVo::getUserId).collect(Collectors.toSet());
            userIds.addAll(collect);
        }
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

package com.chargehub.admin.groupuser.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import com.chargehub.admin.api.domain.SysUser;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.admin.groupuser.domain.GroupUser;
import com.chargehub.admin.groupuser.dto.GroupUserBatchAddDto;
import com.chargehub.admin.groupuser.dto.GroupUserDto;
import com.chargehub.admin.groupuser.dto.GroupUserQueryDto;
import com.chargehub.admin.groupuser.mapper.GroupUserMapper;
import com.chargehub.admin.groupuser.vo.GroupUserVo;
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
public class GroupUserService extends AbstractZ9CrudServiceImpl<GroupUserMapper, GroupUser> {


    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;

    public GroupUserService(GroupUserMapper baseMapper) {
        super(baseMapper);
    }

    public void batchAdd(GroupUserBatchAddDto dto) {
        String parentUserId = dto.getParentUserId();
        Set<String> userIds = dto.getUserIds();
        List<GroupUser> collect = userIds.stream().map(i -> {
            GroupUser groupUser = new GroupUser();
            groupUser.setUserId(i);
            groupUser.setParentUserId(parentUserId);
            return groupUser;
        }).collect(Collectors.toList());
        this.baseMapper.doSaveExcelData(collect);
    }

    public List<SysUser> getUsers() {
        return this.baseMapper.getUsers();
    }

    public void checkPurview(Set<String> userIds, LoginUser loginUser) {
        Set<String> roles = loginUser.getRoles();
        String userid = loginUser.getUserid() + "";
        GroupUserQueryDto groupUserQueryDto = new GroupUserQueryDto();
        groupUserQueryDto.setParentUserId(userid);
        List<GroupUser> all = this.baseMapper.doGetAll(groupUserQueryDto);
        if (!loginUser.isAdmin()) {
            userIds.add(userid);
        }
        if (CollectionUtils.isNotEmpty(all)) {
            Set<String> collect = all.stream().map(GroupUser::getUserId).collect(Collectors.toSet());
            userIds.addAll(collect);
        }
    }

    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return GroupUserDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return GroupUserVo.class;
    }

    @Override
    public Class<? extends Z9CrudQueryDto<GroupUser>> queryDto() {
        return GroupUserQueryDto.class;
    }

    @Override
    public String excelName() {
        return "组员列表";
    }
}

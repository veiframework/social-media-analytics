package com.chargehub.admin.groupuser.mapper;

import com.chargehub.admin.api.domain.SysUser;
import com.chargehub.admin.groupuser.domain.GroupUser;
import com.chargehub.common.security.template.mybatis.Z9MpCrudMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Mapper
public interface GroupUserMapper extends Z9MpCrudMapper<GroupUser> {


    @Select("select user_id,nick_name from sys_user where del_flag = '0' and user_id not in (select user_id from group_user)")
    List<SysUser> getUsers();


    @Select("select id,parent_user_id,user_id,id_path from group_user where FIND_IN_SET(#{userid}, id_path)")
    List<GroupUser> getRelativeUsers(String userid);

}

package com.chargehub.biz.appuser.mapper;


import com.chargehub.biz.appuser.domain.AppUser;
import com.chargehub.common.security.template.mybatis.Z9MpCrudMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 * 
 * @author system
 * @since 2024-03-21
 */
@Mapper
public interface AppUserMapper extends Z9MpCrudMapper<AppUser> {

}
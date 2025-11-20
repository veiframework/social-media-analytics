package com.chargehub.admin.work.mapper;

import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.common.security.template.mybatis.Z9MpCrudMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Mapper
public interface SocialMediaWorkMapper extends Z9MpCrudMapper<SocialMediaWork> {


    List<SocialMediaWork> groupByAccountId(String userIds);


    List<SocialMediaWork> groupByUserIdAndPlatform(String userIds);

}

package com.chargehub.admin.work.mapper;

import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.common.security.template.mybatis.Z9MpCrudMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Mapper
public interface SocialMediaWorkMapper extends Z9MpCrudMapper<SocialMediaWork> {

    default List<SocialMediaWork> groupByAccountId(Collection<String> userIds, Set<String> ascFields, Set<String> descFields) {
        if (userIds != null && userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return this.groupByAccountId0(userIds, ascFields, descFields);
    }

    default List<SocialMediaWork> groupByUserIdAndPlatform(@Param("userIds") Collection<String> userIds) {
        if (userIds != null && userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return this.groupByUserIdAndPlatform0(userIds);
    }

    List<SocialMediaWork> groupByAccountId0(@Param("userIds") Collection<String> userIds, @Param("ascFields") Set<String> ascFields, @Param("descFields") Set<String> descFields);


    List<SocialMediaWork> groupByUserIdAndPlatform0(@Param("userIds") Collection<String> userIds);

}

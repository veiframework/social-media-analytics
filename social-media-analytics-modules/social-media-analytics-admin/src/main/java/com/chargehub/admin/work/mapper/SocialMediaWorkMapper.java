package com.chargehub.admin.work.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    default IPage<SocialMediaWork> groupByAccountId(Page<SocialMediaWork> page, Collection<String> userIds, Set<String> ascFields, Set<String> descFields, String tenantId) {
        if (userIds != null && userIds.isEmpty()) {
            return new Page<>();
        }
        return this.groupByAccountId0(page, userIds, ascFields, descFields, tenantId);
    }

    default List<SocialMediaWork> groupByUserIdAndPlatform(@Param("userIds") Collection<String> userIds, @Param("tenantId") String tenantId) {
        if (userIds != null && userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return this.groupByUserIdAndPlatform0(userIds, tenantId);
    }

    IPage<SocialMediaWork> groupByAccountId0(Page<SocialMediaWork> page,
                                             @Param("userIds") Collection<String> userIds,
                                             @Param("ascFields") Set<String> ascFields,
                                             @Param("descFields") Set<String> descFields,
                                             @Param("tenantId") String tenantId);


    List<SocialMediaWork> groupByUserIdAndPlatform0(@Param("userIds") Collection<String> userIds,
                                                    @Param("tenantId") String tenantId);

}

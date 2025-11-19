package com.chargehub.common.datascope.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.chargehub.common.datascope.domain.Z9Purview;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Zhanghaowei
 * @since 2023-12-14 13:10
 */
@Mapper
@Repository
public interface Z9PurviewMapper extends BaseMapper<Z9Purview> {

    /**
     * 查询
     *
     * @return
     */
    default LambdaQueryChainWrapper<Z9Purview> lambdaQuery() {
        return new LambdaQueryChainWrapper<>(this);
    }

    /**
     * 更新
     *
     * @return
     */
    default LambdaUpdateChainWrapper<Z9Purview> lambdaUpdate() {
        return new LambdaUpdateChainWrapper<>(this);
    }

    default Set<String> getNotOwnerPurviewId(Set<String> purviewIds, String purviewType) {
        return this.lambdaQuery().notIn(CollectionUtils.isNotEmpty(purviewIds), Z9Purview::getPurviewId, purviewIds)
                .eq(Z9Purview::getPurviewType, purviewType)
                .list().stream().map(Z9Purview::getPurviewId).collect(Collectors.toSet());
    }

    /**
     * 根据权限树类型获取权限
     *
     * @param purviewType
     * @param ownerIds
     * @return
     */
    default Set<String> getCurrentUserPurview(Set<String> ownerIds, String purviewType) {
        return this.lambdaQuery()
                .select(Z9Purview::getPurviewId)
                .in(CollectionUtils.isNotEmpty(ownerIds), Z9Purview::getOwnerId, ownerIds)
                .in(Z9Purview::getPurviewType, purviewType, StringPool.ASTERISK)
                .groupBy(Z9Purview::getPurviewId)
                .list().stream().map(Z9Purview::getPurviewId).collect(Collectors.toSet());
    }


    default Map<String, List<String>> getPurviewIdsByOwnerId(Set<String> purviewType, Set<String> ownerIds) {
        if (CollectionUtils.isEmpty(ownerIds)) {
            return new HashMap<>();
        }
        if (CollectionUtils.isEmpty(purviewType)) {
            return new HashMap<>();
        }
        return this.lambdaQuery().select(Z9Purview::getPurviewId, Z9Purview::getOwnerId)
                .in(Z9Purview::getPurviewType, purviewType)
                .in(Z9Purview::getOwnerId, ownerIds)
                .list().stream().collect(Collectors.groupingBy(Z9Purview::getOwnerId, Collectors.mapping(Z9Purview::getPurviewId, Collectors.toList())));
    }

    /**
     * 通过数据权限反查拥有权限的id
     *
     * @param purviewType
     * @param purviewIds
     * @return
     */
    default Map<String, List<String>> getOwnerIdByPurviewIds(Set<String> purviewType, Set<String> purviewIds) {
        if (CollectionUtils.isEmpty(purviewIds)) {
            return new HashMap<>();
        }
        if (CollectionUtils.isEmpty(purviewType)) {
            return new HashMap<>();
        }
        return this.lambdaQuery().select(Z9Purview::getPurviewId, Z9Purview::getOwnerId)
                .in(Z9Purview::getPurviewType, purviewType)
                .in(Z9Purview::getPurviewId, purviewIds)
                .list().stream().collect(Collectors.groupingBy(Z9Purview::getPurviewId, Collectors.mapping(Z9Purview::getOwnerId, Collectors.toList())));
    }

    default Map<String, Integer> getTreeLevel(List<String> ids, String ownerId, String purviewType) {
        if (CollectionUtils.isEmpty(ids)) {
            return new HashMap<>(16);
        }
        //有重复的区域id
        return this.lambdaQuery()
                .select(Z9Purview::getPurviewId, Z9Purview::getTreeLevel)
                .eq(Z9Purview::getOwnerId, ownerId)
                .eq(Z9Purview::getPurviewType, purviewType)
                .in(Z9Purview::getPurviewId, ids)
                .groupBy(Z9Purview::getPurviewId)
                .list().stream().collect(Collectors.toMap(Z9Purview::getPurviewId, Z9Purview::getTreeLevel));
    }

    /**
     * 删除
     *
     * @param ownerId
     * @param purviewType
     */
    default void delete(String ownerId, String purviewType) {
        this.lambdaUpdate().eq(Z9Purview::getPurviewType, purviewType)
                .eq(Z9Purview::getOwnerId, ownerId)
                .remove();
    }
}

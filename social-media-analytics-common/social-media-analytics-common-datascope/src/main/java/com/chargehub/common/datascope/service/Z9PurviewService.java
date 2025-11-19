package com.chargehub.common.datascope.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.common.datascope.domain.Z9Purview;
import com.chargehub.common.datascope.domain.Z9Selector;
import com.chargehub.common.datascope.domain.vo.PurviewTypeCheckedVo;
import com.chargehub.common.datascope.domain.vo.Z9PurviewCreateOrUpdateVo;
import com.chargehub.common.datascope.domain.vo.Z9PurviewTreeVo;
import com.chargehub.common.security.utils.SecurityUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Zhanghaowei
 * @since 2023-12-14 13:11
 */
public interface Z9PurviewService extends IService<Z9Purview> {

    /**
     * 获取数据权限选项
     *
     * @param purviewTypes
     * @return
     */
    List<Z9Selector> getSelector(Set<String> purviewTypes);


    /**
     * 获取权限树
     *
     * @param ownerId
     * @param purviewType
     * @return
     */
    Z9PurviewTreeVo getPurviewTrees(String ownerId, String purviewType);

    /**
     * 添加权限
     *
     * @param createOrUpdateVo
     */
    void addPurview(Z9PurviewCreateOrUpdateVo createOrUpdateVo);

    /**
     * 添加权限
     *
     * @param createOrUpdateVo
     */
    void addPurview0(Z9PurviewCreateOrUpdateVo createOrUpdateVo);

    /**
     * 获取被选中的权限类型
     *
     * @param ownerId
     * @return
     */
    PurviewTypeCheckedVo getCheckedPurviewType(String ownerId);

    /**
     * 获取用户的数据权限ids
     *
     * @param purviewTypes
     * @return
     */
    default List<String> getPurviewIds(Set<String> purviewTypes) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Set<String> ownerIds = new HashSet<>();
        if (loginUser != null) {
            ownerIds = loginUser.ownerIds();
        } else {
            ownerIds.add("unknown");
        }
        return getPurviewIds(purviewTypes, ownerIds);
    }


    /**
     * 获取用户的数据权限ids
     *
     * @param ownerIds
     * @param purviewTypes
     * @return
     */
    List<String> getPurviewIds(Set<String> purviewTypes, Set<String> ownerIds);


    /**
     * 获取用户的数据权限ids
     *
     * @param ownerId
     * @param purviewType
     * @return
     */
    List<String> getPurviewIds(String purviewType, String ownerId);


    /**
     * 获取用户的数据权限ids
     *
     * @param purviewTypes
     * @param ownerId
     * @return
     */
    List<String> getPurviewIds(Set<String> purviewTypes, String ownerId);

    Map<String, Set<String>> getPurviewMap(Set<String> purviewTypes);

    /**
     * 获取拥有权限的拥有者id
     *
     * @param purviewType
     * @param purviewIds
     * @return
     */
    Map<String, List<String>> getOwnerIdByPurviewIds(String purviewType, Set<String> purviewIds);

    /**
     * 获取拥有者的权限id
     *
     * @param purviewType
     * @param ownerIds
     * @return
     */
    Map<String, List<String>> getPurviewIdsByOwnerId(String purviewType, Set<String> ownerIds);

}

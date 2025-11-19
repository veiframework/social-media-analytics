package com.chargehub.common.datascope.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.common.core.constant.PurviewConstants;
import com.chargehub.common.datascope.domain.*;
import com.chargehub.common.datascope.domain.vo.PurviewTypeCheckedVo;
import com.chargehub.common.datascope.domain.vo.Z9PurviewCreateOrUpdateVo;
import com.chargehub.common.datascope.domain.vo.Z9PurviewTreeVo;
import com.chargehub.common.datascope.domain.vo.Z9PurviewVo;
import com.chargehub.common.datascope.mapper.Z9PurviewMapper;
import com.chargehub.common.security.template.domain.Z9CrudQueryCondition;
import com.chargehub.common.security.template.event.PurviewQueryEvent;
import com.chargehub.common.security.utils.SecurityUtils;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zhanghaowei
 * @since 2023-12-14 13:12
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class Z9PurviewServiceImpl extends ServiceImpl<Z9PurviewMapper, Z9Purview> implements Z9PurviewService {


    private List<PurviewTreeFactory> purviewTreeFactories = new ArrayList<>();

    public Z9PurviewServiceImpl(@Autowired(required = false) List<PurviewTreeFactory> purviewTreeFactories) {
        if (CollectionUtils.isEmpty(purviewTreeFactories)) {
            return;
        }
        this.purviewTreeFactories = purviewTreeFactories;
    }

    @Override
    public List<Z9Selector> getSelector(Set<String> purviewTypes) {
        if (CollectionUtils.isEmpty(purviewTypes)) {
            return purviewTreeFactories.stream().map(i -> new Z9Selector(i.purviewTypeDesc(), i.purviewType())).collect(Collectors.toList());
        }
        return purviewTreeFactories.stream().filter(i -> purviewTypes.contains(i.purviewType()))
                .map(i -> new Z9Selector(i.purviewTypeDesc(), i.purviewType())).collect(Collectors.toList());
    }

    @Override
    public Z9PurviewTreeVo getPurviewTrees(String ownerId, String purviewType) {
        Assert.notBlank(ownerId);
        Assert.notBlank(purviewType);
        if (CollectionUtils.isEmpty(purviewTreeFactories)) {
            return new Z9PurviewTreeVo();
        }
        PurviewTreeFactory treeFactory = purviewTreeFactories.stream()
                .filter(purviewTreeFactory -> purviewTreeFactory.purviewType().equals(purviewType))
                .findFirst().orElse(null);
        if (treeFactory == null) {
            return new Z9PurviewTreeVo();
        }
        List<PurviewTree> checkedList = new ArrayList<>();
        List<PurviewTree> collect;
        Set<String> ownerPurviewIds = this.baseMapper.getCurrentUserPurview(Stream.of(ownerId).collect(Collectors.toSet()), purviewType);
        LoginUser user = SecurityUtils.getLoginUser();
        if (user == null) {
            return new Z9PurviewTreeVo();
        }
        //巡检员权限类型需要查询不可选择项
        Set<String> purviewIds = purviewType.equals(PurviewConstants.INSPECTOR_STATION_PURVIEW) ? this.baseMapper.getNotOwnerPurviewId(ownerPurviewIds, purviewType) : new HashSet<>();
        //目标id是否用户全部数据权限
        boolean ownerContainAllPurview = ownerPurviewIds.contains(StringPool.ASTERISK);

        List<PurviewTree> purviewList = treeFactory.getPurviewList();
        List<String> ids = purviewList.stream().map(PurviewTree::getId).collect(Collectors.toList());
        Map<String, Integer> treeLevel = this.baseMapper.getTreeLevel(ids, ownerId, purviewType);
        collect = TreeModel.toTree(purviewList, purviewTree -> {
            boolean containsKey = ownerPurviewIds.contains(purviewTree.getId());
            purviewTree.setChecked(containsKey);
            if (containsKey) {
                PurviewTree copy = BeanUtil.copyProperties(purviewTree, PurviewTree.class);
                copy.setTreeLevel(treeLevel.getOrDefault(purviewTree.getId(), 0));
                checkedList.add(copy);
            }
            purviewTree.setUnavailable(purviewIds.contains(purviewTree.getId()));
        }, i -> BooleanUtil.toInt(!i.getChecked()));
        collect.sort(Comparator.comparingLong((ToLongFunction<PurviewTree>) value -> {
            if (CollectionUtils.isEmpty(value.getChildren())) {
                return 0;
            } else {
                return value.getChildren().stream().filter(PurviewTree::getChecked).count();
            }
        }).reversed());
        return new Z9PurviewTreeVo(collect, checkedList, ownerContainAllPurview);
    }

    @Override
    public void addPurview(Z9PurviewCreateOrUpdateVo createOrUpdateVo) {
        List<Z9PurviewVo> purviewList = createOrUpdateVo.getPurviewList();
        if (CollectionUtils.isNotEmpty(purviewList)) {
            String purviewType = createOrUpdateVo.getPurviewType();
            if (purviewType.equals(PurviewConstants.USER_REGION_PURVIEW) || purviewType.equals(PurviewConstants.GROUP_USER_REGION_PURVIEW)) {
                Z9PurviewVo z9PurviewVo = purviewList.get(0);
                Integer treeLevel = z9PurviewVo.getTreeLevel();
                if (treeLevel.equals(1)) {
                    PurviewTreeFactory treeFactory = purviewTreeFactories.stream()
                            .filter(purviewTreeFactory -> purviewTreeFactory.purviewType().equals(purviewType))
                            .findFirst().orElse(null);
                    List<PurviewTree> originList = treeFactory.getPurviewList();
                    originList.forEach(i -> i.setParentId(i.getRemark()));
                    List<PurviewTree> treeList = TreeModel.toTree(originList);
                    Map<String, PurviewTree> collect = treeList.stream().collect(Collectors.toMap(PurviewTree::getId, v -> v));
                    List<Z9PurviewVo> findPurview = new ArrayList<>();
                    for (Z9PurviewVo purviewVo : purviewList) {
                        PurviewTree purviewTree = collect.get(purviewVo.getPurviewId());
                        if (purviewTree == null) {
                            continue;
                        }
                        List<PurviewTree> flat = TreeModel.flat(purviewTree.getChildren());
                        List<Z9PurviewVo> childrenList = flat.stream().map(i -> {
                            Z9PurviewVo copy = new Z9PurviewVo();
                            copy.setPurviewId(i.getId());
                            return copy;
                        }).collect(Collectors.toList());
                        findPurview.addAll(childrenList);
                    }
                    purviewList.addAll(findPurview);
                } else if (treeLevel.equals(2)) {
                    PurviewTreeFactory treeFactory = purviewTreeFactories.stream()
                            .filter(purviewTreeFactory -> purviewTreeFactory.purviewType().equals(purviewType))
                            .findFirst().orElse(null);
                    List<PurviewTree> treeList = treeFactory.getPurviewList();
                    Map<String, List<PurviewTree>> cityMap = treeList.stream().filter(i -> StringUtils.isNotBlank(i.getRemark())).collect(Collectors.groupingBy(PurviewTree::getRemark));
                    Map<String, PurviewTree> collect = treeList.stream().collect(Collectors.toMap(PurviewTree::getId, v -> v));
                    List<Z9PurviewVo> findPurview = new ArrayList<>();
                    for (Z9PurviewVo purviewVo : purviewList) {
                        String purviewId = purviewVo.getPurviewId();
                        PurviewTree purviewTree = collect.get(purviewId);
                        if (purviewTree == null) {
                            continue;
                        }
                        String parentId = purviewTree.getRemark();
                        List<Z9PurviewVo> childrenList = cityMap.getOrDefault(purviewId, new ArrayList<>())
                                .stream().map(i -> {
                                    Z9PurviewVo copy = new Z9PurviewVo();
                                    copy.setPurviewId(i.getId());
                                    return copy;
                                }).collect(Collectors.toList());
                        PurviewTree parentTree = collect.get(parentId);
                        if (parentTree != null) {
                            Z9PurviewVo parentPurview = new Z9PurviewVo();
                            parentPurview.setPurviewId(parentTree.getId());
                            childrenList.add(parentPurview);
                        }
                        findPurview.addAll(childrenList);
                    }
                    purviewList.addAll(findPurview);
                } else if (treeLevel.equals(3)) {
                    PurviewTreeFactory treeFactory = purviewTreeFactories.stream()
                            .filter(purviewTreeFactory -> purviewTreeFactory.purviewType().equals(purviewType))
                            .findFirst().orElse(null);
                    List<PurviewTree> treeList = treeFactory.getPurviewList();
                    List<Z9PurviewVo> findPurview = new ArrayList<>();
                    Map<String, PurviewTree> collect = treeList.stream().collect(Collectors.toMap(PurviewTree::getId, v -> v));
                    for (Z9PurviewVo purviewVo : purviewList) {
                        String purviewId = purviewVo.getPurviewId();
                        PurviewTree purviewTree = collect.get(purviewId);
                        if (purviewTree == null) {
                            continue;
                        }
                        String remark = purviewTree.getRemark();
                        PurviewTree city = collect.get(remark);
                        PurviewTree province = collect.get(city.getRemark());
                        Stream.of(city, province).forEach(i -> {
                            Z9PurviewVo copy = new Z9PurviewVo();
                            copy.setPurviewId(i.getId());
                            findPurview.add(copy);
                        });
                    }
                    purviewList.addAll(findPurview);
                }
            }

        }
        this.addPurview0(createOrUpdateVo);
    }

    @Override
    public void addPurview0(Z9PurviewCreateOrUpdateVo createOrUpdateVo) {
        String purviewType = createOrUpdateVo.getPurviewType();
        String ownerId = createOrUpdateVo.getOwnerId();
        if (chargehubAllPurview(createOrUpdateVo, purviewType, ownerId)) {
            return;
        }
        this.baseMapper.delete(ownerId, purviewType);
        List<Z9PurviewVo> purviewVos = createOrUpdateVo.getPurviewList();
        //如果选择了全部则自动追加"*"
        if (createOrUpdateVo.isAll()) {
            Z9PurviewVo allPurview = new Z9PurviewVo();
            allPurview.setPurviewId(StringPool.ASTERISK);
            purviewVos.add(allPurview);
        }
        if (CollectionUtils.isEmpty(purviewVos)) {
            return;
        }
        List<Z9Purview> z9PurviewList = BeanUtil.copyToList(purviewVos, Z9Purview.class);
        z9PurviewList.forEach(i -> {
            i.setPurviewType(purviewType);
            i.setOwnerId(ownerId);
        });
        this.saveBatch(z9PurviewList);
    }

    private boolean chargehubAllPurview(Z9PurviewCreateOrUpdateVo createOrUpdateVo, String purviewType, String ownerId) {
        //区域,站点二选一
        boolean userPurview = Arrays.asList(PurviewConstants.USER_REGION_PURVIEW, PurviewConstants.USER_STATION_PURVIEW).contains(purviewType);
        boolean groupUserPurview = Arrays.asList(PurviewConstants.GROUP_USER_REGION_PURVIEW, PurviewConstants.GROUP_USER_STATION_PURVIEW).contains(purviewType);
        if (userPurview) {
            this.baseMapper.lambdaUpdate().eq(Z9Purview::getOwnerId, ownerId).in(Z9Purview::getPurviewType, PurviewConstants.USER_REGION_PURVIEW, PurviewConstants.USER_STATION_PURVIEW).remove();
            if (createOrUpdateVo.isAll()) {
                Z9Purview purview0 = new Z9Purview();
                purview0.setOwnerId(ownerId);
                purview0.setPurviewId(StringPool.ASTERISK);
                purview0.setPurviewType(PurviewConstants.USER_REGION_PURVIEW);
                Z9Purview purview1 = new Z9Purview();
                purview1.setOwnerId(ownerId);
                purview1.setPurviewId(StringPool.ASTERISK);
                purview1.setPurviewType(PurviewConstants.USER_STATION_PURVIEW);
                this.saveBatch(Stream.of(purview0, purview1).collect(Collectors.toList()));
                return true;
            }
        }
        if (groupUserPurview) {
            this.baseMapper.lambdaUpdate().eq(Z9Purview::getOwnerId, ownerId).in(Z9Purview::getPurviewType, PurviewConstants.GROUP_USER_REGION_PURVIEW, PurviewConstants.GROUP_USER_STATION_PURVIEW).remove();
            if (createOrUpdateVo.isAll()) {
                Z9Purview purview0 = new Z9Purview();
                purview0.setOwnerId(ownerId);
                purview0.setPurviewId(StringPool.ASTERISK);
                purview0.setPurviewType(PurviewConstants.GROUP_USER_REGION_PURVIEW);
                Z9Purview purview1 = new Z9Purview();
                purview1.setOwnerId(ownerId);
                purview1.setPurviewId(StringPool.ASTERISK);
                purview1.setPurviewType(PurviewConstants.GROUP_USER_STATION_PURVIEW);
                this.saveBatch(Stream.of(purview0, purview1).collect(Collectors.toList()));
                return true;
            }
        }
        return false;
    }

    @Override
    public PurviewTypeCheckedVo getCheckedPurviewType(String ownerId) {
        Z9Purview purview = this.baseMapper.lambdaQuery().eq(Z9Purview::getOwnerId, ownerId).last("limit 1").one();
        if (purview == null) {
            return new PurviewTypeCheckedVo(false, null);
        }
        boolean hasAllPurview = purview.getPurviewId().equals(StringPool.ASTERISK);
        return new PurviewTypeCheckedVo(hasAllPurview, purview.getPurviewType());
    }

    @Override
    public List<String> getPurviewIds(Set<String> purviewTypes, Set<String> ownerIds) {
        Assert.notEmpty(purviewTypes, "数据权限类型不得为空");
        Assert.notEmpty(ownerIds, "数据权限拥有者不得为空");
        Set<String> purviewTypeSets = new HashSet<>(purviewTypes);
        purviewTypeSets.add(StringPool.ASTERISK);
        long count = this.baseMapper.lambdaQuery()
                .in(Z9Purview::getOwnerId, ownerIds)
                .in(Z9Purview::getPurviewType, purviewTypeSets)
                .eq(Z9Purview::getPurviewId, StringPool.ASTERISK).count();
        if (count > 0) {
            return null;
        }
        purviewTypeSets.remove(StringPool.ASTERISK);
        return this.baseMapper.lambdaQuery().select(Z9Purview::getPurviewId)
                .in(Z9Purview::getOwnerId, ownerIds)
                .in(Z9Purview::getPurviewType, purviewTypeSets).list()
                .stream().map(Z9Purview::getPurviewId).collect(Collectors.toList());
    }


    /**
     * 根据数据权限类型和拥有者id获取数据权限id列表
     *
     * @param purviewType
     * @param ownerId
     * @return
     */
    @Override
    public List<String> getPurviewIds(String purviewType, String ownerId) {

        return getPurviewIds(new HashSet<>(Arrays.asList(purviewType)), new HashSet<>(Arrays.asList(ownerId)));
    }


    /**
     * 根据数据权限类型和拥有者id获取数据权限id列表
     *
     * @param purviewTypes
     * @param ownerId
     * @return
     */
    @Override
    public List<String> getPurviewIds(Set<String> purviewTypes, String ownerId) {

        return getPurviewIds(purviewTypes, new HashSet<>(Arrays.asList(ownerId)));
    }

    @Override
    public Map<String, Set<String>> getPurviewMap(Set<String> purviewTypes) {
        Assert.notEmpty(purviewTypes, "数据权限类型不得为空");
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Set<String> ownerIds = new HashSet<>();
        if (loginUser != null) {
            ownerIds = loginUser.ownerIds();
        } else {
            ownerIds.add("unknown");
        }
        Set<String> purviewTypeSets = new HashSet<>(purviewTypes);
        purviewTypeSets.add(StringPool.ASTERISK);
        long count = this.baseMapper.lambdaQuery()
                .in(Z9Purview::getOwnerId, ownerIds)
                .in(Z9Purview::getPurviewType, purviewTypeSets)
                .eq(Z9Purview::getPurviewId, StringPool.ASTERISK).count();
        if (count > 0) {
            return null;
        }
        purviewTypeSets.remove(StringPool.ASTERISK);
        Map<String, Set<String>> collect = this.baseMapper.lambdaQuery().select(Z9Purview::getPurviewId, Z9Purview::getPurviewType)
                .in(Z9Purview::getOwnerId, ownerIds)
                .in(Z9Purview::getPurviewType, purviewTypeSets).list()
                .stream().collect(Collectors.groupingBy(Z9Purview::getPurviewType, Collectors.mapping(Z9Purview::getPurviewId, Collectors.toSet())));
        if (MapUtil.isEmpty(collect)) {
            //没有数据权限则强制查询为无数据
            return purviewTypes.stream().collect(Collectors.toMap(k -> k, v -> Sets.newHashSet("null")));
        }
        return collect;
    }

    @Override
    public Map<String, List<String>> getOwnerIdByPurviewIds(String purviewType, Set<String> purviewIds) {
        return this.baseMapper.getOwnerIdByPurviewIds(Sets.newHashSet(purviewType), purviewIds);
    }

    @Override
    public Map<String, List<String>> getPurviewIdsByOwnerId(String purviewType, Set<String> ownerIds) {
        return this.baseMapper.getPurviewIdsByOwnerId(Sets.newHashSet(purviewType), ownerIds);
    }

    @EventListener
    public void purviewChangeEventListener(PurviewDataChangeEvent changeEvent) {
        Set<String> purviewIds = changeEvent.getPurviewIds();
        Set<String> purviewTypes = changeEvent.getPurviewTypes();
        Set<String> parentIds = changeEvent.getParentIds();
        boolean delete = changeEvent.isDelete();
        if (CollectionUtils.isEmpty(purviewTypes) || CollectionUtils.isEmpty(parentIds) || CollectionUtils.isEmpty(purviewIds)) {
            return;
        }
        if (delete) {
            this.baseMapper.lambdaUpdate()
                    .in(Z9Purview::getPurviewType, purviewTypes)
                    .in(Z9Purview::getPurviewId, purviewIds)
                    .remove();
        } else {
            List<Z9Purview> parentPurviewList = this.baseMapper.lambdaQuery()
                    .in(Z9Purview::getPurviewType, purviewTypes)
                    .in(Z9Purview::getPurviewId, parentIds)
                    .list();

            Set<String> purviewTypeSet = new HashSet<>();
            Set<String> ownerIdSet = new HashSet<>();

            List<Z9Purview> childrenPurviewList = parentPurviewList.stream().flatMap(i -> purviewIds.stream().map(purviewId -> {
                purviewTypeSet.add(i.getPurviewType());
                ownerIdSet.add(i.getOwnerId());
                Z9Purview z9Purview = new Z9Purview();
                z9Purview.setOwnerId(i.getOwnerId());
                z9Purview.setPurviewType(i.getPurviewType());
                z9Purview.setPurviewId(purviewId);
                return z9Purview;
            })).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(childrenPurviewList)) {
                return;
            }
            if (CollectionUtils.isNotEmpty(purviewTypeSet) && CollectionUtils.isNotEmpty(ownerIdSet)) {
                List<Z9Purview> list = this.baseMapper.lambdaQuery()
                        .in(Z9Purview::getPurviewType, purviewTypeSet)
                        .in(Z9Purview::getOwnerId, ownerIdSet)
                        .in(Z9Purview::getPurviewId, purviewIds).list();
                Set<String> existSet = list.stream().map(i -> i.getPurviewType() + i.getOwnerId() + i.getPurviewId()).collect(Collectors.toSet());
                childrenPurviewList.removeIf(i -> {
                    String key = i.getPurviewType() + i.getOwnerId() + i.getPurviewId();
                    return existSet.contains(key);
                });
            }
            if (CollectionUtils.isEmpty(childrenPurviewList)) {
                return;
            }
            this.saveBatch(childrenPurviewList);
        }
    }

    @EventListener
    public void addPurviewQueryCondition(PurviewQueryEvent<?> event) {
        List<? extends Z9CrudQueryCondition<?>> purviewConditions = event.getPurviewConditions();
        if (CollectionUtils.isEmpty(purviewConditions)) {
            return;
        }
        LambdaQueryWrapper qw = event.getQw();
        Set<String> purviewTypes = purviewConditions.stream().flatMap(i -> i.getPurviewTypes().stream()).collect(Collectors.toSet());
        Map<String, Set<String>> purviewMap = this.getPurviewMap(purviewTypes);
        if (purviewMap == null) {
            //全部数据权限
            return;
        }
        for (Z9CrudQueryCondition<?> purviewCondition : purviewConditions) {
            List<String> fieldPurviewTypes = purviewCondition.getPurviewTypes();
            Set<String> collect = fieldPurviewTypes.stream().flatMap(i -> purviewMap.getOrDefault(i, new HashSet<>()).stream()).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(collect)) {
                qw.in(purviewCondition.getField(), collect);
            }
        }

    }

}

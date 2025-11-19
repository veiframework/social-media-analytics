package com.chargehub.biz.region.service.impl;


import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import com.chargehub.biz.region.domain.RegionEntity;
import com.chargehub.biz.region.domain.RegionIds;
import com.chargehub.biz.region.domain.dto.*;
import com.chargehub.biz.region.domain.vo.RegionAppVo;
import com.chargehub.biz.region.domain.vo.RegionVo;
import com.chargehub.biz.region.mapper.RegionIdsMapper;
import com.chargehub.biz.region.mapper.RegionMapper;
import com.chargehub.biz.region.service.RegionService;
import com.chargehub.common.core.constant.CacheConstants;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.event.Z9BeforeCreateEvent;
import com.chargehub.common.security.template.event.Z9CreateEvent;
import com.chargehub.common.security.template.event.Z9UpdateEvent;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zhanghaowei
 * @date 2021-11-05 14:42:56
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RegionServiceImpl extends AbstractZ9CrudServiceImpl<RegionMapper, RegionEntity> implements RegionService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;

    @Autowired
    private RegionIdsMapper regionIdsMapper;

    protected RegionServiceImpl(RegionMapper baseMapper) {
        super(baseMapper);
    }


    @Override
    public String excelName() {
        return "区域列表";
    }

    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return RegionDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return RegionVo.class;
    }

    @Override
    public RegionAppVo getAppRegionList(RegionAppQueryDto queryDto) {
        RegionQueryDto regionQueryDto = new RegionQueryDto();
        regionQueryDto.setDisablePurview(true);
        regionQueryDto.setLevel(queryDto.getLevel());
        regionQueryDto.setParentId(queryDto.getParentId());
        List<RegionVo> all = (List<RegionVo>) this.getAll(regionQueryDto);
        RegionAppVo regionAppVo = new RegionAppVo();
        if (StringUtils.isNotBlank(queryDto.getLatitude()) && StringUtils.isNotBlank(queryDto.getLongitude())) {
            RegionVo currentRegion = this.baseMapper.getCurrentRegion(queryDto);
            regionAppVo.setCurrentRegion(currentRegion);
        }
        regionAppVo.setAll(all);
        return regionAppVo;
    }

    @Cacheable(value = CacheConstants.REGION_KEY, key = "'map'")
    @Override
    public Map<String, String> getRegionMap() {
        List<RegionEntity> list = this.baseMapper.lambdaQuery().eq(RegionEntity::getVisible, 1).list();
        return list.stream().collect(Collectors.toMap(RegionEntity::getId, RegionEntity::getLabel));
    }

    @Override
    public List<Map<String, Object>> listMaps() {
        LambdaQueryWrapper<RegionEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(RegionEntity::getVisible, 1);
        this.baseMapper.selectMaps(qw);
        return super.listMaps();
    }

    @CacheEvict(value = CacheConstants.REGION_KEY, allEntries = true)
    @EventListener
    public void beforeRegionAdd(Z9BeforeCreateEvent<RegionEntity> event) {
        List<RegionEntity> data = event.getData();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        for (RegionEntity datum : data) {
            String id = IdWorker.getIdStr();
            String parentId = datum.getParentId();
            List<String> idPath = new ArrayList<>();
            if (StringUtils.isBlank(parentId)) {
                idPath.add(id);
            } else {
                List<RegionIds> list = this.regionIdsMapper.lambdaQuery().eq(RegionIds::getRegionId, parentId).list();
                Assert.notEmpty(list, "区域父级不存在");
                List<String> parentIds = list.stream().map(RegionIds::getParentId).collect(Collectors.toList());
                idPath.addAll(parentIds);
                idPath.add(id);
            }
            datum.setId(id);
            List<RegionIds> collect = idPath.stream().map(i -> {
                RegionIds regionIds = new RegionIds();
                regionIds.setRegionId(id);
                regionIds.setParentId(i);
                return regionIds;
            }).collect(Collectors.toList());
            this.regionIdsMapper.doSaveExcelData(collect);
        }
    }

    @CacheEvict(value = CacheConstants.REGION_KEY, allEntries = true)
    @EventListener
    public void afterRegionUpdate(Z9UpdateEvent<RegionEntity> event) {
        List<RegionEntity> data = event.getData();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        for (RegionEntity datum : data) {
            String id = datum.getId();
            String parentId = datum.getParentId();
            List<String> idPath = new ArrayList<>();
            if (StringUtils.isBlank(parentId)) {
                idPath.add(id);
            } else {
                List<RegionIds> list = this.regionIdsMapper.lambdaQuery().eq(RegionIds::getRegionId, parentId).list();
                Assert.notEmpty(list, "区域父级不存在");
                List<String> parentIds = list.stream().map(RegionIds::getParentId).collect(Collectors.toList());
                idPath.addAll(parentIds);
                idPath.add(id);
            }
            List<RegionIds> nodeList = this.regionIdsMapper.lambdaQuery().eq(RegionIds::getParentId, id).list();
            Set<String> deleteIds = nodeList.stream().map(RegionIds::getRegionId).collect(Collectors.toSet());
            this.regionIdsMapper.lambdaUpdate().in(RegionIds::getRegionId, deleteIds).remove();
            List<RegionIds> newIds = deleteIds.stream().flatMap(i -> {
                List<String> copy = new ArrayList<>(idPath);
                if (!i.equals(id)) {
                    //排除当前节点
                    copy.add(i);
                }
                return copy.stream().map(j -> {
                    RegionIds regionIds = new RegionIds();
                    regionIds.setRegionId(i);
                    regionIds.setParentId(j);
                    return regionIds;
                });
            }).collect(Collectors.toList());
            this.regionIdsMapper.doSaveExcelData(newIds);
        }
    }


    @EventListener
    public void afterRegionAdd(Z9CreateEvent<RegionEntity> event) {
        List<RegionEntity> data = event.getData();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
//        Set<String> purviewIds = data.stream().map(RegionEntity::getId).filter(Objects::nonNull).collect(Collectors.toSet());
//        Set<String> parentIds = data.stream().map(RegionEntity::getParentId).filter(Objects::nonNull).collect(Collectors.toSet());
//        PurviewDataChangeEvent purviewDataChangeEvent = new PurviewDataChangeEvent();
//        purviewDataChangeEvent.setPurviewIds(purviewIds);
//        purviewDataChangeEvent.setPurviewTypes(Sets.newHashSet(PurviewConstants.USER_REGION_PURVIEW, PurviewConstants.GROUP_USER_REGION_PURVIEW));
//        purviewDataChangeEvent.setParentIds(parentIds);
//        applicationContext.publishEvent(purviewDataChangeEvent);
    }


    @CacheEvict(value = CacheConstants.REGION_KEY, allEntries = true)
    @Override
    public void updateVisible(RegionVisibleDto visibleDto) {
        if (CollectionUtils.isEmpty(visibleDto.getNodeList())) {
            return;
        }
        RegionNode nodeList = visibleDto.getNodeList().get(0);
        String parentId = nodeList.getParentId();
        String id = nodeList.getId();
        boolean visible = BooleanUtil.toBoolean(visibleDto.getVisible());
        Set<String> parentIds = new HashSet<>();
        Set<String> ids = new HashSet<>();
        ids.add(id);
        //查出子集
        List<RegionEntity> list = this.baseMapper.lambdaQuery().eq(RegionEntity::getParentId, id).list();
        Set<String> subIds = list.stream().map(RegionEntity::getId).collect(Collectors.toSet());
        ids.addAll(subIds);
        if (StringUtils.isNotBlank(parentId)) {
            parentIds.add(parentId);
            RegionEntity regionEntity = this.baseMapper.doGetDetailById(parentId);
            Assert.notNull(regionEntity, "地区id不存在");
            if (visible) {
                //如果地区没开通则追加id
                if (regionEntity.getVisible().equals("0")) {
                    ids.add(parentId);
                }
            } else {
                //如果地区都不开通则父级也追加不开通
                Long count = this.baseMapper.lambdaQuery().ne(RegionEntity::getId, id).eq(RegionEntity::getParentId, parentId).eq(RegionEntity::getVisible, 1).count();
                if (count == 0) {
                    ids.add(parentId);
                }
            }
        }

        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        this.baseMapper.lambdaUpdate().set(RegionEntity::getVisible, visible).in(RegionEntity::getId, ids).update();
//        if (visible) {
//            PurviewDataChangeEvent purviewDataChangeEvent = new PurviewDataChangeEvent();
//            purviewDataChangeEvent.setPurviewIds(ids);
//            purviewDataChangeEvent.setPurviewTypes(Sets.newHashSet(PurviewConstants.USER_REGION_PURVIEW, PurviewConstants.GROUP_USER_REGION_PURVIEW));
//            purviewDataChangeEvent.setParentIds(parentIds);
//            applicationContext.publishEvent(purviewDataChangeEvent);
//        }

    }

    @Override
    public List<String> getRegionIdsByCode(String countyCode) {
        List<String> ids = new ArrayList<>();
        RegionEntity county = this.baseMapper.lambdaQuery().eq(RegionEntity::getAreaCode, countyCode).one();
        if (county == null) {
            return ids;
        }
        RegionEntity city = this.baseMapper.lambdaQuery().eq(RegionEntity::getId, county.getParentId()).one();
        RegionEntity province = this.baseMapper.lambdaQuery().eq(RegionEntity::getId, city.getParentId()).one();
        ids.add(province.getId());
        ids.add(city.getId());
        ids.add(county.getId());
        return ids;
    }

    @CacheEvict(value = CacheConstants.REGION_KEY, allEntries = true)
    @Override
    public void deleteByIds(String ids) {
        String[] split = ids.split(",");
        if (ArrayUtils.isEmpty(split)) {
            return;
        }
        List<String> idList = Arrays.stream(split).collect(Collectors.toList());
        List<RegionIds> list = regionIdsMapper.lambdaQuery().in(RegionIds::getParentId, idList).list();
        Set<String> deleteIds = list.stream().map(RegionIds::getRegionId).collect(Collectors.toSet());
        this.baseMapper.lambdaUpdate().in(RegionEntity::getId, deleteIds).remove();
//        this.regionIdsMapper.lambdaUpdate().in(RegionIds::getRegionId, deleteIds).remove();
    }

    @Override
    public List<String> getRegionIds(String regionId) {
        return this.baseMapper.getRegionIds(regionId);
    }

    @Override
    public Map<String, Map<String, String>> getRegionNameMap(List<String> ids) {
        return this.baseMapper.getRegionNameMap(ids);
    }

    @Override
    public List<String> getSubRegionIds(String regionId) {
        return this.baseMapper.getSubRegionIds(regionId);
    }

    @Override
    public String getRegionIdByCode(String code) {
        return this.baseMapper.getRegionIdByCode(code);
    }
}
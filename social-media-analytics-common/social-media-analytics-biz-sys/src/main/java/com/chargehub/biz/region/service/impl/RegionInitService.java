package com.chargehub.biz.region.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.toolkit.Db;

import com.chargehub.biz.region.domain.RegionEntity;
import com.chargehub.biz.region.domain.RegionIds;
import com.chargehub.biz.region.domain.vo.RegionVo;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhanghaowei
 * @date 2024/08/27 11:32
 */
@Component
public class RegionInitService {


    public void addAddList() {
        //读取外部js文件
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(MessageFormat.format("classpath:{0}", "address.js"));
        if (!resource.exists()) {
            return;
        }
        try {
            String content = IoUtil.readUtf8(resource.getInputStream());
            List<CascadeItem> cascadeItems = JSON.parseArray(content, CascadeItem.class);
            List<RegionVo> list = new LinkedList<>();
            toRegion(cascadeItems, list, "", null, new ArrayList<>());
            map(list);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }


    public void toRegion(List<CascadeItem> cascadeItems, List<RegionVo> regionVOS, String parentId, Integer rank, List<String> idPath) {
        if (CollectionUtils.isEmpty(cascadeItems)) {
            return;
        }
        Integer rankC = rank;
        if (rankC != null) {
            rankC++;
        } else {
            rankC = 1;
        }
        for (CascadeItem i : cascadeItems) {
            RegionVo copy = new RegionVo();
            BeanUtil.copyProperties(i, copy);
            postProcess(i, copy);
            copy.setAreaCode(i.getValue());
            copy.setLastStage(false);
            List<CascadeItem> children = i.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                copy.setChildren(new ArrayList<>());
            } else {
                copy.setLastStage(true);
            }
            if (StringUtils.isNotBlank(parentId)) {
                copy.setParentId(parentId);
            }
            List<String> nextIdPath = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(idPath)) {
                nextIdPath.addAll(idPath);
                nextIdPath.add(copy.getId());
            } else {
                nextIdPath.add(copy.getId());
            }
            copy.setIdPath(nextIdPath);
            copy.setLevel(rankC);
            toRegion(children, copy.getChildren(), copy.getId(), copy.getLevel(), nextIdPath);
            regionVOS.add(copy);
        }
    }

    private void postProcess(CascadeItem i, RegionVo copy) {
        copy.setId(IdWorker.getIdStr());
    }


    public void map(List<RegionVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<RegionEntity> regionEntities = BeanUtil.copyToList(list, RegionEntity.class);
        Db.saveOrUpdateBatch(regionEntities);
        List<RegionIds> idPaths = list.stream().flatMap(i -> i.getIdPath().stream().map(j -> {
            RegionIds regionIds = new RegionIds();
            regionIds.setRegionId(i.getId());
            regionIds.setParentId(j);
            return regionIds;
        })).collect(Collectors.toList());
        Db.saveBatch(idPaths);
        for (RegionVo i : list) {
            List<RegionVo> children = i.getChildren();
            map(children);
        }
    }


    @Data
    public static class CascadeItem {

        private List<CascadeItem> children;

        private String value;

        private String label;

    }


}

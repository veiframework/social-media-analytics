package com.chargehub.biz.region.service;


import com.chargehub.biz.region.domain.RegionEntity;
import com.chargehub.biz.region.domain.dto.RegionAppQueryDto;
import com.chargehub.biz.region.domain.dto.RegionVisibleDto;
import com.chargehub.biz.region.domain.vo.RegionAppVo;
import com.chargehub.common.security.template.service.Z9CrudService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * @author Zhanghaowei
 * @date 2021-11-05 14:42:56
 */
public interface RegionService extends Z9CrudService<RegionEntity> {


    /**
     * 区域列表
     *
     * @param queryDto
     * @return
     */
    RegionAppVo getAppRegionList(RegionAppQueryDto queryDto);

    /**
     * 获取全部区域map
     *
     * @return
     */
    Map<String, String> getRegionMap();

    /**
     * 是否可见区域
     *
     * @param visibleDto
     */
    void updateVisible(RegionVisibleDto visibleDto);

    List<String> getRegionIdsByCode(String countyCode);


    List<String> getRegionIds(String regionId);


    Map<String, Map<String, String>> getRegionNameMap(@Param("ids") List<String> ids);

    /**
     * 获取子集区域集合，包含了自身
     *
     * @param regionId regionId
     * @return List<String>
     */
    List<String> getSubRegionIds(String regionId);

    /**
     * 根据code获取区域id
     *
     * @param code
     * @return
     */
    String getRegionIdByCode(String code);
}


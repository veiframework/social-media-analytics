package com.chargehub.biz.region.mapper;


import cn.hutool.core.bean.BeanUtil;

import com.chargehub.biz.region.domain.RegionEntity;
import com.chargehub.biz.region.domain.dto.RegionAppQueryDto;
import com.chargehub.biz.region.domain.vo.RegionVo;
import com.chargehub.common.security.template.mybatis.Z9MpCrudMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Zhanghaowei
 * @date 2021-11-05 14:42:56
 */
@Mapper
public interface RegionMapper extends Z9MpCrudMapper<RegionEntity> {


    /**
     * 获取全部
     *
     * @return
     */
    default List<RegionVo> getRegionAll() {
        return this.lambdaQuery()
                .orderByDesc(RegionEntity::getId)
                .list().stream().map(i -> BeanUtil.copyProperties(i, RegionVo.class))
                .collect(Collectors.toList());
    }

    /**
     * 获取当前区域
     *
     * @param regionAppQueryDto
     * @return
     */
    RegionVo getCurrentRegion(RegionAppQueryDto regionAppQueryDto);



    @Select("SELECT " +
            "  r.id " +
            "FROM " +
            "  charge_region_ids rids " +
            "  INNER JOIN charge_region r ON rids.parent_id = r.id  " +
            "WHERE " +
            "  rids.region_id = #{regionId} " +
            "ORDER BY " +
            "  `level` ASC;")
    List<String> getRegionIds(@Param("regionId") String regionId);


    @MapKey("id")
    @Select("<script> SELECT id, label as name FROM charge_region where id in <foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'> #{item} </foreach> </script>")
    Map<String, Map<String, String>> getRegionNameMap(@Param("ids") List<String> ids);


    /**
     * 获取子集区域集合，包含了自身
     * @param regionId regionId
     * @return List<String>
     */
    @Select("select region_id from charge_region_ids where parent_id = #{regionId};")
    List<String> getSubRegionIds(@Param("regionId") String regionId);


    @Select("select id from charge_region where area_code = #{areaCode};")
    String getRegionIdByCode(@Param("areaCode")String areaCode);
}

package com.chargehub.biz.sys.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 网关启动程序
 * date: 2024/10/30
 *
 * @author TiAmo@13721682347@163.com
 **/
@Mapper
public interface ChargeStationExtMapper {

    @MapKey("id")
    @Select("<script>" + " SELECT id, name FROM charg_station where id in "
            + " <foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'> " +
            " #{item} " +
            "</foreach> </script>"
    )
    Map<Long, Map<String, String>> stationNameMap(@Param("ids") List<Long> ids);

    /**
     * 查询所有流量方站点
     *
     * @return Set<String>
     */
    @Select("select CAST(id as CHAR) as id from charg_station where type = 'user_side' and del_flag = '0'")
    Set<String> allUserSideStation();
}

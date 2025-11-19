package com.chargehub.common.security.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/04/04 10:32
 */
public interface DynamicDictMapper {

    /**
     * SQL查询
     *
     * @param sql 查询SQL
     * @return 列表
     */
    @Select("${sql}")
    Map<String, Object> applySql(@Param("sql") String sql);

    @Select("${sql}")
    List<Map<String,Object>> listMaps(@Param("sql") String sql);
}

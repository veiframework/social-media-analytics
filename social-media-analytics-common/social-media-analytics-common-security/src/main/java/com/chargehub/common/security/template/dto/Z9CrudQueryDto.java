package com.chargehub.common.security.template.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import com.chargehub.common.security.template.domain.Z9CrudQueryCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Zhanghaowei
 * @date 2024/04/03 14:53
 */
public interface Z9CrudQueryDto<E extends Z9CrudEntity> {

    /**
     * 页码
     *
     * @param pageNum
     */
    void setPageNum(Long pageNum);

    /**
     * 分页大小
     *
     * @param pageSize
     */
    void setPageSize(Long pageSize);


    /**
     * 是否禁用数据权限
     *
     * @return
     */
    default boolean isDisablePurview() {
        return false;
    }

    /**
     * 构建分页对象
     *
     * @return
     */
    Page<E> buildPageObj();


    /**
     * 查询条件
     *
     * @return
     */
    default List<Z9CrudQueryCondition<E>> queryConditions() {
        return new ArrayList<>();
    }

    /**
     * 升序字段
     *
     * @return
     */
    Set<String> getAscFields();

    /**
     * 降序字段
     *
     * @return
     */
    Set<String> getDescFields();

    default boolean autoOrder() {
        return true;
    }

}

package com.chargehub.common.security.template.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/04/03 16:26
 */
public interface Z9BaseCrud<E extends Z9CrudEntity> {

    /**
     * 创建
     *
     * @param e
     */
    void doCreate(E e);

    /**
     * 更新
     *
     * @param e
     * @param id
     */
    void doEdit(E e, String id);

    /**
     * 批量删除
     *
     * @param ids
     */
    void doDeleteByIds(Collection<?> ids);

    /**
     * 获取详情
     *
     * @param id
     * @return
     */
    E doGetDetailById(String id);

    /**
     * 获取所有
     *
     * @param queryDto
     * @return
     */
    List<E> doGetAll(Z9CrudQueryDto<E> queryDto);

    /**
     * 查询名字
     *
     * @param e
     * @param id
     * @return
     */
    E doGetExist(E e, String id);

    /**
     * 获取分页
     *
     * @param queryDto
     * @return
     */
    IPage<E> doGetPage(Z9CrudQueryDto<E> queryDto);

    /**
     * 保存excel数据
     *
     * @param list
     */
    void doSaveExcelData(List<E> list);

    /**
     * 获取集合列表
     *
     * @return
     */
    List<Map<String, Object>> doListMaps();

    /**
     * 获取当前实体
     *
     * @return
     */
    @SuppressWarnings("all")
    default Class<E> getEntity() {
        Class<?>[] typeArguments = GenericTypeUtils.resolveTypeArguments(getClass(), Z9BaseCrud.class);
        return (Class<E>) typeArguments[0];
    }


}

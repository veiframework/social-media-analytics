package com.chargehub.common.security.template.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.common.security.template.domain.ExcelImportDto;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import com.chargehub.common.security.template.domain.Z9CrudExportResult;
import com.chargehub.common.security.template.domain.Z9CrudImportResult;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.vo.Z9CrudVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @since 2023-12-13 15:59
 */
public interface Z9CrudService<E extends Z9CrudEntity> {

    /**
     * 创建
     *
     * @param dto
     */
    void create(Z9CrudDto<E> dto);


    /**
     * 更新
     *
     * @param dto
     * @param id
     */
    void edit(Z9CrudDto<E> dto, String id);

    /**
     * 批量删除
     *
     * @param ids
     */
    void deleteByIds(String ids);

    /**
     * 批量保存excel数据
     *
     * @param dtos
     */
    void saveExcelData(List<Z9CrudDto<E>> dtos);

    /**
     * 获取详情
     *
     * @param id
     * @return
     */
    Z9CrudVo getDetailById(String id);

    /**
     * 检查是否存在
     *
     * @param dto
     * @param id
     * @return
     */
    Z9CrudVo getExist(Z9CrudDto<E> dto, String id);


    /**
     * 获取所有
     *
     * @param queryDto
     * @return
     */
    @SuppressWarnings("all")
    List<? extends Z9CrudVo> getAll(Z9CrudQueryDto<E> queryDto);


    /**
     * 获取分页
     *
     * @param queryDto
     * @return
     */
    @SuppressWarnings("all")
    IPage<? extends Z9CrudVo> getPage(Z9CrudQueryDto<E> queryDto);

    /**
     * 导出excel
     *
     * @param queryDto
     * @return
     */
    Z9CrudExportResult exportExcel(Z9CrudQueryDto<E> queryDto);

    /**
     * 导入
     *
     * @param importDto
     * @return
     */
    Z9CrudImportResult<Z9CrudDto<E>> importExcel(ExcelImportDto importDto);

    /**
     * 导出excel模板
     *
     * @param response
     */
    void exportExcelTemplate(HttpServletResponse response);

    /**
     * 重复数据提示
     *
     * @return
     */
    default String duplicateMsg() {
        return "数据已存在,请勿重复添加";
    }

    /**
     * excel名称
     *
     * @return
     */
    String excelName();

    /**
     * 分片大小
     *
     * @return
     */
    default long partitionSize() {
        return 500;
    }

    /**
     * 发布事件
     *
     * @param event
     */
    void publishEvent(Object event);

    /**
     * 获取集合列表
     *
     * @return
     */
    List<Map<String, Object>> listMaps();

    /**
     * 获取class
     *
     * @return
     */
    <T extends Z9CrudDto<E>> Class<T> dtoClass();

    /**
     * 获取class
     *
     * @return
     */
    <V extends Z9CrudVo> Class<V> voClass();


}

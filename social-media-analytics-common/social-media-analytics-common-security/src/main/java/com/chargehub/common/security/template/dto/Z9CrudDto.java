package com.chargehub.common.security.template.dto;

import cn.afterturn.easypoi.handler.inter.IExcelDataModel;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.chargehub.common.security.template.domain.Z9CrudEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/04/03 14:52
 */
public interface Z9CrudDto<E extends Z9CrudEntity> extends IExcelModel, IExcelDataModel {


    /**
     * 获取子id
     *
     * @return
     */
    default Map<SFunction<E, Object>, Object> subUniqueIdGetter() {
        return new HashMap<>(16);
    }
}

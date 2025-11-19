package com.chargehub.common.security.template.event;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import com.chargehub.common.security.template.domain.Z9CrudQueryCondition;
import lombok.Data;

import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2024/09/29 15:13
 */
@Data
public class PurviewQueryEvent<E extends Z9CrudEntity> {

    private List<Z9CrudQueryCondition<E>> purviewConditions;

    private LambdaQueryWrapper<E> qw;

    public PurviewQueryEvent(List<Z9CrudQueryCondition<E>> purviewConditions, LambdaQueryWrapper<E> qw) {
        this.purviewConditions = purviewConditions;
        this.qw = qw;
    }
}

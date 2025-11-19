package com.vchaoxi.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.vchaoxi.entity.MemberOpenOption;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 15:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberOpenOptionQueryDto extends Pagination implements Z9CrudQueryDto<MemberOpenOption> {

    @CrudQueryField
    private Integer status;

    @Override
    public Page<MemberOpenOption> buildPageObj() {
        return page();
    }

}

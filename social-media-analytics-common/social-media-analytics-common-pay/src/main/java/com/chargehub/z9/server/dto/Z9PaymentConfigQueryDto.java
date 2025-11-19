package com.chargehub.z9.server.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.z9.server.domain.Z9PaymentConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/05/19 18:14
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class Z9PaymentConfigQueryDto extends Pagination implements Z9CrudQueryDto<Z9PaymentConfig>, Serializable {

    private static final long serialVersionUID = -2134634938840886283L;

    @Schema(description = "租户id")
    @CrudQueryField
    private String tenantId;

    @Override
    public Page<Z9PaymentConfig> buildPageObj() {
        return page();
    }


}

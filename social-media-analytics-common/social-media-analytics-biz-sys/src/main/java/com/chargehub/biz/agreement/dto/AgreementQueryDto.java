package com.chargehub.biz.agreement.dto;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.biz.agreement.domain.Agreement;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zhanghaowei
 * @date 2025/08/08 16:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AgreementQueryDto extends Pagination implements Z9CrudQueryDto<Agreement> {

    @CrudQueryField
    @ApiModelProperty(value = "协议类型")
    private String type;

    @Override
    public Page<Agreement> buildPageObj() {
        return page();
    }

}

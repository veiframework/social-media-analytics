package com.vchaoxi.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.vchaoxi.entity.CommissionRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Zhanghaowei
 * @date 2025/08/27 18:02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommissionRecordQueryDto extends Pagination implements Serializable, Z9CrudQueryDto<CommissionRecord> {
    private static final long serialVersionUID = 6690381114610426858L;

    @ApiModelProperty("操作类型 commission-提成, withdraw-提现")
    @CrudQueryField
    private String type;

    @ApiModelProperty("登陆人id")
    @CrudQueryField
    private String loginId;

    @CrudQueryField
    private String status;

    @ApiModelProperty(value = "升序字段")
    private Set<String> ascFields;

    @ApiModelProperty(value = "降序字段")
    private Set<String> descFields;
    @Override
    public Page<CommissionRecord> buildPageObj() {
        return page();
    }
}

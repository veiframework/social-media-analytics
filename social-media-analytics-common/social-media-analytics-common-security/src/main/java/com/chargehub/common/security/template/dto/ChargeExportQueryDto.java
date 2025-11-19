package com.chargehub.common.security.template.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.domain.ChargeExport;
import com.chargehub.common.security.template.domain.Z9CrudQueryCondition;
import com.chargehub.common.security.template.enums.Z9QueryTypeEnum;
import com.chargehub.common.security.utils.SecurityUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Zhanghaowei
 * @date 2024/07/16 17:21
 */
@Data
public class ChargeExportQueryDto implements Serializable, Z9CrudQueryDto<ChargeExport> {

    private static final long serialVersionUID = 3003898770728656859L;

    @CrudQueryField(queryType = Z9QueryTypeEnum.EQ)
    @ApiModelProperty(value = "业务编码")
    private String businessCode;


    @CrudQueryField(queryType = Z9QueryTypeEnum.EQ)
    @ApiModelProperty(value = "用户id")
    private String createBy;

    @CrudQueryField(queryType = Z9QueryTypeEnum.EQ)
    @ApiModelProperty(value = "导出状态0-导出中,1导出成功,2导出失败")
    private Integer exportStatus;

    @ApiModelProperty(value = "当前页码")
    private Long pageNum;

    @ApiModelProperty(value = "每页条数")
    private Long pageSize;

    @ApiModelProperty(hidden = true)
    private long number;

    @ApiModelProperty(hidden = true)
    private long size;

    @ApiModelProperty(value = "升序字段")
    private Set<String> ascFields;

    @ApiModelProperty(value = "降序字段")
    private Set<String> descFields;


    public <T> Page<T> page() {
        long number0 = getNumber();
        long size0 = getSize();
        if (pageNum != null) {
            number0 = getPageNum();
        }
        if (pageSize != null) {
            size0 = getPageSize();
        }
        return new Page<>(number0, size0);
    }

    @Override
    public Page<ChargeExport> buildPageObj() {
        return page();
    }


    @Override
    public List<Z9CrudQueryCondition<ChargeExport>> queryConditions() {
        List<Z9CrudQueryCondition<ChargeExport>> z9CrudQueryConditions = new ArrayList<>();
        Z9CrudQueryCondition<ChargeExport> condition = new Z9CrudQueryCondition<>();
        condition.setField(ChargeExport::getCreateBy);
        condition.setQueryType(Z9QueryTypeEnum.EQ);
        condition.setValue(StringUtils.isNotBlank(createBy) ? createBy : SecurityUtils.getUserId());
        z9CrudQueryConditions.add(condition);
        return z9CrudQueryConditions;
    }

}

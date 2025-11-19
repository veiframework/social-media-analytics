package com.chargehub.biz.region.domain.dto;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.biz.region.domain.RegionEntity;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.domain.Z9CrudQueryCondition;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.enums.Z9QueryTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Zhanghaowei
 * @date 2021-11-05 14:42:56
 */
@Data
@Schema(description = "分页查询参数")
@EqualsAndHashCode(callSuper = true)
public class RegionQueryDto extends Pagination implements Z9CrudQueryDto<RegionEntity> {

    private static final long serialVersionUID = 6589114595277274611L;

    @CrudQueryField(queryType = Z9QueryTypeEnum.LIKE)
    @Schema(description = "标签")
    private String label;

    @Schema(description = "是否关闭数据权限")
    private boolean disablePurview;

    @CrudQueryField(queryType = Z9QueryTypeEnum.EQ)
    @Schema(description = "是否可见0-不可见,1-可见")
    private String visible;

    @CrudQueryField(queryType = Z9QueryTypeEnum.EQ)
    @Schema(description = "层级")
    private Integer level;


    @CrudQueryField(queryType = Z9QueryTypeEnum.EQ)
    @Schema(description = "父级id")
    private String parentId;

    @CrudQueryField(queryType = Z9QueryTypeEnum.EQ)
    @Schema(description = "区域编码")
    private String areaCode;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    @Schema(description = "区域id逗号隔开,形如1,2,3,5")
    private Set<String> id;

    private Boolean filterCost;


    @Override
    public Page<RegionEntity> buildPageObj() {
        return page();
    }

    @Override
    public List<Z9CrudQueryCondition<RegionEntity>> queryConditions() {
        List<Z9CrudQueryCondition<RegionEntity>> z9CrudQueryConditions = new ArrayList<>();
        if (filterCost != null) {
            Z9CrudQueryCondition<RegionEntity> condition = new Z9CrudQueryCondition<>();
            condition.setField(RegionEntity::getId);
            condition.setQueryType(Z9QueryTypeEnum.IN_SQL);
            condition.setValue("select parent_id from charge_region_ids where region_id in ( select region_id from analytics.charge_cost_detail)");
            z9CrudQueryConditions.add(condition);
        }
        return z9CrudQueryConditions;
    }
}

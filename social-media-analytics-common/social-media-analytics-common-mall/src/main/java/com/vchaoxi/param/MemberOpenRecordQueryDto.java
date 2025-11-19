package com.vchaoxi.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.biz.region.domain.RegionEntity;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.domain.Z9CrudQueryCondition;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.enums.Z9QueryTypeEnum;
import com.vchaoxi.entity.MemberOpenRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 17:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberOpenRecordQueryDto extends Pagination implements Z9CrudQueryDto<MemberOpenRecord> {

    private String startTime;

    private String endTime;

    @CrudQueryField(queryType = Z9QueryTypeEnum.LIKE)
    private String phone;

    @CrudQueryField
    private Integer type;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<Integer> status;

    @Override
    public Page<MemberOpenRecord> buildPageObj() {
        return page();
    }


    @Override
    public List<Z9CrudQueryCondition<MemberOpenRecord>> queryConditions() {
        List<Z9CrudQueryCondition<MemberOpenRecord>> z9CrudQueryConditions = new ArrayList<>();
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            Z9CrudQueryCondition<MemberOpenRecord> condition = new Z9CrudQueryCondition<>();
            condition.setField(MemberOpenRecord::getOpenTime);
            condition.setQueryType(Z9QueryTypeEnum.GE);
            condition.setValue(startTime);
            Z9CrudQueryCondition<MemberOpenRecord> condition2 = new Z9CrudQueryCondition<>();
            condition2.setField(MemberOpenRecord::getOpenTime);
            condition2.setQueryType(Z9QueryTypeEnum.LT);
            condition2.setValue(endTime);
            z9CrudQueryConditions.add(condition);
            z9CrudQueryConditions.add(condition2);
        }
        return z9CrudQueryConditions;
    }
}

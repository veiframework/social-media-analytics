package com.chargehub.admin.account.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.domain.Z9CrudQueryCondition;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.enums.Z9QueryTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SocialMediaAccountQueryDto extends Pagination implements Z9CrudQueryDto<SocialMediaAccount>, Serializable {
    private static final long serialVersionUID = -4206120068017448896L;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<String> id;

    @CrudQueryField(queryType = Z9QueryTypeEnum.LIKE)
    @ApiModelProperty("第三方用户昵称")
    private Set<String> nickname;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<String> userId;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<Integer> syncWorkStatus;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<String> type;

    @CrudQueryField(queryType = Z9QueryTypeEnum.LE)
    private String syncWorkDate;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    @ApiModelProperty("平台id")
    private Set<String> platformId;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    @ApiModelProperty("第三方用户id")
    private Set<String> uid;

    @CrudQueryField
    @ApiModelProperty("是否启用自动同步")
    private String autoSync;

    @CrudQueryField
    private Integer crawler;

    @CrudQueryField
    private String tenantId;

    private Set<String> userId_dictText;

    public SocialMediaAccountQueryDto() {
    }

    public SocialMediaAccountQueryDto(long pageNum, long pageSize, boolean searchCount) {
        this.setPageNum(pageNum);
        this.setPageSize(pageSize);
        this.setSearchCount(searchCount);
    }

    @Override
    public Page<SocialMediaAccount> buildPageObj() {
        return page();
    }

    @Override
    public List<Z9CrudQueryCondition<SocialMediaAccount>> queryConditions() {
        List<Z9CrudQueryCondition<SocialMediaAccount>> z9CrudQueryConditions = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userId_dictText)) {
            Z9CrudQueryCondition<SocialMediaAccount> condition = new Z9CrudQueryCondition<>();
            condition.setField(SocialMediaAccount::getUserId);
            condition.setQueryType(Z9QueryTypeEnum.IN_SQL);
            condition.setValue("select user_id from sys_user where nick_name in (" + userId_dictText.stream().map(i -> "'" + i + "'").collect(Collectors.joining(",")) + ")");
            z9CrudQueryConditions.add(condition);
        }
        return z9CrudQueryConditions;
    }

}

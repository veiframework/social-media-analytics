package com.chargehub.admin.work.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.enums.Z9QueryTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Set;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SocialMediaWorkQueryDto extends Pagination implements Serializable, Z9CrudQueryDto<SocialMediaWork> {
    private static final long serialVersionUID = 992829188762599177L;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<String> userId;

    @CrudQueryField
    private String type;

    @CrudQueryField
    private String status;

    @CrudQueryField
    private String platformId;

    @CrudQueryField(queryType = Z9QueryTypeEnum.LIKE)
    @ApiModelProperty("描述")
    private String description;

    @Override
    public Page<SocialMediaWork> buildPageObj() {
        return page();
    }
}

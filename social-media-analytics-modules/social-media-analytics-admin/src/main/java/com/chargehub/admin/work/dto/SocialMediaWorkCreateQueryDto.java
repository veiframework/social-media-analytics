package com.chargehub.admin.work.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.work.domain.SocialMediaWorkCreate;
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
 * @author zhanghaowei
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SocialMediaWorkCreateQueryDto extends Pagination implements Z9CrudQueryDto<SocialMediaWorkCreate>, Serializable {


    private static final long serialVersionUID = 6563357599873877017L;

    @CrudQueryField
    private String creator;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<String> createStatus;

    @CrudQueryField(queryType = Z9QueryTypeEnum.GT)
    @ApiModelProperty("重试次数")
    private Integer retryCount;

    @CrudQueryField(queryType = Z9QueryTypeEnum.LIKE)
    private String shareLink;

    @CrudQueryField
    private String accountType;

    @CrudQueryField
    @ApiModelProperty("业务类型")
    private String customType;

    @Override
    public Page<SocialMediaWorkCreate> buildPageObj() {
        return page();
    }
}

package com.chargehub.admin.account.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.enums.Z9QueryTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SocialMediaAccountQueryDto extends Pagination implements Z9CrudQueryDto<SocialMediaAccount>, Serializable {
    private static final long serialVersionUID = -4206120068017448896L;

    @CrudQueryField
    private String id;

    @CrudQueryField(queryType = Z9QueryTypeEnum.LIKE)
    @ApiModelProperty("第三方用户昵称")
    private String nickname;

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
}

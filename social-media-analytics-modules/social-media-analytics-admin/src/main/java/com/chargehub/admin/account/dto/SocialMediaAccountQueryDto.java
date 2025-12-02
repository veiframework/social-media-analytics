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
import java.util.Set;

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
    private String nickname;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<String> userId;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<Integer> syncWorkStatus;

    @CrudQueryField
    private String type;

    @CrudQueryField(queryType = Z9QueryTypeEnum.LE)
    private String syncWorkDate;

    @CrudQueryField
    @ApiModelProperty("平台id")
    private String platformId;

    @CrudQueryField
    @ApiModelProperty("是否启用自动同步")
    private String autoSync;

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

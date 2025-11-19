package com.chargehub.admin.groupuser.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.groupuser.domain.GroupUser;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupUserQueryDto extends Pagination implements Serializable, Z9CrudQueryDto<GroupUser> {
    private static final long serialVersionUID = 8801748761563136761L;

    @CrudQueryField
    private String parentUserId;

    @Override
    public Page<GroupUser> buildPageObj() {
        return page();
    }
}

package com.chargehub.admin.work.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.work.domain.SocialMediaWorkPriority;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SocialMediaWorkPriorityQueryDto extends Pagination implements Serializable, Z9CrudQueryDto<SocialMediaWorkPriority> {
    private static final long serialVersionUID = 1237886692418456962L;



    @Override
    public Page<SocialMediaWorkPriority> buildPageObj() {
        return page();
    }
}

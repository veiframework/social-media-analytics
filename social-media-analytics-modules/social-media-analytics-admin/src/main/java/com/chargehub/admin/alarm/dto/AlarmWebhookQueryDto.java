package com.chargehub.admin.alarm.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.alarm.domain.AlarmWebhook;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.annotation.CrudQueryField;
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
public class AlarmWebhookQueryDto extends Pagination implements Serializable, Z9CrudQueryDto<AlarmWebhook> {
    private static final long serialVersionUID = -1139977004347098087L;


    @CrudQueryField
    private String userId;

    @CrudQueryField
    private String type;


    @Override
    public Page<AlarmWebhook> buildPageObj() {
        return page();
    }
}

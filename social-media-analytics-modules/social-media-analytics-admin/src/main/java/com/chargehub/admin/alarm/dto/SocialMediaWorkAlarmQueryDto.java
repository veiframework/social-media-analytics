package com.chargehub.admin.alarm.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.alarm.domain.SocialMediaWorkAlarm;
import com.chargehub.common.datasource.mybatis.Pagination;
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
public class SocialMediaWorkAlarmQueryDto extends Pagination implements Serializable, Z9CrudQueryDto<SocialMediaWorkAlarm> {
    private static final long serialVersionUID = -9204418493431493590L;


    @Override
    public Page<SocialMediaWorkAlarm> buildPageObj() {
        return page();
    }
}

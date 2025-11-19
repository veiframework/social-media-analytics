package com.chargehub.biz.appuser.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.chargehub.biz.appuser.domain.AppUser;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.enums.Z9QueryTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询DTO
 *
 * @author system
 * @since 2024-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UserQueryDto", description = "用户查询DTO")
public class AppUserQueryDto extends Pagination implements Z9CrudQueryDto<AppUser> {

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    @CrudQueryField(queryType = Z9QueryTypeEnum.LIKE)
    private String username;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    @CrudQueryField(queryType = Z9QueryTypeEnum.LIKE)
    private String nickname;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    @CrudQueryField(queryType = Z9QueryTypeEnum.LIKE)
    private String phone;


    @Override
    public Page<AppUser> buildPageObj() {
        return page();
    }

}
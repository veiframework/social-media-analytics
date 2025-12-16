package com.chargehub.admin.work.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.domain.Z9CrudQueryCondition;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.enums.Z9QueryTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
public class SocialMediaWorkQueryDto extends Pagination implements Serializable, Z9CrudQueryDto<SocialMediaWork> {
    private static final long serialVersionUID = 992829188762599177L;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<String> userId;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<String> type;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<String> status;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<String> platformId;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    @ApiModelProperty("业务类型")
    private Set<String> customType;


    @CrudQueryField(queryType = Z9QueryTypeEnum.LIKE)
    @ApiModelProperty("描述")
    private Set<String> description;


    @ApiModelProperty("发布时间开始")
    private String startPostTime;

    @ApiModelProperty("发布时间结束")
    private String endPostTime;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<String> accountId;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    @ApiModelProperty("社交账号类型 个人- individual, 素人- amateur")
    private Set<String> accountType;

    @CrudQueryField(queryType = Z9QueryTypeEnum.GE)
    private String updateTime;

    @CrudQueryField(queryType = Z9QueryTypeEnum.LIKE)
    private String shareLink;

    @CrudQueryField(queryType = Z9QueryTypeEnum.IN)
    private Set<Integer> syncWorkStatus;

    private Set<String> accountId_dictText;

    private Set<String> userId_dictText;

    private String startCreateTime;

    private String endCreateTime;

    @Override
    public Page<SocialMediaWork> buildPageObj() {
        return page();
    }

    @Override
    public List<Z9CrudQueryCondition<SocialMediaWork>> queryConditions() {
        List<Z9CrudQueryCondition<SocialMediaWork>> z9CrudQueryConditions = new ArrayList<>();
        if (StringUtils.isNotBlank(startPostTime) && StringUtils.isNotBlank(endPostTime)) {
            Z9CrudQueryCondition<SocialMediaWork> condition = new Z9CrudQueryCondition<>();
            condition.setField(SocialMediaWork::getPostTime);
            condition.setQueryType(Z9QueryTypeEnum.GE);
            condition.setValue(startPostTime);
            Z9CrudQueryCondition<SocialMediaWork> condition2 = new Z9CrudQueryCondition<>();
            condition2.setField(SocialMediaWork::getPostTime);
            condition2.setQueryType(Z9QueryTypeEnum.LE);
            condition2.setValue(endPostTime);
            z9CrudQueryConditions.add(condition);
            z9CrudQueryConditions.add(condition2);
        }
        if (CollectionUtils.isNotEmpty(accountId_dictText)) {
            Z9CrudQueryCondition<SocialMediaWork> condition = new Z9CrudQueryCondition<>();
            condition.setField(SocialMediaWork::getAccountId);
            condition.setQueryType(Z9QueryTypeEnum.IN_SQL);
            condition.setValue("select id from social_media_account where nickname in (" + accountId_dictText.stream().map(i -> "'" + i + "'").collect(Collectors.joining(",")) + ")");
            z9CrudQueryConditions.add(condition);
        }
        if (CollectionUtils.isNotEmpty(userId_dictText)) {
            Z9CrudQueryCondition<SocialMediaWork> condition = new Z9CrudQueryCondition<>();
            condition.setField(SocialMediaWork::getUserId);
            condition.setQueryType(Z9QueryTypeEnum.IN_SQL);
            condition.setValue("select user_id from sys_user where nick_name in (" + userId_dictText.stream().map(i -> "'" + i + "'").collect(Collectors.joining(",")) + ")");
            z9CrudQueryConditions.add(condition);
        }
        if (StringUtils.isNotBlank(startCreateTime) && StringUtils.isNotBlank(endCreateTime)) {
            Z9CrudQueryCondition<SocialMediaWork> condition = new Z9CrudQueryCondition<>();
            condition.setField(SocialMediaWork::getPostTime);
            condition.setQueryType(Z9QueryTypeEnum.GE);
            condition.setValue(startCreateTime);
            Z9CrudQueryCondition<SocialMediaWork> condition2 = new Z9CrudQueryCondition<>();
            condition2.setField(SocialMediaWork::getPostTime);
            condition2.setQueryType(Z9QueryTypeEnum.LT);
            condition2.setValue(endCreateTime);
            z9CrudQueryConditions.add(condition);
            z9CrudQueryConditions.add(condition2);
        }
        return z9CrudQueryConditions;
    }

}

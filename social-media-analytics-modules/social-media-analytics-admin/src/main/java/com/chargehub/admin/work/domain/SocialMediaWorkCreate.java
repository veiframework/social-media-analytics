package com.chargehub.admin.work.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chargehub.common.security.template.annotation.CrudSubUniqueId;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class SocialMediaWorkCreate implements Serializable, Z9CrudEntity {

    private static final long serialVersionUID = -7561717508409133742L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("作品ID")
    private String workId;

    @CrudSubUniqueId
    @ApiModelProperty("分享链接")
    private String shareLink;

    @ApiModelProperty("账号类型")
    private String accountType;

    @ApiModelProperty("业务类型")
    private String customType;

    @ApiModelProperty("错误堆栈")
    private String errorStack;

    @ApiModelProperty("错误信息")
    private String errorMsg;

    @ApiModelProperty("创建状态")
    private String createStatus;

    @ApiModelProperty("重试次数")
    private Integer retryCount;

    private Date createTime;

    @TableField(fill = FieldFill.INSERT)
    private String creator;


    @Override
    public String getUniqueId() {
        return this.id;
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }
}

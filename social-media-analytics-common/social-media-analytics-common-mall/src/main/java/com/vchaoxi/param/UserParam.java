package com.vchaoxi.param;


import com.chargehub.common.core.utils.validation.customized.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class UserParam {
    @NotNull
    private Integer userId;

    /**
     * 用户等级（1:普通用户，2:会员用户，3:vip用户）
     */
    @EnumValue(intValues = {1,2,3})
    private Integer memGrade;

    /**
     * 是否有设置获取第三级用户积分的权限（0:有，1:无）
     */
    @EnumValue(intValues = {0,1})
    private Integer thirdPointsAccess;

    /**
     * 添加或积分
     */
    private Integer integral;
    /**
     * 绑定推广人
     */
    private Integer parentUserId;

}

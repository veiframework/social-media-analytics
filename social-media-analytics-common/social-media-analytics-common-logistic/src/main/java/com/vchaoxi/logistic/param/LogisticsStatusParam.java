package com.vchaoxi.logistic.param;


import com.chargehub.common.core.utils.validation.customized.EnumValue;
import lombok.Data;


/**
 * <p>
 * 
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-30
 */
@Data
public class LogisticsStatusParam {


    /**
     * 商家id
     */
    private Integer shopId;

    /**
     * 物流开关状态  1开 0关
     */
    @EnumValue(intValues = {0,1})
    private Integer status;
}

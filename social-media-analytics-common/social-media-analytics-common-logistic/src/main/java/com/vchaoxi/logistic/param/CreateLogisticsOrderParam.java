package com.vchaoxi.logistic.param;


import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.thirdparty.api.domain.dto.logistics.LoOrderAddParam;
import com.chargehub.thirdparty.api.domain.vo.logistics.LoOrderAddVo;
import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2023/11/6 17:25
 * @Project：vc-shoes-server
 * @Package：com.vchaoxi.param
 * @Filename：ShopCouponValidateParam
 */
@Data
public class CreateLogisticsOrderParam {

    private AjaxResult commonResult;
    private LoOrderAddParam loOrderAddParam;
    private LoOrderAddVo loOrderAddVo;



    public CreateLogisticsOrderParam(AjaxResult commonResult) {
        this.commonResult = commonResult;
    }


    public CreateLogisticsOrderParam(LoOrderAddParam loOrderAddParam, LoOrderAddVo loOrderAddVo) {
        this.loOrderAddParam = loOrderAddParam;
        this.loOrderAddVo = loOrderAddVo;
    }
}

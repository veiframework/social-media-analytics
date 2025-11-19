package com.vchaoxi.param;

import com.chargehub.common.core.utils.validation.customized.EnumValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;


@Data
public class GoodsParam {

    @NotNull(groups = {Edit.class,Del.class,UpAndDown.class,SetOrCancelBanner.class,SetPromotion.class})
    private Integer id;
    @NotNull(groups = {AddGoods.class,Edit.class})
    private Integer type;
    @NotEmpty(groups = {AddGoods.class,Edit.class})
    private String name;
    @NotEmpty(groups = {AddGoods.class,Edit.class})
    private String goodsNo;
    @EnumValue(groups = {UpAndDown.class},intValues = {1,0})
    private Integer status;
    @EnumValue(groups = {SetOrCancelBanner.class},intValues = {1,0})
    private Integer isBanner;
    /**
     * 是否促销 0正常  1促销
     */
    @EnumValue(groups = {SetPromotion.class},intValues = {1,0})
    private Integer isPromotion;
    @NotEmpty(groups = {AddGoods.class,Edit.class})
    private String img;

    private List<String> galleryList;

    //商品标准价格
    private BigDecimal amount;

    @ApiModelProperty("运费")
    private BigDecimal freight;

    //商品会员价格
    private BigDecimal memAmount;

    //商品vip价格
    private BigDecimal vipAmount;

    private List<SpecificationParam> goodsSpecificationVoList;


    private Integer serial;

    private String unit;
    //简介
    private String brief;
    private String details;


    private Integer shopId;
    private Integer agentId;



    @NotNull(groups = {GoodsTyepParam.Sort.class})
    @Size(groups = {GoodsTyepParam.Sort.class},min = 1)
    private List<Integer> ids;



    public interface AddGoods {}
    public interface Edit {}
    public interface Del {}
    public interface UpAndDown {}
    public interface SetOrCancelBanner {}//设置banner或取消设置banner
    //设置促销
    public interface SetPromotion{}
    public interface Sort {}
}

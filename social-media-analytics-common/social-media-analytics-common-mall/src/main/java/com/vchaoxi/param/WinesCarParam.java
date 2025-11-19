package com.vchaoxi.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class WinesCarParam {

    /**
     * 购物车条目的id
     */
    @NotNull(groups = {Edit.class},message = "购物车条目的id不能为空")
    private Integer id;

    @NotNull(groups = {Del.class},message = "购物车条目的id不能为空")
    private List<Integer> ids;
    /**
     * 商品表的商品ID
     */
    @NotNull(groups = {Add.class},message = "商品ID不能为空")
    private Integer goodsId;

    /**
     * 商品货品的数量
     */
    @NotNull(groups = {Add.class},message = "数量不能为空")
    private Integer number;

    /**
     * 规格的id
     */
    @NotNull(groups = {Add.class},message = "商品规格Id不能为空")
    private Integer specificationId;

    /**
     * 规格
     */
    private String specifications;

    /**
     * 购物车中商品是否选择状态（0:未勾选，1:已勾选）
     */
    private Integer checked;

    /**
     * 0:取消选择，1:选择
     */
    @NotNull(groups = {Batch.class})
    private Integer batchType;

    private String group;

    public interface Add {}
    public interface Del{}
    public interface Edit{}
    public interface Batch{}
}

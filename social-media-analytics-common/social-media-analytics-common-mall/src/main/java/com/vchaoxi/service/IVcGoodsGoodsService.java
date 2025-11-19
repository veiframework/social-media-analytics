package com.vchaoxi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vchaoxi.entity.VcGoodsGoods;
import com.vchaoxi.param.GoodsParam;
import com.vchaoxi.vo.CommonResult;
import com.vchaoxi.vo.GoodsVo;

/**
 * <p>
 * 商品相关 - 商品表 服务类
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
public interface IVcGoodsGoodsService extends IService<VcGoodsGoods> {


    /**
     * 添加商品信息
     * @param goodsParam
     * @return
     */
    public CommonResult add(GoodsParam goodsParam);


    /**
     * 编辑
     * @param goodsParam
     * @param goodsVo
     * @return
     */
    public CommonResult edit(GoodsParam goodsParam, GoodsVo goodsVo);



    /**
     * 删除商品信息
     * @param id
     * @param goodsVo
     * @return
     */
    public CommonResult del(Integer id,GoodsVo goodsVo);
}

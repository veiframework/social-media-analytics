package com.vchaoxi.controller.app;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.common.core.constant.HttpStatus;
import com.chargehub.common.core.web.controller.BaseController;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.mapper.VcGoodsGoodsMapper;
import com.vchaoxi.mapper.VcGoodsTypeMapper;
import com.vchaoxi.mapper.WinesGoodsSpecificationMapper;
import com.vchaoxi.service.IVcUserUserService;
import com.vchaoxi.vo.CommonResult;
import com.vchaoxi.vo.GoodsVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 商铺商铺服务相关接口
 */
@RestController
@RequestMapping("/app/goods")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GoodsAppController extends BaseController {


    private final VcGoodsTypeMapper vcGoodsTypeMapper;
    private final VcGoodsGoodsMapper vcGoodsGoodsMapper;

    private final WinesGoodsSpecificationMapper winesGoodsSpecificationMapper;

    private final IVcUserUserService vcUserUserService;

    /**
     * 查询商品洗鞋服务分类
     *
     * @param shopId
     * @return
     */
    @GetMapping("/goods-type")
    @RequiresLogin(doIntercept = false)
    public CommonResult goodsType(@RequestParam(value = "shopId", defaultValue = "") Integer shopId,
                                  @RequestParam(value = "parentId", required = false) Integer parentId) {
        if (shopId == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "商家信息不能同时为空");
        }
        return CommonResult.success(vcGoodsTypeMapper.selectByShopId(shopId, parentId));
    }


    /**
     * 小程序首页信息
     *
     * @return
     */
    @GetMapping("/home-info")
    public CommonResult homeInfo(@RequestParam(required = false) Integer goodsParentType) {
        List<GoodsVo> goodsList = vcGoodsGoodsMapper.userGoodsList(null, goodsParentType);
        List<GoodsVo> userBannerList = goodsList.stream()
                .filter(g -> g.getIsBanner() == 1)
                .collect(Collectors.toList());
        List<GoodsVo> isPromotion = goodsList.stream()
                .filter(goodsVo -> goodsVo.getIsPromotion() == 1).collect(Collectors.toList());
        GoodsVo goodsVo = null;
        if (!isPromotion.isEmpty() && 0 != isPromotion.size()) {
            goodsVo = isPromotion.get(0);
        }
        //查询促销商品
        for (GoodsVo good : goodsList) {
            good.setGoodsSpecificationVoList(winesGoodsSpecificationMapper.goodsSpecificationList(good.getId()));
        }
        System.out.println("促销商品：" + goodsVo);
        Map<String, Object> map = new HashMap<>();
        map.put("goodsList", goodsList);
        map.put("bannerList", userBannerList);
        map.put("promotionGood", null != goodsVo ? goodsVo : null);
        return CommonResult.success(map);
    }


    /**
     * 查询商品列表
     *
     * @param goodsName
     * @param sortType（不传，代表综合，1:销量，2:价格正序，3:价格倒序）
     * @return
     */
    @GetMapping("/goods-list")
    public CommonResult goodsList(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, @RequestParam(defaultValue = "10", value = "pageSize") Integer pageSize,
                                  @RequestParam(value = "goodsName", defaultValue = "") String goodsName,
                                  @RequestParam(value = "sortType", defaultValue = "") Integer sortType,
                                  @RequestParam(value = "parentId", required = false) Integer parentId,
                                  @RequestParam(value = "type", required = false) Integer type) {

        Integer memGrade = null;
        VcUserUser vcUserUser = vcUserUserService.getCurrentUserNoException();
        if (vcUserUser != null) {
            memGrade = vcUserUser.getMemGrade();
        }

        Page<GoodsVo> page = new Page<>(pageNum, pageSize);
        List<GoodsVo> goodsVoList = vcGoodsGoodsMapper.goodsSearchWithSort(page, goodsName, sortType, memGrade, parentId, type);
        return CommonResult.success(page.setRecords(goodsVoList));
    }


    /**
     * 查询商品详情
     *
     * @param id
     * @return
     */
    @GetMapping("/goods-info")
    public CommonResult goodsInfo(@RequestParam(value = "id") Integer id) {
        GoodsVo goodsVo = vcGoodsGoodsMapper.selectByGoodId(id);
        if (goodsVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "未查询到该商品");
        }
        Map<String, Object> map = new HashMap<>();
        goodsVo.setTypeVo(vcGoodsTypeMapper.selectByTypeId(goodsVo.getType()));
        goodsVo.setGoodsSpecificationVoList(winesGoodsSpecificationMapper.goodsSpecificationList(goodsVo.getId()));
        if (!StringUtils.isEmpty(goodsVo.getGallery())) {
            String[] strArray = goodsVo.getGallery().split(",");
            List<String> galleryList = Arrays.asList(strArray);
            goodsVo.setGalleryList(galleryList);
        }
        map.put("goodsInfo", goodsVo);
        return CommonResult.success(map);
    }

}

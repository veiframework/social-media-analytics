package com.vchaoxi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.common.core.constant.HttpStatus;
import com.chargehub.common.core.web.controller.BaseController;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.utils.SecurityUtils;
import com.vchaoxi.constant.SysConstant;
import com.vchaoxi.entity.VcGoodsGoods;
import com.vchaoxi.entity.VcGoodsType;
import com.vchaoxi.entity.VcShopShop;
import com.vchaoxi.mapper.VcGoodsGoodsMapper;
import com.vchaoxi.mapper.VcGoodsTypeMapper;
import com.vchaoxi.mapper.WinesGoodsSpecificationMapper;
import com.vchaoxi.param.GoodsParam;
import com.vchaoxi.param.GoodsTyepParam;
import com.vchaoxi.service.IVcGoodsGoodsService;
import com.vchaoxi.service.IVcGoodsTypeService;
import com.vchaoxi.service.IVcPriceDefaultService;
import com.vchaoxi.service.IVcShopShopService;
import com.vchaoxi.vo.CommonResult;
import com.vchaoxi.vo.GoodsVo;
import com.vchaoxi.vo.TypeVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * 商品相关接口
 */
@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GoodsController extends BaseController {


    @Autowired
    private IVcShopShopService vcShopShopService;
    @Autowired
    private IVcGoodsTypeService vcGoodsTypeService;
    @Autowired
    private IVcGoodsGoodsService vcGoodsGoodsService;
    @Autowired
    private IVcPriceDefaultService vcPriceDefaultService;


    private final VcGoodsGoodsMapper vcGoodsGoodsMapper;
    private final VcGoodsTypeMapper vcGoodsTypeMapper;
    private final WinesGoodsSpecificationMapper winesGoodsSpecificationMapper;

    @Autowired
    private HttpServletRequest httpServletRequest;

    public Integer getAgentId() {
        String agentId = httpServletRequest.getHeader(SysConstant.AGENT_ID);
        if (agentId == null)
            return null;
        try {
            return Integer.parseInt(agentId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 添加商品信息
     *
     * @return
     */
    @PostMapping("/add")
    @RequiresPermissions({"operateData:goods:add:add"})
    public CommonResult newList(@RequestBody @Validated({GoodsParam.AddGoods.class}) GoodsParam goodsParam) {
        //判断所选分类是否有误
        VcGoodsType vcGoodsType = vcGoodsTypeService.lambdaQuery().eq(VcGoodsType::getId, goodsParam.getType()).one();
        if (vcGoodsType == null || vcGoodsType.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "所选分类不存在");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        goodsParam.setShopId(loginUser.getShopId());

        VcShopShop vcShopShop = vcShopShopService.lambdaQuery().eq(VcShopShop::getId, goodsParam.getShopId()).one();
        if (vcShopShop == null || vcShopShop.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "随选商铺不存在");
        }

        goodsParam.setAgentId(vcShopShop.getAgentId());
        return vcGoodsGoodsService.add(goodsParam);
    }


    /**
     * 商品列表
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @param shopId
     * @param status
     * @return
     */
    @GetMapping("/list")
    @RequiresPermissions({"operateData:goods:list:list"})
    public CommonResult add(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum,
                            @RequestParam(defaultValue = "10", value = "pageSize") Integer pageSize,
                            @RequestParam(defaultValue = "", value = "name") String name,
                            @RequestParam(defaultValue = "", value = "shopId") Integer shopId,
                            @RequestParam(defaultValue = "", value = "type") Integer type,
                            @RequestParam(defaultValue = "", value = "status") Integer status,
                            @RequestParam(defaultValue = "", value = "goodsNo") String goodsNo,
                            @RequestParam(value = "parentId", required = false) Integer parentId) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        Integer agentId = getAgentId();
        if (shopId == null) {
            shopId = loginUser.getShopId();
        }
        Page<GoodsVo> page = new Page<>(pageNum, pageSize);
        List<GoodsVo> list = vcGoodsGoodsMapper.adminSelect(page, name, agentId, shopId, type, status, goodsNo, parentId);
        for (GoodsVo goodsVo : list) {
            goodsVo.setTypeVo(vcGoodsTypeMapper.selectByTypeId(goodsVo.getType()));
            goodsVo.setGoodsSpecificationVoList(winesGoodsSpecificationMapper.goodsSpecificationList(goodsVo.getId()));
            if (!StringUtils.isEmpty(goodsVo.getGallery())) {
                String[] strArray = goodsVo.getGallery().split(",");
                List<String> galleryList = Arrays.asList(strArray);
                goodsVo.setGalleryList(galleryList);
            }
        }
        return CommonResult.success(page.setRecords(list));
    }


    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @GetMapping("/info")
    @RequiresPermissions({"operateData:goods:list:info"})
    public CommonResult add(@RequestParam("id") Integer id) {
        GoodsVo goodsVo = vcGoodsGoodsMapper.selectByGoodId(id);
        if (goodsVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "所选商品不存在");
        }

        goodsVo.setTypeVo(vcGoodsTypeMapper.selectByTypeId(goodsVo.getType()));
        goodsVo.setGoodsSpecificationVoList(winesGoodsSpecificationMapper.goodsSpecificationList(goodsVo.getId()));
        if (!StringUtils.isEmpty(goodsVo.getGallery())) {
            String[] strArray = goodsVo.getGallery().split(",");
            List<String> galleryList = Arrays.asList(strArray);
            goodsVo.setGalleryList(galleryList);
        }
        return CommonResult.success(goodsVo);
    }


    /**
     * 设置banner或者取消设置
     *
     * @param goodsParam
     * @return
     */
    @PostMapping("/set-cancel")
    @RequiresPermissions({"operateData:goods:list:setOrCancel"})
    @Transactional
    public CommonResult setCancel(@RequestBody @Validated({GoodsParam.SetOrCancelBanner.class}) GoodsParam goodsParam) {
        GoodsVo goodsVo = vcGoodsGoodsMapper.selectByGoodId(goodsParam.getId());
        if (goodsVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "所选商品不存在");
        }


        //如果本次为上架操作 将判断当前商品分类是否存在 且状态是否正常
        if (goodsParam.getIsBanner() == 1) {
            VcGoodsType vcGoodsType = vcGoodsTypeService.lambdaQuery().eq(VcGoodsType::getId, goodsVo.getType()).one();
            if (vcGoodsType == null || vcGoodsType.getIsDelete() != 0) {
                return CommonResult.error(HttpStatus.BAD_REQUEST, "当前商品分类不存在或已删除，商品无法设置成banner");
            }
        }

        vcGoodsGoodsService.lambdaUpdate().eq(VcGoodsGoods::getId, goodsParam.getId())
                .set(VcGoodsGoods::getIsBanner, goodsParam.getIsBanner()).update();
        return CommonResult.success();
    }

    /**
     * 设置banner或者取消设置
     *
     * @param goodsParam
     * @return
     */
    @PostMapping("/set-promotion")
    @RequiresPermissions({"operateData:goods:list:promotion"})
    @Transactional
    public CommonResult setPromotion(@RequestBody @Validated({GoodsParam.SetPromotion.class}) GoodsParam goodsParam) {
        GoodsVo goodsVo = vcGoodsGoodsMapper.selectByGoodId(goodsParam.getId());
        if (goodsVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "所选商品不存在");
        }
        //是否已存在促销商品
        VcGoodsGoods goodsGoodsDb = vcGoodsGoodsService.lambdaQuery().eq(VcGoodsGoods::getIsPromotion, 1).eq(VcGoodsGoods::getIsDelete, 0).one();
        if (Objects.nonNull(goodsGoodsDb) && !goodsParam.getId().equals(goodsGoodsDb.getId())) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "促销商品已存在");
        }

        //如果本次为上架操作 将判断当前商品分类是否存在 且状态是否正常
        if (goodsParam.getIsPromotion() == 1) {
            VcGoodsType vcGoodsType = vcGoodsTypeService.lambdaQuery().eq(VcGoodsType::getId, goodsVo.getType()).one();
            if (vcGoodsType == null || vcGoodsType.getIsDelete() != 0) {
                return CommonResult.error(HttpStatus.BAD_REQUEST, "当前商品分类不存在或已删除，商品无法设置成促销");
            }
        }
        vcGoodsGoodsService.lambdaUpdate().eq(VcGoodsGoods::getId, goodsParam.getId())
                .set(VcGoodsGoods::getIsPromotion, goodsParam.getIsPromotion()).update();
        return CommonResult.success();
    }


    /**
     * 商品上下架
     *
     * @param goodsParam
     * @return
     */
    @PostMapping("/up-down")
    @RequiresPermissions({"operateData:goods:list:upAndDown"})
    @Transactional
    public CommonResult upDown(@RequestBody @Validated({GoodsParam.UpAndDown.class}) GoodsParam goodsParam) {
        GoodsVo goodsVo = vcGoodsGoodsMapper.selectByGoodId(goodsParam.getId());
        if (goodsVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "所选商品不存在");
        }


        //如果本次为上架操作 将判断当前商品分类是否存在 且状态是否正常
        if (goodsParam.getStatus() == 1) {
            VcGoodsType vcGoodsType = vcGoodsTypeService.lambdaQuery().eq(VcGoodsType::getId, goodsVo.getType()).one();
            if (vcGoodsType == null || vcGoodsType.getIsDelete() != 0) {
                return CommonResult.error(HttpStatus.BAD_REQUEST, "当前商品分类不存在或已删除，商品无法上架");
            }
        }

        vcGoodsGoodsService.lambdaUpdate().eq(VcGoodsGoods::getId, goodsParam.getId())
                .set(VcGoodsGoods::getStatus, goodsParam.getStatus()).update();
        return CommonResult.success();
    }


    /**
     * 编辑商品信息
     *
     * @param goodsParam
     * @return
     */
    @PostMapping("/edit")
    @RequiresPermissions({"operateData:goods:list:edit"})
    public CommonResult edit(@RequestBody @Validated({GoodsParam.Edit.class}) GoodsParam goodsParam) {
        GoodsVo goodsVo = vcGoodsGoodsMapper.selectByGoodId(goodsParam.getId());
        if (goodsVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "所选商品不存在");
        }


        //设置商家id
        goodsParam.setShopId(goodsVo.getShopId());

        return vcGoodsGoodsService.edit(goodsParam, goodsVo);
    }


    /**
     * 删除商品信息
     *
     * @param goodsParam
     * @return
     */
    @PostMapping("/del")
    @RequiresPermissions({"operateData:goods:list:del"})
    public CommonResult del(@RequestBody @Validated({GoodsParam.Del.class}) GoodsParam goodsParam) {
        GoodsVo goodsVo = vcGoodsGoodsMapper.selectByGoodId(goodsParam.getId());
        if (goodsVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "所选商品不存在");
        }

        return vcGoodsGoodsService.del(goodsParam.getId(), goodsVo);
    }


    /**
     * 商品分类列表
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @param shopId
     * @return
     */
    @GetMapping("/type-list")
    @RequiresPermissions({"operateData:goods:type:list"})
    public CommonResult typeList(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, @RequestParam(defaultValue = "10", value = "pageSize") Integer pageSize,
                                 @RequestParam(defaultValue = "", value = "name") String name, @RequestParam(defaultValue = "", value = "shopId") Integer shopId,
                                 @RequestParam(value = "parentId",required = false) Integer parentId) {
        Integer agentId = getAgentId();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (shopId == null) {
            shopId = loginUser.getShopId();
        }
        Page<TypeVo> page = new Page<>(pageNum, pageSize);
        List<TypeVo> list = vcGoodsTypeMapper.adminSelect(page, name, agentId, shopId, parentId);
        return CommonResult.success(page.setRecords(list));
    }


    /**
     * 添加商品分类
     *
     * @param goodsTyepParam
     * @return
     */
    @PostMapping("/type-add")
    @RequiresPermissions({"operateData:goods:type:add"})
    @Transactional
    public CommonResult typeAdd(@RequestBody @Validated({GoodsTyepParam.Add.class}) GoodsTyepParam goodsTyepParam) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        goodsTyepParam.setShopId(loginUser.getShopId());

        VcShopShop vcShopShop = vcShopShopService.lambdaQuery().eq(VcShopShop::getId, goodsTyepParam.getShopId()).one();
        if (vcShopShop == null || vcShopShop.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "随选商铺不存在");
        }

        //查询当前商家分类数量
        Long serial = vcGoodsTypeService.lambdaQuery().eq(VcGoodsType::getIsDelete, 0).eq(VcGoodsType::getShopId, goodsTyepParam.getShopId()).count();

        VcGoodsType vcGoodsType = new VcGoodsType();
        vcGoodsType.setShopId(goodsTyepParam.getShopId());
        vcGoodsType.setName(goodsTyepParam.getName());
        vcGoodsType.setParentId(goodsTyepParam.getParentId());
        vcGoodsType.setStatus(1);
        vcGoodsType.setSerial(serial.intValue() + 1);
        vcGoodsType.setAgentId(vcShopShop.getAgentId());
        vcGoodsTypeService.save(vcGoodsType);
        return CommonResult.success();
    }


    /**
     * 编辑商品分类
     *
     * @param addService
     * @return
     */
    @PostMapping("/type-edit")
    @RequiresPermissions({"operateData:goods:type:edit"})
    @Transactional
    public CommonResult typeEdit(@RequestBody @Validated({GoodsParam.Edit.class}) GoodsTyepParam addService) {
        //查询所属商品分类是否存在
        VcGoodsType vcGoodsType = vcGoodsTypeService.lambdaQuery().eq(VcGoodsType::getId, addService.getId()).one();
        if (vcGoodsType == null || vcGoodsType.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "商品分类不存在");
        }

        if(addService.getParentId()!=null){
            vcGoodsType.setParentId(addService.getParentId());
        }
        vcGoodsType.setName(addService.getName());
        if (addService.getSerial() != null) {
            vcGoodsType.setSerial(addService.getSerial());
        }
        vcGoodsTypeService.updateById(vcGoodsType);

        //重新排序
        typeSorting(vcGoodsType.getShopId());
        return CommonResult.success();
    }


    /**
     * 商品分类排序
     *
     * @param shopId
     */
    private void typeSorting(Integer shopId) {
        List<VcGoodsType> list = vcGoodsTypeService.lambdaQuery().eq(VcGoodsType::getIsDelete, 0).eq(VcGoodsType::getShopId, shopId)
                .orderByAsc(VcGoodsType::getSerial).list();
        Integer serial = 1;
        for (VcGoodsType vcGoodsType : list) {
            vcGoodsTypeService.lambdaUpdate().eq(VcGoodsType::getId, vcGoodsType.getId())
                    .set(VcGoodsType::getSerial, serial).update();

            serial++;
        }
    }


    /**
     * 分类上下架
     *
     * @param addService
     * @return
     */
    @PostMapping("/type-up-down")
    @RequiresPermissions({"operateData:goods:type:upAndDown"})
    @Transactional
    public CommonResult typeUpDown(@RequestBody @Validated({GoodsParam.UpAndDown.class}) GoodsTyepParam addService) {
        //查询所属商品分类是否存在
        VcGoodsType vcGoodsType = vcGoodsTypeService.lambdaQuery().eq(VcGoodsType::getId, addService.getId()).one();
        if (vcGoodsType == null || vcGoodsType.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "商品分类不存在");
        }


        vcGoodsType.setStatus(addService.getStatus());
        vcGoodsTypeService.updateById(vcGoodsType);
        return CommonResult.success();
    }


    /**
     * 分类删除
     *
     * @param addService
     * @return
     */
    @PostMapping("/type-del")
    @RequiresPermissions({"operateData:goods:type:del"})
    @Transactional
    public CommonResult del(@RequestBody @Validated({GoodsParam.Del.class}) GoodsTyepParam addService) {
        //查询所属商品分类是否存在
        VcGoodsType vcGoodsType = vcGoodsTypeService.lambdaQuery().eq(VcGoodsType::getId, addService.getId()).one();
        if (vcGoodsType == null || vcGoodsType.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "商品分类不存在");
        }


        vcGoodsType.setIsDelete(1);
        vcGoodsType.setDeleteTime(LocalDateTime.now());
        vcGoodsTypeService.updateById(vcGoodsType);

        //删除分类后  将其下的商品状态改为下架状态
        vcGoodsGoodsService.lambdaUpdate().eq(VcGoodsGoods::getIsDelete, 0).eq(VcGoodsGoods::getType, addService.getId())
                .set(VcGoodsGoods::getStatus, 0).update();

        //重新排序
        typeSorting(vcGoodsType.getShopId());
        return CommonResult.success();
    }


    /**
     * 商铺商铺分类
     *
     * @param shopId
     * @return
     */
    @GetMapping("/shop-type")
    public CommonResult shopType(@RequestParam(value = "shopId", defaultValue = "") Integer shopId,
                                 @RequestParam(value = "parentId",required = false) Integer parentId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (shopId == null) {
            shopId = loginUser.getShopId();
        }

        List<TypeVo> list = vcGoodsTypeMapper.selectByShopId(shopId,parentId);
        return CommonResult.success(list);
    }


    /**
     * 获取门店商品分类和对应的商品信息
     *
     * @param shopId
     * @param groupId
     * @return
     */
    @GetMapping("/type-and-goods")
    public CommonResult typeAndGoods(@RequestParam(value = "shopId", defaultValue = "") Integer shopId,
                                     @RequestParam(value = "groupId", defaultValue = "0") Integer groupId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (shopId == null) {
            shopId = loginUser.getShopId();
        }

        List<Map<String, Object>> list = vcGoodsTypeMapper.selectTypeNameByShopId(shopId);
        for (Map<String, Object> map : list) {
            map.put("goodsList", vcGoodsGoodsMapper.selectByTypeId((Integer) map.get("id")));
        }
        return CommonResult.success(list);
    }


    /**
     * 商品分类排序
     *
     * @param goodsTyepParam
     * @return
     */
    @PostMapping("/type-sort")
    @Transactional
    public CommonResult typeSort(@RequestBody @Validated({GoodsTyepParam.Sort.class}) GoodsTyepParam goodsTyepParam) {
        Integer firstId = goodsTyepParam.getIds().get(0);
        VcGoodsType vcGoodsType = vcGoodsTypeService.lambdaQuery().eq(VcGoodsType::getId, firstId).one();
        if (vcGoodsType == null || vcGoodsType.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "所选商品分类不存在");
        }


        //查询其他商品分类是否为当前客户
        Long count = vcGoodsTypeService.lambdaQuery().eq(VcGoodsType::getIsDelete, 0).eq(VcGoodsType::getShopId, vcGoodsType.getShopId())
                .in(VcGoodsType::getId, goodsTyepParam.getIds()).eq(VcGoodsType::getStatus, 1).count();
        if (count != goodsTyepParam.getIds().size()) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "传入商品分类有误");
        }

        //修改商品分类序号 从0开始
        List<Integer> ids = goodsTyepParam.getIds();
        Integer serial = 0;
        for (Integer id : ids) {
            vcGoodsTypeService.lambdaUpdate().eq(VcGoodsType::getId, id).set(VcGoodsType::getSerial, serial).update();
            serial++;
        }
        return CommonResult.success();
    }


    /**
     * 商品排序
     *
     * @param goodsParam
     * @return
     */
    @PostMapping("/goods-sort")
    @Transactional
    public CommonResult goodsSort(@RequestBody @Validated({GoodsParam.Sort.class}) GoodsParam goodsParam) {
        Integer firstId = goodsParam.getIds().get(0);
        VcGoodsGoods vcGoodsGoods = vcGoodsGoodsService.lambdaQuery().eq(VcGoodsGoods::getId, firstId).one();
        if (vcGoodsGoods == null || vcGoodsGoods.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "所选商品不存在");
        }


        //查询其他商品分类是否为当前客户
        Long count = vcGoodsGoodsService.lambdaQuery().eq(VcGoodsGoods::getIsDelete, 0).eq(VcGoodsGoods::getShopId, vcGoodsGoods.getShopId())
                .in(VcGoodsGoods::getId, goodsParam.getIds()).eq(VcGoodsGoods::getStatus, 1).count();
        if (count != goodsParam.getIds().size()) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "传入商品有误");
        }

        //修改商品分类序号 从0开始
        List<Integer> ids = goodsParam.getIds();
        Integer serial = 0;
        for (Integer id : ids) {
            vcGoodsGoodsService.lambdaUpdate().eq(VcGoodsGoods::getId, id).set(VcGoodsGoods::getSerial, serial).update();
            serial++;
        }
        return CommonResult.success();
    }


}

package com.vchaoxi.controller.app;


import com.chargehub.common.core.constant.HttpStatus;
import com.chargehub.common.core.web.controller.BaseController;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.vchaoxi.entity.VcGoodsGoods;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.entity.WinesCar;
import com.vchaoxi.entity.WinesGoodsSpecification;
import com.vchaoxi.param.WinesCarParam;
import com.vchaoxi.service.IVcGoodsGoodsService;
import com.vchaoxi.service.IVcUserUserService;
import com.vchaoxi.service.IWinesCarService;
import com.vchaoxi.service.IWinesGoodsSpecificationService;
import com.vchaoxi.service.impl.WinesCarServiceImpl;
import com.vchaoxi.vo.CommonResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 购物车 前端控制器
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-07-29
 */
@RestController
@RequestMapping("/app/wines-car")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WinesCarAppController extends BaseController {

    @Autowired
    private IWinesCarService iWinesCarService;
    @Autowired
    private IVcGoodsGoodsService iVcGoodsGoodsService;
    @Autowired
    private IWinesGoodsSpecificationService iWinesGoodsSpecificationService;

    @Autowired
    private IVcUserUserService vcUserUserService;

    @GetMapping("/list")
    @RequiresLogin(doIntercept = false)
    @Transactional
    public CommonResult list(@RequestParam(required = false) String group) {
        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();

        VcUserUser selUser = vcUserUserService.getById(vcUserUser.getId());

        List<WinesCar> winesCarList = iWinesCarService.lambdaQuery()
                .eq(WinesCar::getIsDelete, 0)
                .eq(WinesCar::getUserId, selUser.getId())
                .eq(StringUtils.isNotBlank(group), WinesCar::getGoodsGroup, group)
                .list();

        BigDecimal freight = BigDecimal.ZERO;
        BigDecimal totalGoodsPrice = BigDecimal.ZERO;
        for (WinesCar winesCar : winesCarList) {
            VcGoodsGoods goodsGoods = iVcGoodsGoodsService.getById(winesCar.getGoodsId());
            WinesGoodsSpecification goodsSpecification = iWinesGoodsSpecificationService.lambdaQuery()
                    .eq(WinesGoodsSpecification::getIsDelete, 0)
                    .eq(WinesGoodsSpecification::getId, winesCar.getSpecificationId()).one();
            if (goodsGoods == null || goodsGoods.getIsDelete() != 0 || goodsGoods.getStatus() != 1 || goodsSpecification == null || goodsSpecification.getIsDelete() != 0) {
                winesCar.setIsDelete(0);
                iWinesCarService.updateById(winesCar);
                continue;
            }
            BigDecimal goodsPrice = goodsSpecification.getAmount();
            if (selUser.getMemGrade() == 2) {
                goodsPrice = goodsSpecification.getMemAmount();
            }
            if (selUser.getMemGrade() == 3) {
                goodsPrice = goodsSpecification.getVipAmount();
            }
            if (selUser.getMemGrade() != 4 && winesCar.getChecked().equals(1)) {
                freight = freight.add(goodsGoods.getFreight().multiply(BigDecimal.valueOf(winesCar.getNumber())));
            }
            if (goodsPrice.compareTo(winesCar.getPrice()) != 0) {//如果价格有变化酒进行修改
                winesCar.setPrice(goodsPrice);
                iWinesCarService.updateById(winesCar);
            }
            totalGoodsPrice = totalGoodsPrice.add(goodsPrice);
        }

        List<WinesCar> filteredList = winesCarList.stream()
                .filter(winesCar -> winesCar.getIsDelete() == 0)
                .collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("totalPrice", iWinesCarService.checkCarsPrice(selUser.getId(), group).add(freight));
        map.put("carList", filteredList);
        map.put("freight", freight);
        map.put("totalGoodsPrice", totalGoodsPrice);
        return CommonResult.success(map);
    }

    @PostMapping("/add")
    @RequiresLogin(doIntercept = false)
    public CommonResult add(@RequestBody @Validated({WinesCarParam.Add.class}) WinesCarParam carParam) {
        VcGoodsGoods goodsGoods = iVcGoodsGoodsService.getById(carParam.getGoodsId());
        if (goodsGoods == null || goodsGoods.getIsDelete() != 0 || goodsGoods.getStatus() != 1) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "商品信息不存在或已下架");
        }
        WinesGoodsSpecification goodsSpecification = iWinesGoodsSpecificationService.lambdaQuery()
                .eq(WinesGoodsSpecification::getIsDelete, 0)
                .eq(WinesGoodsSpecification::getId, carParam.getSpecificationId()).one();
        if (goodsSpecification == null || goodsSpecification.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "未查询到该规格的商品");
        }

        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();

        VcUserUser selUser = vcUserUserService.getById(vcUserUser.getId());
        BigDecimal goodsPrice = goodsSpecification.getAmount();
        if (selUser.getMemGrade() == 2) {
            goodsPrice = goodsSpecification.getMemAmount();
        }
        if (selUser.getMemGrade() == 3) {
            goodsPrice = goodsSpecification.getVipAmount();
        }
        if (goodsPrice == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "该规格下价格不存在");
        }
        String group = carParam.getGroup();
        List<WinesCar> winesCarList = iWinesCarService.lambdaQuery()
                .eq(WinesCar::getIsDelete, 0)
                .eq(WinesCar::getUserId, selUser.getId())
                .eq(WinesCar::getGoodsId, carParam.getGoodsId())
                .eq(WinesCar::getSpecificationId, carParam.getSpecificationId())
                .eq(StringUtils.isNotBlank(group), WinesCar::getGoodsGroup, group)
                .list();
        if (CollectionUtils.isEmpty(winesCarList)) {//如果为空
            WinesCar winesCar = new WinesCar();
            winesCar.setUserId(selUser.getId());
            winesCar.setPrice(goodsPrice);
            winesCar.setNumber(carParam.getNumber());
            winesCar.setGoodsId(goodsGoods.getId());
            winesCar.setPicUrl(goodsGoods.getImg());
            winesCar.setGoodsName(goodsGoods.getName());
            winesCar.setSpecificationId(goodsSpecification.getId());
            winesCar.setSpecifications(goodsSpecification.getValue());
            winesCar.setChecked(1);
            winesCar.setGoodsGroup(group);
            iWinesCarService.save(winesCar);
        } else {
            WinesCar winesCar = winesCarList.get(0);
            Integer oriGoodsNum = winesCar.getNumber();
            winesCar.setPrice(goodsPrice);
            winesCar.setNumber(oriGoodsNum + carParam.getNumber());
            winesCar.setChecked(1);
            iWinesCarService.updateById(winesCar);
        }

        return CommonResult.success();
    }

    @PostMapping("/del")
    @RequiresLogin(doIntercept = false)
    public CommonResult del(@RequestBody @Validated({WinesCarParam.Del.class}) WinesCarParam carParam) {
        List<WinesCar> winesCarList = iWinesCarService.lambdaQuery().eq(WinesCar::getIsDelete, 0)
                .in(WinesCar::getId, carParam.getIds())
                .list();
        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();

        if (winesCarList.stream()
                .map(WinesCar::getUserId)
                .noneMatch(userId -> userId.equals(vcUserUser.getId()))) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "无权限操作该条购车信息");
        }
        for (WinesCar winesCar : winesCarList) {
            winesCar.setIsDelete(1);
        }
        iWinesCarService.updateBatchById(winesCarList);
        return CommonResult.success();
    }


    @PostMapping("/edit-check")
    @RequiresLogin(doIntercept = false)
    @Transactional
    public CommonResult editCheck(@RequestBody @Validated({WinesCarParam.Edit.class}) WinesCarParam carParam) {
        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();

        VcUserUser selUser = vcUserUserService.getById(vcUserUser.getId());
        WinesCar selWinesCar = iWinesCarService.getById(carParam.getId());
        VcGoodsGoods goodsGoods = iVcGoodsGoodsService.getById(selWinesCar.getGoodsId());
        if (goodsGoods == null || !goodsGoods.getIsDelete().equals(0) || !goodsGoods.getStatus().equals(1)) {
            selWinesCar.setIsDelete(1);
            iWinesCarService.updateById(selWinesCar);
            return CommonResult.error(HttpStatus.BAD_REQUEST, "选择的购物车条目商品已下架");
        }

        WinesGoodsSpecification goodsSpecification = iWinesGoodsSpecificationService.lambdaQuery()
                .eq(WinesGoodsSpecification::getIsDelete, 0)
                .eq(WinesGoodsSpecification::getId, selWinesCar.getSpecificationId()).one();
        if (goodsSpecification == null || goodsSpecification.getIsDelete() != 0) {
            selWinesCar.setIsDelete(1);
            iWinesCarService.updateById(selWinesCar);
            return CommonResult.error(HttpStatus.BAD_REQUEST, "该规格的商品不存在或已下架");
        }

        BigDecimal goodsPrice = goodsSpecification.getAmount();
        if (selUser.getMemGrade() == 2) {
            goodsPrice = goodsSpecification.getMemAmount();
        }
        if (selUser.getMemGrade() == 3) {
            goodsPrice = goodsSpecification.getVipAmount();
        }
        if (!carParam.getChecked().equals(selWinesCar.getChecked()) || !carParam.getNumber().equals(selWinesCar.getNumber()) || selWinesCar.getPrice().compareTo(goodsPrice) != 0) {
            selWinesCar.setChecked(carParam.getChecked());
            selWinesCar.setNumber(carParam.getNumber());
            selWinesCar.setPrice(goodsPrice);
            iWinesCarService.updateById(selWinesCar);
        }

        Map<String, Object> map = new HashMap<>();
        WinesCarServiceImpl.EditOneGoods editOneGoods = iWinesCarService.checkCarsPrice(selUser, carParam.getGroup());
        map.put("totalPrice", editOneGoods.getTotalPrice());
        map.put("freight", editOneGoods.getFreight());
        return CommonResult.success(map);
    }

    @PostMapping("/batch-edit")
    @RequiresLogin(doIntercept = false)
    public CommonResult batchEdit(@RequestBody @Validated({WinesCarParam.Batch.class}) WinesCarParam carParam) {
        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();
        String group = carParam.getGroup();
        VcUserUser selUser = vcUserUserService.getById(vcUserUser.getId());
        List<WinesCar> selWinesCarList = iWinesCarService.lambdaQuery()
                .eq(WinesCar::getIsDelete, 0)
                .eq(StringUtils.isNotBlank(group), WinesCar::getGoodsGroup, group)
                .eq(WinesCar::getUserId, selUser.getId()).list();
        BigDecimal freight = BigDecimal.ZERO;
        for (WinesCar winesCar : selWinesCarList) {
            VcGoodsGoods goodsGoods = iVcGoodsGoodsService.getById(winesCar.getGoodsId());
            WinesGoodsSpecification goodsSpecification = iWinesGoodsSpecificationService.lambdaQuery()
                    .eq(WinesGoodsSpecification::getIsDelete, 0)
                    .eq(WinesGoodsSpecification::getId, winesCar.getSpecificationId()).one();
            if (goodsGoods == null || !goodsGoods.getIsDelete().equals(0) || !goodsGoods.getStatus().equals(1) || goodsSpecification == null || goodsSpecification.getIsDelete() != 0) {
                winesCar.setIsDelete(0);
                continue;
            }
            BigDecimal goodsPrice = goodsSpecification.getAmount();
            if (selUser.getMemGrade() == 2) {
                goodsPrice = goodsSpecification.getMemAmount();
            }
            if (selUser.getMemGrade() == 3) {
                goodsPrice = goodsSpecification.getVipAmount();
            }
            if (goodsPrice.compareTo(winesCar.getPrice()) != 0) {
                winesCar.setPrice(goodsPrice);
            }
            if (selUser.getMemGrade() != 4 && carParam.getBatchType() != 0) {
                freight = freight.add(goodsGoods.getFreight().multiply(BigDecimal.valueOf(winesCar.getNumber())));
            }
            winesCar.setChecked(carParam.getBatchType());
        }
        iWinesCarService.updateBatchById(selWinesCarList);
        Map<String, Object> map = new HashMap<>();
        map.put("totalPrice", iWinesCarService.checkCarsPrice(selUser.getId(), group).add(freight));
        map.put("freight", freight);

        return CommonResult.success(map);

    }


}
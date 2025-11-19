
package com.vchaoxi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.common.core.constant.HttpStatus;
import com.chargehub.common.core.web.controller.BaseController;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.utils.SecurityUtils;
import com.vchaoxi.constant.SysConstant;
import com.vchaoxi.entity.VcAgentAgent;
import com.vchaoxi.entity.VcAgentWx;
import com.vchaoxi.entity.VcShopShop;
import com.vchaoxi.entity.VcShopWx;
import com.vchaoxi.mapper.VcShopShopMapper;
import com.vchaoxi.param.VcShopShopParam;
import com.vchaoxi.param.VcShopWxParam;
import com.vchaoxi.service.IVcAgentAgentService;
import com.vchaoxi.service.IVcAgentWxService;
import com.vchaoxi.service.IVcShopShopService;
import com.vchaoxi.service.IVcShopWxService;
import com.vchaoxi.vo.CommonResult;
import com.vchaoxi.vo.VcShopShopVo;
import com.vchaoxi.vo.VcShopWxVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShopController extends BaseController {


    @Value("${wx.mp.authUrl:}")
    private String mpAuthUrl;

    @Autowired
    private IVcShopShopService vcShopShopService;

    @Autowired
    private IVcAgentAgentService vcAgentAgentService;
    @Autowired
    private IVcShopWxService vcShopWxService;


    @Autowired
    private IVcAgentWxService vcAgentWxService;


    private final VcShopShopMapper vcShopShopMapper;


    /**
     * 查询全部的商家列表*
     *
     * @return
     */
    @GetMapping("/all")
    public CommonResult all() {


        List<VcShopShopVo> list = vcShopShopMapper.selectAllShop(null);

        return CommonResult.success(list);
    }

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
     * 获取商家列表*
     *
     * @param pageNum
     * @param pageSize
     * @param shopName
     * @return
     */
    @GetMapping("/list")
    @RequiresPermissions({"operateData:customer:list"})
    public CommonResult list(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, @RequestParam(defaultValue = "10", value = "pageSize") Integer pageSize,
                             @RequestParam(defaultValue = "", value = "shopName") String shopName) {
        Integer agentId = getAgentId();

        Page<VcShopShopVo> page = new Page(pageNum, pageSize);
        List<VcShopShopVo> list = vcShopShopMapper.erpSelectList(page, agentId, shopName);

        return CommonResult.success(page.setRecords(list));
    }


    /**
     * 添加商家信息*
     *
     * @param vcShopShopParam
     * @return
     */
    @PostMapping("/add")
    @RequiresPermissions({"operateData:customer:add"})
    @Transactional
    public CommonResult add(@RequestBody @Validated({VcShopShopParam.Add.class}) VcShopShopParam vcShopShopParam) {
        //如果前端传来的代理商id不为0  则判断当前代理商是否存在
        if (vcShopShopParam.getAgentId() != null && vcShopShopParam.getAgentId() != 0) {
            Long count = vcAgentAgentService.lambdaQuery().eq(VcAgentAgent::getIsDelete, 0).eq(VcAgentAgent::getId, vcShopShopParam.getAgentId()).count();
            if (count == 0) {
                return CommonResult.error(HttpStatus.BAD_REQUEST, "当前代理商不存在");
            }
        } else {
            vcShopShopParam.setAgentId(0);
        }




        //添加客户信息
        VcShopShop vcShopShop = new VcShopShop();
        BeanUtils.copyProperties(vcShopShopParam, vcShopShop);
        vcShopShopService.save(vcShopShop);

        VcAgentWx vcAgentWx = vcAgentWxService.lambdaQuery().eq(VcAgentWx::getIsDelete, 0).eq(VcAgentWx::getAgentId, vcShopShopParam.getAgentId()).one();
        //为客户添加微信相关配置信息
        VcShopWx vcShopWx = new VcShopWx();
        BeanUtils.copyProperties(vcAgentWx, vcShopWx);
        vcShopWx.setShopId(vcShopShop.getId());
        vcShopWx.setInsertTime(LocalDateTime.now());
        vcShopWx.setUpdateTime(LocalDateTime.now());
        vcShopWx.setId(null);
        vcShopWxService.save(vcShopWx);

        //为商家添加管理员账号信息

        return CommonResult.success();
    }


    /**
     * 获取商家详情*
     *
     * @param id
     * @return
     */
    @GetMapping("/info")
    @RequiresPermissions({"operateData:customer:getInfo", "systemManage:info:getInfo"})
    public CommonResult list(@RequestParam(defaultValue = "", value = "id") Integer id) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        id = loginUser.getShopId();


        VcShopShopVo vcShopShopVo = vcShopShopMapper.erpSelectById(id);
        if (vcShopShopVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "当前客户信息不存在");
        }

        VcShopWx vcShopWx = vcShopWxService.lambdaQuery().eq(VcShopWx::getIsDelete, 0).eq(VcShopWx::getShopId, id).one();
        VcShopWxVo vcShopWxVo = new VcShopWxVo();
        BeanUtils.copyProperties(vcShopWx, vcShopWxVo);

        Map<String, Object> map = new HashMap<>();
        map.put("shopInfo", vcShopShopVo);
        map.put("wxInfo", vcShopWxVo);
        map.put("mpAuthUrl", mpAuthUrl);
        return CommonResult.success(map);
    }


    /**
     * 编辑商家信息*
     *
     * @param vcShopShopParam
     * @return
     */
    @PostMapping("/edit-shop")
    @RequiresPermissions({"operateData:customer:edit"})
    @Transactional
    public CommonResult editShop(@RequestBody @Validated({VcShopShopParam.Edit.class}) VcShopShopParam vcShopShopParam) {
        VcShopShop vcShopShop = vcShopShopService.lambdaQuery()
                .eq(VcShopShop::getIsDelete, 0)
                .eq(VcShopShop::getId, vcShopShopParam.getId()).one();
        if (vcShopShop == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "当前客户信息不存在");
        }


        BeanUtils.copyProperties(vcShopShopParam, vcShopShop);
        vcShopShopService.updateById(vcShopShop);





        return CommonResult.success();
    }

    /**
     * 删除信息*
     *
     * @param vcShopShopParam
     * @return
     */
    @PostMapping("/del")
    @RequiresPermissions({"operateData:customer:del"})
    @Transactional
    public CommonResult del(@RequestBody @Validated({VcShopShopParam.Del.class}) VcShopShopParam vcShopShopParam) {


        return CommonResult.success();
    }


    /**
     * 客户商户号配置
     *
     * @param vcShopWxParam
     * @return
     */
    @PostMapping("/shop-mch-conf")
    @RequiresPermissions({"operateData:customer:indepConf", "systemManage:info:shopmchconf"})
    public CommonResult shopMchConf(@RequestBody @Validated({VcShopWxParam.EditMchInfo.class}) VcShopWxParam vcShopWxParam) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        vcShopWxParam.setShopId(loginUser.getShopId());


        VcShopShopVo vcShopShopVo = vcShopShopMapper.erpSelectById(vcShopWxParam.getShopId());
        if (vcShopShopVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "当前客户信息不存在");
        }

        //更新相关配置信息
        vcShopWxService.lambdaUpdate().eq(VcShopWx::getIsDelete, 0)
                .eq(VcShopWx::getShopId, vcShopWxParam.getShopId())
                .set(VcShopWx::getMchId, vcShopWxParam.getMchId())
                .set(VcShopWx::getMchKey, vcShopWxParam.getMchKey())
                .set(VcShopWx::getMchCertName, vcShopWxParam.getMchCertName())
                .set(VcShopWx::getCustomEdit, 1).update();

        return CommonResult.success();
    }


    /**
     * 客户公众号配置
     *
     * @param vcShopWxParam
     * @return
     */
    @PostMapping("/shop-mp-conf")
    @RequiresPermissions({"operateData:customer:indepConf", "systemManage:info:shopmpconf"})
    public CommonResult shopMpConf(@RequestBody @Validated({VcShopWxParam.EditMpInfo.class}) VcShopWxParam vcShopWxParam) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        vcShopWxParam.setShopId(loginUser.getShopId());

        VcShopShopVo vcShopShopVo = vcShopShopMapper.erpSelectById(vcShopWxParam.getShopId());
        if (vcShopShopVo == null) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "当前客户信息不存在");
        }

        //更新相关配置信息
        vcShopWxService.lambdaUpdate().eq(VcShopWx::getIsDelete, 0)
                .eq(VcShopWx::getShopId, vcShopWxParam.getShopId())
                .set(VcShopWx::getMpAppid, vcShopWxParam.getMpAppId())
                .set(VcShopWx::getMpMainBody, vcShopWxParam.getMpMainBody())
                .set(VcShopWx::getMpName, vcShopWxParam.getMpName())
                .set(VcShopWx::getMpQr, vcShopWxParam.getMpQr())
                .set(VcShopWx::getCustomEdit, 1).update();

        return CommonResult.success();
    }

}

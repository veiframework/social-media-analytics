package com.chargehub.biz.appuser.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;

import com.chargehub.admin.api.domain.dto.WxLoginDto;
import com.chargehub.biz.appuser.domain.AppUser;
import com.chargehub.biz.appuser.dto.AppUserDto;
import com.chargehub.biz.appuser.dto.AppUserInfoDto;
import com.chargehub.biz.appuser.dto.AppUserQueryDto;
import com.chargehub.biz.appuser.mapper.AppUserMapper;
import com.chargehub.biz.appuser.vo.AppUserVo;
import com.chargehub.common.core.utils.NicknameGenerator;
import com.chargehub.common.redis.service.RedisService;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务类
 *
 * @author system
 * @since 2024-03-21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppUserService extends AbstractZ9CrudServiceImpl<AppUserMapper, AppUser> {

    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;

    @Autowired(required = false)
    private UserInfoExtension userInfoExtension;

    @Autowired
    private RedisService redisService;

    public AppUserService(AppUserMapper appUserMapper) {
        super(appUserMapper);
    }

    @Override
    public Class<AppUserDto> doGetDtoClass() {
        return AppUserDto.class;
    }

    @Override
    public Class<AppUserVo> doGetVoClass() {
        return AppUserVo.class;
    }

    @Override
    public Class<AppUserQueryDto> queryDto() {
        return AppUserQueryDto.class;
    }

    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }

    @Override
    public String excelName() {
        return "用户管理";
    }

    @Override
    public String duplicateMsg() {
        return "用户已存在";
    }

    public void updateAppInfo(AppUserInfoDto userAppInfoDto) {
        this.baseMapper.lambdaUpdate()
                .set(AppUser::getAvatar, userAppInfoDto.getAvatar())
                .set(AppUser::getNickname, userAppInfoDto.getNickname())
                .set(StringUtils.isNotBlank(userAppInfoDto.getPhone()), AppUser::getPhone, userAppInfoDto.getPhone())
                .eq(AppUser::getId, userAppInfoDto.getId())
                .update();
    }

    public AppUserVo loginByOpenId(WxLoginDto dto) {
        String unionId = dto.getUnionId();
        String openId = dto.getOpenId();
        return redisService.lock("app:login:lock:key", locked -> {
            Assert.isTrue(locked, "登录过于频繁");
            boolean haveUnionId = StringUtils.isNotBlank(unionId);
            AppUser user;
            if (haveUnionId) {
                user = this.baseMapper.lambdaQuery().eq(AppUser::getUnionid, unionId).one();
                if (user == null) {
                    user = this.baseMapper.lambdaQuery().eq(AppUser::getOpenid, openId).one();
                    if (user != null) {
                        user.setUnionid(unionId);
                        this.baseMapper.lambdaUpdate()
                                .set(AppUser::getUnionid, unionId)
                                .eq(AppUser::getId, user.getId())
                                .update();
                    }
                }
            } else {
                user = this.baseMapper.lambdaQuery().eq(AppUser::getOpenid, openId).one();
            }
            if (user != null) {
                AppUserVo appUserVo = BeanUtil.copyProperties(user, AppUserVo.class);
                if (userInfoExtension != null) {
                    ObjectNode extendParams = userInfoExtension.getExtendParams(user.getId(), appUserVo);
                    appUserVo.setExtendParams(extendParams);
                }
                return appUserVo;
            } else {
                AppUser newUser = new AppUser();
                newUser.setOpenid(openId);
                newUser.setUnionid(haveUnionId ? unionId : openId);
                newUser.setNickname(NicknameGenerator.generateInterestingNickname());
                newUser.setPoints(0);
                newUser.setAvatar("https://img.alicdn.com/imgextra/i1/O1CN01vxzdFu1LYUHY9o0cC_!!6000000001311-2-tps-88-88.png");
                this.baseMapper.insert(newUser);
                AppUserVo appUserVo = BeanUtil.copyProperties(newUser, AppUserVo.class);
                if (userInfoExtension != null) {
                    ObjectNode extendParams = userInfoExtension.getExtendParams(newUser.getId(), appUserVo);
                    appUserVo.setExtendParams(extendParams);
                }
                return appUserVo;
            }
        }, 30);
    }

}
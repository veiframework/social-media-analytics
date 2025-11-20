package com.chargehub.admin.account.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.dto.SocialMediaAccountDto;
import com.chargehub.admin.account.dto.SocialMediaAccountQueryDto;
import com.chargehub.admin.account.dto.SocialMediaAccountShareLinkDto;
import com.chargehub.admin.account.mapper.SocialMediaAccountMapper;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import com.chargehub.common.security.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SocialMediaAccountService extends AbstractZ9CrudServiceImpl<SocialMediaAccountMapper, SocialMediaAccount> {

    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;

    @Autowired
    private DataSyncManager dataSyncManager;

    public SocialMediaAccountService(SocialMediaAccountMapper baseMapper) {
        super(baseMapper);
    }

    @Override
    public void create(Z9CrudDto<SocialMediaAccount> dto) {
        Long userId = SecurityUtils.getUserId();
        SocialMediaAccountDto socialMediaAccountDto = (SocialMediaAccountDto) dto;
        if (StringUtils.isBlank(socialMediaAccountDto.getUserId())) {
            socialMediaAccountDto.setUserId(userId + "");
        }
        super.create(dto);
    }

    public void createByShareLink(SocialMediaAccountShareLinkDto dto) {
        String shareLink = dto.getShareLink();
        SocialMediaPlatformEnum.SecUser secUser = SocialMediaPlatformEnum.parseSecUserId(shareLink);
        Assert.notNull(secUser, "第三方用户信息解析失败");
        String secUserIdId = secUser.getId();
        //获取社交平台信息
        SocialMediaUserInfo socialMediaUserInfo = dataSyncManager.getSocialMediaUserInfo(secUser.getPlatformEnum(), secUserIdId);
        //TODO 直接获取社交平台accessToken
        String nickname = socialMediaUserInfo.getNickname();
        String uid = socialMediaUserInfo.getUid();
        SocialMediaAccountDto socialMediaAccountDto = new SocialMediaAccountDto();
        socialMediaAccountDto.setSecUid(secUserIdId);
        socialMediaAccountDto.setPlatformId(secUser.getPlatform());
        socialMediaAccountDto.setUserId(dto.getUserId());
        socialMediaAccountDto.setUid(uid);
        socialMediaAccountDto.setNickname(nickname);
        this.create(socialMediaAccountDto);
    }

    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return SocialMediaAccountDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return SocialMediaAccountVo.class;
    }

    @Override
    public Class<? extends Z9CrudQueryDto<SocialMediaAccount>> queryDto() {
        return SocialMediaAccountQueryDto.class;
    }

    @Override
    public String excelName() {
        return "社交帐号管理";
    }
}

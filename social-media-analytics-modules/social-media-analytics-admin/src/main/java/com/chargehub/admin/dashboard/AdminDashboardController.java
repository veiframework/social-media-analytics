package com.chargehub.admin.dashboard;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.admin.account.dto.SocialMediaAccountQueryDto;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.vo.SocialMediaAccountStatisticVo;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@UnifyResult
@RequestMapping("/social-media/dashboard")
@RestController
public class AdminDashboardController {

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @ApiOperation("统计账号的数据")
    @GetMapping("/statistic/account")
    public IPage<SocialMediaAccountStatisticVo> getAccountStatistic(SocialMediaAccountQueryDto queryDto) {
        return this.socialMediaAccountService.getAccountStatistic(queryDto);
    }

    @ApiOperation("统计各个平台的数据")
    @GetMapping("/statistic/platform")
    public List<SocialMediaWorkVo> groupByUserIdAndPlatform() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return this.socialMediaWorkService.groupByUserIdAndPlatform(loginUser);
    }


}

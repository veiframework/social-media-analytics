package com.chargehub.admin.dashboard;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.admin.account.dto.SocialMediaAccountQueryDto;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.vo.SocialMediaAccountStatisticVo;
import com.chargehub.admin.groupuser.service.GroupUserService;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.common.security.annotation.UnifyResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

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

    @Autowired
    private GroupUserService groupUserService;

    @RequiresLogin
    @ApiOperation("统计账号的数据")
    @GetMapping("/statistic/account")
    public IPage<SocialMediaAccountStatisticVo> getAccountStatistic(SocialMediaAccountQueryDto queryDto) {
        Set<String> userIds = this.groupUserService.checkPurview();
        queryDto.setUserId(userIds);
        return this.socialMediaAccountService.getAccountStatistic(queryDto);
    }

    @RequiresLogin
    @ApiOperation("统计各个平台的数据")
    @GetMapping("/statistic/platform")
    public List<SocialMediaWorkVo> groupByUserIdAndPlatform() {
        Set<String> userIds = this.groupUserService.checkPurview();
        return this.socialMediaWorkService.groupByUserIdAndPlatform(userIds);
    }


}

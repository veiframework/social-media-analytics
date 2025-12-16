package com.chargehub.admin.statistic.service;

import com.chargehub.admin.groupuser.service.GroupUserService;
import com.chargehub.admin.work.dto.SocialMediaWorkQueryDto;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Service
public class StatisticService {

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Autowired
    private GroupUserService groupUserService;

    public List<SocialMediaWorkVo> getWorkRankByGroup(SocialMediaWorkQueryDto queryDto) {
        Set<String> userId = queryDto.getUserId();
        if (CollectionUtils.isEmpty(userId)) {
            Set<String> userIds = this.groupUserService.checkPurview();
            queryDto.setUserId(userIds);
        } else {
            Set<String> userIds = this.groupUserService.checkPurview(new ArrayList<>(userId).get(0));
            queryDto.setUserId(userIds);
        }
        return this.socialMediaWorkService.getPurviewPage(queryDto).getRecords();
    }


}

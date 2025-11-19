package com.vchaoxi.task;

import cn.hutool.core.date.DateUtil;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.service.IVcUserUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 19:22
 */
public class MallMemberTask {

    @Autowired
    private IVcUserUserService vcUserUserService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void execute() {
        //变更所有过期的会员
        vcUserUserService.lambdaUpdate()
                .le(VcUserUser::getMemberExpireDate, DateUtil.now())
                .set(VcUserUser::getMemGrade, 1)
                .update();

    }



}

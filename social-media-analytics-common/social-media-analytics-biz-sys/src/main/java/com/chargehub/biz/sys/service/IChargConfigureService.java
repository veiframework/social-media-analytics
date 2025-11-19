package com.chargehub.biz.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chargehub.biz.sys.domain.ChargConfigure;

import java.util.List;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/5/6 17:19
 * @Project：chargehub
 * @Package：com.chargehub.biz.sys.service
 * @Filename：IChargConfigureService
 */
public interface IChargConfigureService extends IService<ChargConfigure> {


    /**
     * 获取开通会员的城市id
     * @return
     */
    List<String> getOpenMemberCity();


    /**
     * 判断当前系统使用支付方式是否为通联支付
     * @return
     */
    Boolean useAllinpay();
}

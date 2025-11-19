package com.chargehub.biz.sys.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chargehub.biz.sys.domain.ChargConfigure;
import com.chargehub.biz.sys.mapper.ChargConfigureMapper;
import com.chargehub.biz.sys.service.IChargConfigureService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/5/6 17:20
 * @Project：chargehub
 * @Package：com.chargehub.biz.sys.service.impl
 * @Filename：ChargConfigureServiceImpl
 */
@Service
public class ChargConfigureServiceImpl extends ServiceImpl<ChargConfigureMapper, ChargConfigure> implements IChargConfigureService {


    /**
     * 获取开通会员的城市id
     * @return
     */
    @Override
    public List<String> getOpenMemberCity() {
        ChargConfigure chargConfigure = lambdaQuery().eq(ChargConfigure::getConfigName,"OPEN_MEMBER_CITY").last("LIMIT 1").one();
        if(chargConfigure == null){
            return new ArrayList<>();
        }
        return JSONObject.parseObject(chargConfigure.getDetail().toString(), List.class);
    }


    /**
     * 判断当前系统使用支付方式是否为通联支付
     * @return
     */
    @Override
    public Boolean useAllinpay() {
        ChargConfigure chargConfigure = lambdaQuery().eq(ChargConfigure::getConfigName,"ALLION_PAY").last("LIMIT 1").one();
        if(chargConfigure == null){
            return false;
        }
        return "1".equals(chargConfigure.getDetail().toString());
    }
}

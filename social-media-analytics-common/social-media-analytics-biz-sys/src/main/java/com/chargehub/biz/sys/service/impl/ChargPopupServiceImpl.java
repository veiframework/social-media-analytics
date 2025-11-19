package com.chargehub.biz.sys.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chargehub.biz.sys.domain.ChargPopup;
import com.chargehub.biz.sys.mapper.ChargPopupMapper;
import com.chargehub.biz.sys.service.IChargPopupService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 弹框管理
 */
@Service
public class ChargPopupServiceImpl extends ServiceImpl<ChargPopupMapper, ChargPopup> implements IChargPopupService {

    @Override
    public List<ChargPopup> selectChargPopup(ChargPopup chargPopup) {
        LambdaQueryWrapper<ChargPopup> queryWrapper = new LambdaQueryWrapper<>();
        if (chargPopup != null){
            if (ObjectUtil.isNotEmpty(chargPopup.getTitle())) {
                queryWrapper.like(ChargPopup::getTitle, chargPopup.getTitle());
            }
            if (ObjectUtil.isNotEmpty(chargPopup.getType())) {
                queryWrapper.like(ChargPopup::getType, chargPopup.getType());
            }
        }
        return list(queryWrapper);
    }
}

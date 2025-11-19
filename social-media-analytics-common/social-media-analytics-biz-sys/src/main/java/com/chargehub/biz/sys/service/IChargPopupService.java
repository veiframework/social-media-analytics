package com.chargehub.biz.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.chargehub.biz.sys.domain.ChargPopup;

import java.util.List;
/**
 * 弹框管理
 */
public interface IChargPopupService extends IService<ChargPopup> {


    public List<ChargPopup> selectChargPopup(ChargPopup chargPopup);
}

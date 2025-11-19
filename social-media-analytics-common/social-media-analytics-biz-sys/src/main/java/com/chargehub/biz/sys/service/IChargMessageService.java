package com.chargehub.biz.sys.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chargehub.biz.sys.domain.ChargMessage;
import com.chargehub.common.core.web.domain.AjaxResult;

import java.util.List;

/**
 * @Description: 留言管理
 * @Author: jeecg-boot
 * @Date:   2023-07-31
 * @Version: V1.0
 */
public interface IChargMessageService extends IService<ChargMessage> {

    public List<ChargMessage> selectChargMessageList(ChargMessage chargMessage);

    public IPage<ChargMessage> selectChargMessageList4User(ChargMessage chargMessage, Integer userId);

    public List<ChargMessage> selectRecoverlist(ChargMessage chargMessage);

    public AjaxResult reply(ChargMessage chargMessage);

}

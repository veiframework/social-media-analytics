package com.chargehub.biz.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chargehub.biz.sys.domain.ChargAnnouncementContent;

import java.util.List;

public interface IChargAnnouncementContentService extends IService<ChargAnnouncementContent> {

    /**
     * 查询公告内容列表
     *
     * @param chargAnnouncementContent 公告内容
     * @return 公告内容集合
     */
    List<ChargAnnouncementContent> selectChargAnnouncementContentList(ChargAnnouncementContent chargAnnouncementContent);

}

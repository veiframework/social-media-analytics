package com.chargehub.biz.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chargehub.biz.sys.domain.ChargAnnouncementContent;
import com.chargehub.biz.sys.mapper.ChargAnnouncementContentMapper;
import com.chargehub.biz.sys.service.IChargAnnouncementContentService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ChargAnnouncementContentServiceImpl extends ServiceImpl<ChargAnnouncementContentMapper, ChargAnnouncementContent> implements IChargAnnouncementContentService {

    /**
     * 查询公告内容列表
     *
     * @param chargAnnouncementContent 公告内容
     * @return 公告内容
     */
    @Override
    public List<ChargAnnouncementContent> selectChargAnnouncementContentList(ChargAnnouncementContent chargAnnouncementContent) {
        return getBaseMapper().selectChargAnnouncementContentList(chargAnnouncementContent);
    }

}

package com.chargehub.biz.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chargehub.biz.sys.domain.ChargAnnouncementContent;

import java.util.List;

;

/**
 * @Description: charg_announcement_content
 * @Author: jeecg-boot
 * @Date:   2023-07-31
 * @Version: V1.0
 */
public interface ChargAnnouncementContentMapper extends BaseMapper<ChargAnnouncementContent> {

    /**
     * 查询公告内容列表
     *
     * @param chargAnnouncementContent 公告内容
     * @return 公告内容集合
     */
    List<ChargAnnouncementContent> selectChargAnnouncementContentList(ChargAnnouncementContent chargAnnouncementContent);
}

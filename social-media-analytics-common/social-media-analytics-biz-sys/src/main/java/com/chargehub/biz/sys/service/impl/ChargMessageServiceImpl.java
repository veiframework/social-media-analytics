package com.chargehub.biz.sys.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chargehub.biz.sys.domain.ChargMessage;
import com.chargehub.biz.sys.mapper.ChargMessageMapper;
import com.chargehub.biz.sys.service.IChargMessageService;
import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.common.core.web.page.PageDomain;
import com.chargehub.common.core.web.page.TableSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 留言管理
 */
@Service
public class ChargMessageServiceImpl extends ServiceImpl<ChargMessageMapper, ChargMessage> implements IChargMessageService {

    @Override
    public List<ChargMessage> selectChargMessageList(ChargMessage chargMessage) {
        LambdaQueryWrapper<ChargMessage> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(chargMessage.getPhone())) {
            queryWrapper.like(ChargMessage::getPhone, chargMessage.getPhone());
        }
        if (ObjectUtil.isNotEmpty(chargMessage.getType())) {
            queryWrapper.eq(ChargMessage::getType, chargMessage.getType());
        }
        if (ObjectUtil.isEmpty(chargMessage.getPostId())) {
            queryWrapper.isNull(ChargMessage::getReplyId).isNull(ChargMessage::getPostId);
        }
        queryWrapper.orderByDesc(ChargMessage::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    public IPage<ChargMessage> selectChargMessageList4User(ChargMessage chargMessage, Integer userId) {
        LambdaQueryWrapper<ChargMessage> queryWrapper = new LambdaQueryWrapper<>();
        if(chargMessage != null){
            if (ObjectUtil.isNotEmpty(chargMessage.getPhone())) {
                queryWrapper.like(ChargMessage::getPhone, chargMessage.getPhone());
            }
            if (ObjectUtil.isNotEmpty(chargMessage.getType())) {
                queryWrapper.eq(ChargMessage::getType, chargMessage.getType());
            }
            if (ObjectUtil.isEmpty(chargMessage.getPostId())) {
                queryWrapper.isNull(ChargMessage::getReplyId).isNull(ChargMessage::getPostId);
            }
        }
        if(userId != null){
            queryWrapper.eq(ChargMessage::getUserId, userId);
        }

        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        Page<ChargMessage> page = new Page<>(pageNum, pageSize);
        return page(page, queryWrapper);
    }

    @Override
    public List<ChargMessage> selectRecoverlist(ChargMessage chargMessage) {
        LambdaQueryWrapper<ChargMessage> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(chargMessage.getId())) {
            queryWrapper.eq(ChargMessage::getReplyId, chargMessage.getId());
        }
        queryWrapper.orderByDesc(ChargMessage::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult reply(ChargMessage chargMessage) {
        save(chargMessage);

        /* 修改帖子状态 */
        ChargMessage chargMessage1 = new ChargMessage();
        chargMessage1.setId(chargMessage.getReplyId());
        chargMessage1.setType(chargMessage.getType());
        chargMessage1.setUpdateTime(chargMessage.getCreateTime());
        chargMessage1.setUpdateBy(chargMessage.getCreateBy());
        updateById(chargMessage1);


        return AjaxResult.success("修改成功");
    }
}

package com.chargehub.admin.work.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import com.chargehub.admin.work.domain.SocialMediaTopic;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.mapper.SocialMediaTopicMapper;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.event.Z9CreateEvent;
import com.chargehub.common.security.template.event.Z9DeleteEvent;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class SocialMediaTopicService extends AbstractZ9CrudServiceImpl<SocialMediaTopicMapper, SocialMediaTopic> {


    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;

    protected SocialMediaTopicService(SocialMediaTopicMapper baseMapper) {
        super(baseMapper);
    }

    @EventListener
    public void topicListenAfterCreateWork(Z9CreateEvent<SocialMediaWork> event) {
        List<SocialMediaWork> data = event.getData();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        for (SocialMediaWork datum : data) {
            String topics = datum.getTopics();
            if (StringUtils.isBlank(topics)) {
                continue;
            }
            String[] split = topics.split(",");
            for (String topic : split) {
                SocialMediaTopic socialMediaTopic = new SocialMediaTopic();
                socialMediaTopic.setTopic(topic);
                socialMediaTopic.setWorkId(datum.getId());
                socialMediaTopic.setTenantId(datum.getTenantId());
                this.baseMapper.insert(socialMediaTopic);
            }
        }
    }

    @EventListener
    public void topicListenDeleteWork(Z9DeleteEvent<SocialMediaWork> event) {
        List<String> data = event.getData();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        for (String datum : data) {
            this.baseMapper.lambdaUpdate()
                    .eq(SocialMediaTopic::getWorkId, datum)
                    .remove();
        }
    }


    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return null;
    }

    @Override
    public Class<?> doGetVoClass() {
        return null;
    }

    @Override
    public String excelName() {
        return "话题列表";
    }
}

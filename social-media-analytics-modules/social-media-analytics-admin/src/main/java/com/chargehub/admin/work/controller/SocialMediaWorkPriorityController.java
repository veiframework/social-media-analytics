package com.chargehub.admin.work.controller;

import com.chargehub.admin.work.dto.SocialMediaWorkPriorityDto;
import com.chargehub.admin.work.dto.SocialMediaWorkPriorityQueryDto;
import com.chargehub.admin.work.service.SocialMediaWorkPriorityService;
import com.chargehub.admin.work.vo.SocialMediaWorkPriorityVo;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.template.controller.AbstractZ9Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@UnifyResult
@RestController
@RequestMapping("/social-media/work-priority")
public class SocialMediaWorkPriorityController extends AbstractZ9Controller<SocialMediaWorkPriorityDto, SocialMediaWorkPriorityQueryDto, SocialMediaWorkPriorityVo, SocialMediaWorkPriorityService> {


    protected SocialMediaWorkPriorityController(SocialMediaWorkPriorityService crudService) {
        super(crudService);
    }


}

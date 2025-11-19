package com.chargehub.biz.agreement.controller;


import com.chargehub.biz.agreement.dto.AgreementDto;
import com.chargehub.biz.agreement.dto.AgreementQueryDto;
import com.chargehub.biz.agreement.service.AgreementService;
import com.chargehub.biz.agreement.vo.AgreementVo;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.template.controller.AbstractZ9Controller;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhanghaowei
 * @date 2025/08/08 16:45
 */
@Api(tags = "协议管理")
@RestController
@RequestMapping("/agreement")
@UnifyResult
public class AgreementController extends AbstractZ9Controller<AgreementDto, AgreementQueryDto, AgreementVo, AgreementService> {

    protected AgreementController(AgreementService crudService) {
        super(crudService);
    }

}

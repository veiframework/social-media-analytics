package com.chargehub.biz.agreement.controller;

import com.chargehub.biz.agreement.dto.AgreementQueryDto;
import com.chargehub.biz.agreement.service.AgreementService;
import com.chargehub.biz.agreement.vo.AgreementVo;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.common.security.annotation.UnifyResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2025/08/19 10:24
 */
@Api(tags = "APP - 协议管理")
@RestController
@RequestMapping("/agreement/app")
@UnifyResult
public class AgreementAppController {

    @Autowired
    private AgreementService agreementService;

    @GetMapping("/list")
    @RequiresLogin(doIntercept = false)
    @ApiOperation("获取协议列表")
    public List<AgreementVo> getAgreementList() {
        AgreementQueryDto queryDto = new AgreementQueryDto();
        return (List<AgreementVo>) agreementService.getAll(queryDto);
    }

}

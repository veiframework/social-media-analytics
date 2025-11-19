package com.chargehub.z9.server.controller;

import com.chargehub.common.security.template.controller.AbstractZ9Controller;
import com.chargehub.z9.server.dto.Z9PaymentConfigDto;
import com.chargehub.z9.server.dto.Z9PaymentConfigQueryDto;
import com.chargehub.z9.server.service.Z9PaymentConfigService;
import com.chargehub.z9.server.vo.Z9PaymentConfigVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhanghaowei
 * @date 2025/05/19 18:14
 */
@Tag(name = "支付配置管理", description = "支付配置管理")
@RestController
@RequestMapping("/payment/config")
public class Z9PaymentConfigController extends AbstractZ9Controller<Z9PaymentConfigDto, Z9PaymentConfigQueryDto, Z9PaymentConfigVo, Z9PaymentConfigService> {

    public Z9PaymentConfigController(Z9PaymentConfigService crudService) {
        super(crudService);
    }

}

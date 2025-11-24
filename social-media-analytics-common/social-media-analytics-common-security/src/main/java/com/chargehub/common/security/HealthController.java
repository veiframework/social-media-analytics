package com.chargehub.common.security;

import com.chargehub.common.core.web.domain.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@RestController
@RequestMapping("/health")
public class HealthController {


    @GetMapping("/1992774542232305665")
    public AjaxResult health() {
        return AjaxResult.success();
    }


}

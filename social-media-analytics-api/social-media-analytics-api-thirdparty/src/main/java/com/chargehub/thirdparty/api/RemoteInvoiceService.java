package com.chargehub.thirdparty.api;

import com.alibaba.fastjson2.JSONObject;
import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.thirdparty.api.domain.dto.InvoiceDto;
import com.chargehub.thirdparty.api.domain.vo.invoice.InvoiceVo;
import com.chargehub.thirdparty.api.domain.vo.invoice.InvoicingVo;
import com.chargehub.thirdparty.api.factory.RemoteInvoiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 18:27
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api
 * @Filename：RemoteInvoiceService
 */
@FeignClient(contextId = "remoteInvoiceService", value = ServiceNameConstants.THIRD_PARTY_SERVICE, fallbackFactory = RemoteInvoiceFallbackFactory.class)
public interface RemoteInvoiceService {


    /**
     * 申请开发票
     *
     * @return
     */
    @PostMapping("/invoice")
    public InvoicingVo invoicing(@RequestBody @Validated InvoiceDto invoiceDto);


    /**
     * 根据发票号查询发票
     *
     * @param number
     * @param qyId
     * @return
     */
    @GetMapping("/invoice/{number}")
    public InvoiceVo selectByInvoiceNumber(@PathVariable("number") String number, @RequestParam(value = "qyId", required = false) String qyId);


    @PostMapping("/invoice/addEcg")
    public JSONObject addEcg(@RequestBody Map<String, Object> map);

}

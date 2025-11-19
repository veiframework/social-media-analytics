package com.chargehub.thirdparty.api.factory;

import com.alibaba.fastjson2.JSONObject;
import com.chargehub.thirdparty.api.RemoteInvoiceService;
import com.chargehub.thirdparty.api.domain.dto.InvoiceDto;
import com.chargehub.thirdparty.api.domain.vo.invoice.InvoiceVo;
import com.chargehub.thirdparty.api.domain.vo.invoice.InvoicingVo;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 18:27
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.factory
 * @Filename：RemoteInvoiceFallbackFactory
 */
@Component
public class RemoteInvoiceFallbackFactory implements FallbackFactory<RemoteInvoiceService> {
    @Override
    public RemoteInvoiceService create(Throwable cause) {
        return new RemoteInvoiceService() {
            @Override
            public InvoicingVo invoicing(InvoiceDto invoiceDto) {
                return InvoicingVo.fail(cause.getMessage());
            }

            @Override
            public InvoiceVo selectByInvoiceNumber(String number, String qyId) {
                return null;
            }


            @Override
            public JSONObject addEcg(Map<String, Object> map) {
                throw new IllegalArgumentException(cause.getMessage());
            }
        };
    }
}

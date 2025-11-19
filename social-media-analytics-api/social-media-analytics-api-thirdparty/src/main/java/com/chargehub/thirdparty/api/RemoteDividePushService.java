package com.chargehub.thirdparty.api;

import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.thirdparty.api.domain.dto.divide.DivideReqDTO;
import com.chargehub.thirdparty.api.domain.dto.divide.DivideRspDTO;
import com.chargehub.thirdparty.api.factory.RemoteDividePushServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;
import java.util.List;

@FeignClient(contextId = "remoteDividePushService", url = "EMPTY", name = ServiceNameConstants.THIRD_PARTY_SERVICE, fallbackFactory = RemoteDividePushServiceFallbackFactory.class)
public interface RemoteDividePushService {

    //@RequestMapping(value = "/pay-api/xny/devide/receiveData", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    //DivideRspDTO pushDivide(URI uri, @RequestBody DivideReqDTO reqDTO);


    @RequestMapping(value = "/pay-api/xny/devide/receiveData", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    DivideRspDTO pushDivideBatch(URI uri, @RequestBody List<DivideReqDTO> reqDTO);

}
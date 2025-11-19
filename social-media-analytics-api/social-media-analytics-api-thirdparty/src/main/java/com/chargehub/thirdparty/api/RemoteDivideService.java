package com.chargehub.thirdparty.api;

import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.thirdparty.api.domain.dto.divide.DivideReqDTO;
import com.chargehub.thirdparty.api.domain.dto.divide.DivideRspDTO;
import com.chargehub.thirdparty.api.factory.RemoteDivideServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(contextId = "remoteDivideService",  name = ServiceNameConstants.THIRD_PARTY_SERVICE, fallbackFactory = RemoteDivideServiceFallbackFactory.class)
public interface RemoteDivideService {

    //@RequestMapping(value = "/divide/reqDivide", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    //DivideRspDTO reqDivide(DivideReqDTO reqDTO);

    @RequestMapping(value = "/divide/reqDivideBatch", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    DivideRspDTO reqDivideBatch(@RequestBody List<DivideReqDTO> reqDTOList);

}

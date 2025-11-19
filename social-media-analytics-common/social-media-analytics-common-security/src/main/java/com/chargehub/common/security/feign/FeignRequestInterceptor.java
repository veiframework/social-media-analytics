package com.chargehub.common.security.feign;

import com.chargehub.common.core.constant.SecurityConstants;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.core.utils.ServletUtils;
import com.chargehub.common.core.utils.StringUtils;
import com.chargehub.common.core.utils.ip.IpUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * feign 请求拦截器
 *
 * @author ruoyi
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    @Autowired
    private HubProperties hubProperties;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest httpServletRequest = ServletUtils.getRequest();
        if (StringUtils.isNotNull(httpServletRequest)) {
            Map<String, String> headers = ServletUtils.getHeaders(httpServletRequest);
            // 传递用户信息请求头，防止丢失
            String userId = headers.get(SecurityConstants.DETAILS_USER_ID);
            if (StringUtils.isNotEmpty(userId)) {
                requestTemplate.header(SecurityConstants.DETAILS_USER_ID, userId);
            }
            String userKey = headers.get(SecurityConstants.USER_KEY);
            if (StringUtils.isNotEmpty(userKey)) {
                requestTemplate.header(SecurityConstants.USER_KEY, userKey);
            }
            String userName = headers.get(SecurityConstants.DETAILS_USERNAME);
            if (StringUtils.isNotEmpty(userName)) {
                requestTemplate.header(SecurityConstants.DETAILS_USERNAME, userName);
            }
            String authentication = headers.get(SecurityConstants.AUTHORIZATION_HEADER);
            if (StringUtils.isNotEmpty(authentication)) {
                requestTemplate.header(SecurityConstants.AUTHORIZATION_HEADER, authentication);
            }

            String type = headers.get(SecurityConstants.LOGIN_USER_TYPE);
            if (StringUtils.isNotEmpty(authentication)) {
                requestTemplate.header(SecurityConstants.LOGIN_USER_TYPE, type);
            }

            // 配置客户端IP
            requestTemplate.header("X-Forwarded-For", IpUtils.getIpAddr());

            if (hubProperties.isStandalone()) {
                requestTemplate.header(SecurityConstants.FEIGN_REQUEST, SecurityConstants.INNER);
            }


//            //远程调用时动态拼接固定前缀
//            String path = requestTemplate.path();

//            String contextPath = hubProperties.getContextPath();
//            if(path.contains(contextPath)){
//                return;
//            }
//            if(StringUtils.isNotBlank(contextPath)){
//                requestTemplate.uri(contextPath+path);
//            }
        }
    }
}
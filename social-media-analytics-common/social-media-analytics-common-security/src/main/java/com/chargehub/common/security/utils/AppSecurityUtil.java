package com.chargehub.common.security.utils;

import com.chargehub.common.core.constant.TokenConstants;
import com.chargehub.common.core.utils.ServletUtils;
import com.chargehub.common.core.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class AppSecurityUtil {

    public static String getToken() {
        return getToken(ServletUtils.getRequest());
    }

    public static String getToken(HttpServletRequest request) {
        // 从header获取token标识
        String token = request.getHeader(TokenConstants.AUTHENTICATION);
        return replaceTokenPrefix(token);
    }

    /**
     * 裁剪token前缀
     */
    public static String replaceTokenPrefix(String token) {
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, "");
        }
        return token;
    }


    public static Integer userId() {
       return null;
    }
}

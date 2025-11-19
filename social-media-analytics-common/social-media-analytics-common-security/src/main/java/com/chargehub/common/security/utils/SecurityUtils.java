package com.chargehub.common.security.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.common.core.constant.SecurityConstants;
import com.chargehub.common.core.constant.SystemConstants;
import com.chargehub.common.core.constant.TokenConstants;
import com.chargehub.common.core.context.SecurityContextHolder;
import com.chargehub.common.core.utils.ServletUtils;
import com.chargehub.common.core.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限获取工具类
 *
 * @author ruoyi
 */
public class SecurityUtils {
    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return SecurityContextHolder.getUserId();
    }

    /**
     * 获取用户名称
     */
    public static String getUsername() {
        return SecurityContextHolder.getUserName();
    }


    /**
     * 获取当前登录的用户昵称
     *
     * @return
     */
    public static String getNickname() {

        return getLoginUser().getSysUser().getNickName();
    }

    /**
     * 获取用户key
     */
    public static String getUserKey() {
        return SecurityContextHolder.getUserKey();
    }

    /**
     * 获取登录用户信息
     */
    public static LoginUser getLoginUser() {
        return SecurityContextHolder.get(SecurityConstants.LOGIN_USER, LoginUser.class);
    }

    /**
     * 获取请求token
     */
    public static String getToken() {
        return getToken(ServletUtils.getRequest());
    }

    /**
     * 根据request获取请求token
     */
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

    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public static String getPurviewSql(List<String> purviewTypeList) {
        return getPurviewSql(purviewTypeList, false);
    }

    public static String getPurviewSql(List<String> purviewTypeList, boolean allPurviewSql) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String purviewTypeStr = purviewTypeList.stream().map(i -> "'" + i + "'").collect(Collectors.joining(","));
        Set<String> ownerIds = new HashSet<>();
        if (loginUser != null) {
            ownerIds = loginUser.ownerIds();
        } else {
            ownerIds.add("unknown");
        }
        String ownerIdStr = ownerIds.stream().map(i -> "'" + i + "'").collect(Collectors.joining(","));
        if (allPurviewSql) {
            return "SELECT Z9_PURVIEW_ID FROM Z9_PURVIEW WHERE Z9_OWNER_ID IN (" + ownerIdStr + ") AND Z9_PURVIEW_TYPE IN ( '*'," + purviewTypeStr + ") and Z9_PURVIEW_ID = '*'";
        }
        return "SELECT Z9_PURVIEW_ID FROM Z9_PURVIEW WHERE Z9_OWNER_ID IN (" + ownerIdStr + ")" + " AND Z9_PURVIEW_TYPE IN (" + purviewTypeStr + ")";
    }

    public static <T> T getLoginUserInfo(Class<T> tClass) {
        LoginUser loginUser = getLoginUser();
        Assert.notNull(loginUser, "user not login!");
        String info = loginUser.getInfo();
        if (info == null) {
            return null;
        }
        return JSON.parseObject(info, tClass);
    }

    public static Integer getAgentId(HttpServletRequest httpServletRequest) {
        String agentId = httpServletRequest.getHeader(SystemConstants.AGENT_ID);
        if (agentId == null)
            return null;
        try {
            return Integer.parseInt(agentId);
        } catch (Exception e) {
            return null;
        }
    }
}

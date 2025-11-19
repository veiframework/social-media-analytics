package com.chargehub.common.security.config;

import com.chargehub.common.core.constant.SecurityConstants;
import com.chargehub.common.core.properties.HubProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Zhanghaowei
 * @date 2025/08/21 09:37
 */
@Component
@Slf4j
public class CustomUrlPathHelper extends UrlPathHelper {

    /**
     * 重写核心方法，自动移除第一个路径段
     */
    @Override
    public String getLookupPathForRequest(HttpServletRequest request) {

        // 获取原始路径
        String originalPath = super.getLookupPathForRequest(request);
        // 🔧 如果是静态资源，直接返回原路径
        if (isStaticResource(request)) {
            return originalPath;
        }
        // 如果使内部feign调用跳过
        String header = request.getHeader(SecurityConstants.FEIGN_REQUEST);
        if (StringUtils.isNotBlank(header) && header.equals(SecurityConstants.INNER)) {
            return originalPath;
        }
        // 🔧 核心逻辑：移除第一个路径段
        String processedPath = removeFirstPathSegment(originalPath);

        if (!originalPath.equals(processedPath)) {
            log.debug("Path processed: {} -> {}", originalPath, processedPath);
        }

        return processedPath;
    }


    /**
     * 判断是否为静态资源
     */
    private boolean isStaticResource(HttpServletRequest request) {
        String referer = request.getHeader("referer");
        if (StringUtils.isNotBlank(referer)) {
            if (referer.contains("/swagger-ui/index.html")) {
                return true;
            }
        }


        List<RequestMatcher> matchers = new ArrayList<>();
        matchers.add(new AntPathRequestMatcher("/**/*.js"));
        matchers.add(new AntPathRequestMatcher("/**/*.css"));
        matchers.add(new AntPathRequestMatcher("/**/index.html"));
        matchers.add(new AntPathRequestMatcher("/**/index.html/**"));
        matchers.add(new AntPathRequestMatcher("/**/index.html*"));
        matchers.add(new AntPathRequestMatcher("/**/swagger*/**"));
        matchers.add(new AntPathRequestMatcher("/**/v2/api-docs"));
        matchers.add(new AntPathRequestMatcher("/**/favicon.ico"));
        matchers.add(new AntPathRequestMatcher("/**/*.json"));
        matchers.add(new AntPathRequestMatcher("/code"));
        matchers.add(new AntPathRequestMatcher("/**/static/**"));
        matchers.add(new AntPathRequestMatcher("/**/fonts/**"));
        matchers.add(new AntPathRequestMatcher("/**/ws/**"));
        matchers.add(new AntPathRequestMatcher("/**/public/**"));
        matchers.add(new AntPathRequestMatcher("/**/img/**"));
        matchers.add(new AntPathRequestMatcher("/**/images/**"));
        matchers.add(new AntPathRequestMatcher("/webjars/**"));
        matchers.add(new AntPathRequestMatcher("/**/*.jpg"));
        matchers.add(new AntPathRequestMatcher("/**/*.png"));
        matchers.add(new AntPathRequestMatcher("/**/*.mp4"));
        matchers.add(new AntPathRequestMatcher("/**/*.jpeg"));
        matchers.add(new AntPathRequestMatcher("/**/*.gif"));
        matchers.add(new AntPathRequestMatcher("/**/*.xls"));
        matchers.add(new AntPathRequestMatcher("/**/*.docx"));
        matchers.add(new AntPathRequestMatcher("/**/*.xlsx"));
        matchers.add(new AntPathRequestMatcher("/**/*.doc"));
        matchers.add(new AntPathRequestMatcher("/**/*.pptx"));
        matchers.add(new AntPathRequestMatcher("/**/*.ppt"));
        matchers.add(new AntPathRequestMatcher("/**/*.pdf"));
        matchers.add(new AntPathRequestMatcher("/**/*.md"));
        matchers.add(new AntPathRequestMatcher("/**/*.txt"));
        matchers.add(new AntPathRequestMatcher("/**/*.svg"));
        return matchers.stream().anyMatch(i -> i.matches(request));
    }

    /**
     * 移除第一个路径段
     */
    private String removeFirstPathSegment(String path) {
        if (StringUtils.isBlank(path) || "/".equals(path)) {
            return path;
        }

        // 分割路径
        String[] segments = path.split("/");

        // 如果只有一个段或没有段，返回根路径
        if (segments.length <= 2) {
            return "/";
        }

        // 重新组装路径，跳过第一个非空段
        StringBuilder result = new StringBuilder();
        for (int i = 2; i < segments.length; i++) {
            result.append("/").append(segments[i]);
        }

        if (result.length() == 0) {
            return "/";
        }
        if (path.endsWith("/")) {
            result.append("/");
        }
        return result.toString();
    }
}
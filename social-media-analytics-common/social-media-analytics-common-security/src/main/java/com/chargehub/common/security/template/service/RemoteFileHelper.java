package com.chargehub.common.security.template.service;

import org.springframework.mock.web.MockMultipartFile;

/**
 * @author Zhanghaowei
 * @date 2024/04/20 19:42
 */
public interface RemoteFileHelper {

    /**
     * 下载文件
     *
     * @param fileUrl
     * @return
     */
    byte[] download(String fileUrl);

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    default String upload(MockMultipartFile file) {
        return "notSupport";
    }



}

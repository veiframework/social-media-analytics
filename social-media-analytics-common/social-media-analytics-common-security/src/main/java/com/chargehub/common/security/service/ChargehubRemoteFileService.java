package com.chargehub.common.security.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import com.chargehub.common.security.template.service.RemoteFileHelper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

import java.net.URL;

/**
 * @author Zhanghaowei
 * @date 2024/04/20 19:47
 */
@Component
public class ChargehubRemoteFileService implements RemoteFileHelper {


    @Override
    public byte[] download(String fileUrl) {
        URL url = URLUtil.url("/opt/resources/" + fileUrl);
        return FileUtil.readBytes(url.getFile());
    }


    @Override
    public String upload(MockMultipartFile file) {
        return file.getName();
    }

}

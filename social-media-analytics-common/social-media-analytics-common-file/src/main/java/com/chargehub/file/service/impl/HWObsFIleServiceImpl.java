package com.chargehub.file.service.impl;

import com.chargehub.common.core.utils.DateUtils;
import com.chargehub.common.core.utils.ObsBootUtil;
import com.chargehub.file.service.ISysFileService;
import com.obs.services.model.TemporarySignatureResponse;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/**
 * @author yixiu
 */
@Primary
@Service
@Configuration
public class HWObsFIleServiceImpl implements ISysFileService {
    private static final Logger log = LoggerFactory.getLogger(HWObsFIleServiceImpl.class);


    /**
     * 文件上传
     *
     * @param file    文件
     * @return 路径
     */

    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        return ObsBootUtil.upload(file, DateUtils.datePath());
    }



    @Override
    public String uploadFile(MultipartFile file, String fileDic) throws Exception {
        return ObsBootUtil.upload(file, fileDic);
    }

    @Override
    public boolean deleteFile(String fileURL) {
        ObsBootUtil.delete(fileURL, true);
        return true;
    }



    private static Request.Builder getBuilder(TemporarySignatureResponse res) {
        Request.Builder builder = new Request.Builder();
        for (Map.Entry<String, String> entry : res.getActualSignedRequestHeaders().entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }
        return builder.url(res.getSignedUrl());
    }


}

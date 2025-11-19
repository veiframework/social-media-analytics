package com.chargehub.file.service.impl;

import com.chargehub.file.service.ISysFileService;
import com.chargehub.file.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 本地文件存储
 * 
 * @author ruoyi
 */

@Service
@Qualifier("LocalSysFileServiceImpl")
public class LocalSysFileServiceImpl implements ISysFileService
{
    /**
     * 资源映射路径 前缀
     */
    @Value("${file.prefix}")
    public String localFilePrefix;

    /**
     * 域名或本机访问地址
     */
    @Value("${file.domain}")
    public String domain;
    
    /**
     * 上传文件存储在本地的根路径
     */
    @Value("${file.path}")
    private String localFilePath;

    /**
     * 本地文件上传接口
     * 
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public String uploadFile(MultipartFile file) throws Exception
    {
        String name = FileUploadUtils.upload(localFilePath, file);
        String url = domain + localFilePrefix + name;
        return url;
    }

    @Override
    public String uploadFile(MultipartFile file, String fileDic) throws Exception {
        String name = FileUploadUtils.upload(localFilePath + fileDic, file);
        String url = domain + localFilePrefix + fileDic + name;
        return url;
    }

    public boolean deleteFile(String fileURL)  {
        // 创建 URL 对象
        URL url = null;
        try {
            url = new URL(fileURL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // 获取路径部分
        String path = url.getPath();

        // 提取相对路径
        String relativePath = path.replace("/statics", "");
        return FileUploadUtils.delete(localFilePath + relativePath);
    }
}

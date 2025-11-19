package com.chargehub.admin.api.factory;

import com.chargehub.admin.api.RemoteFileService;
import com.chargehub.admin.api.domain.SysFile;
import com.chargehub.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件服务降级处理
 * 
 * @author ruoyi
 */
@Component
public class RemoteFileFallbackFactory implements FallbackFactory<RemoteFileService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteFileFallbackFactory.class);

    @Override
    public RemoteFileService create(Throwable throwable)
    {
        log.error("文件服务调用失败:{}", throwable.getMessage());
        return new RemoteFileService()
        {

            @Override
            public R<Map> getPrivateObsToken(String filePath, String method, String contentType) {
                return R.fail("获取token失败:" + throwable.getMessage());
            }

            @Override
            public R<SysFile> upload(MultipartFile file)
            {
                return R.fail("上传文件失败:" + throwable.getMessage());
            }

            @Override
            public R<SysFile> upload(MultipartFile file, String fileDir) {
                return R.fail("上传文件失败:" + throwable.getMessage());
            }

            @Override
            public R<SysFile> delete(String fileUrl) {
                return R.fail("删除文件失败:" + throwable.getMessage());
            }

            @Override
            public R<SysFile> uploadLocal(MultipartFile file) {
                return R.fail("上传文件到本地失败:" + throwable.getMessage());
            }

            @Override
            public R<SysFile> deleteLocal(String fileUrl) {
                return R.fail("删除本地文件失败:" + throwable.getMessage());
            }
        };
    }
}

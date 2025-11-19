package com.chargehub.admin.api;

import cn.hutool.core.util.URLUtil;
import com.chargehub.admin.api.domain.SysFile;
import com.chargehub.admin.api.factory.RemoteFileFallbackFactory;
import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件服务
 *
 * @author ruoyi
 */
@FeignClient(contextId = "remoteFileService", value = ServiceNameConstants.FILE_SERVICE, fallbackFactory = RemoteFileFallbackFactory.class)
public interface RemoteFileService {
    /**
     * 上传文件
     *
     * @param file 文件信息
     * @return 结果
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<SysFile> upload(@RequestPart(value = "file") MultipartFile file);

    /**
     * https://wcx-chonghong-static.obs.cn-north-4.myhuaweicloud.com/Snipaste_2024-04-19_11-14-25.png
     * @param filePath    文件路径加文件名
     * @param method      支持 GET、PUT
     * @param contentType headers中的 Content-Type
     * @return 请求需要用的header和url
     */
    @GetMapping(value = "/get-private-obs-token")
    public R<Map> getPrivateObsToken(@RequestParam(value = "filePath") String filePath, @RequestParam(value = "method") String method, @RequestParam(required = false, value = "contentType") String contentType);

    @ApiOperation("指定路径文件上传")
    @PostMapping(value = "upload-dir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<SysFile> upload(@RequestPart(value = "file") MultipartFile file, @RequestPart(value = "fileDir") String fileDir);

    @ApiOperation(value = "删除私有OBS内文件", notes = "用于内部服务删除私有OBS的文件")
    @DeleteMapping("delete")
    public R delete(@ApiParam(value = "文件url", required = true) @RequestParam(value = "fileUrl") String fileUrl);

    @ApiOperation("文件上传本地服务")
    @PostMapping(value = "upload-local", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<SysFile> uploadLocal(@ApiParam(value = "file") @RequestPart(value = "file")  MultipartFile file);

    @ApiOperation("删除本地文件")
    @DeleteMapping("delete-local")
    public R<SysFile> deleteLocal(@ApiParam("fileUrl") @RequestParam(value = "fileUrl") String fileUrl);

    public static void main(String[] args) {
        String path = URLUtil.getPath("https://wcx-chonghong-static.obs.cn-north-4.myhuaweicloud.com/file/Snipaste_2024-04-19_11-14-25.png");
        System.out.println(path);


    }
}

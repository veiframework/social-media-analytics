package com.chargehub.file.controller;

import com.chargehub.admin.api.domain.SysFile;
import com.chargehub.common.core.domain.R;
import com.chargehub.common.core.utils.ObsBootUtil;
import com.chargehub.common.core.utils.file.FileUtils;
import com.chargehub.file.service.ISysFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.Objects;

/**
 * 文件请求处理
 * 
 * @author ruoyi
 */
@RestController
@Api(value = "文件服务", tags = "文件服务", basePath = "/file-api")
public class SysFileController
{
    private static final Logger log = LoggerFactory.getLogger(SysFileController.class);
    public static final String errorMessage = "上传文件失败";

    @Autowired
    private ISysFileService sysFileService;

    @Autowired
    @Qualifier("LocalSysFileServiceImpl")
    private ISysFileService localSysFileService;



    /**
     * 文件上传请求
     */
    // @ApiOperation(value = "文件上传OBS", notes = "上传文件到公开OBS", response = SysFile.class)
    // @ApiResponses(
    //         @ApiResponse(code = 200, message = "Success", response = SysFile.class)
    // )
    // @PostMapping("upload")
    // public R<SysFile> upload(@ApiParam("要上传的文件") MultipartFile file)
    // {
    //     try
    //     {
    //         // 上传并返回访问地址
    //         String url = sysFileService.uploadFile(file);
    //         SysFile sysFile = new SysFile();
    //         sysFile.setName(FileUtils.getName(url));
    //         sysFile.setUrl(url);
    //         return R.ok(sysFile);
    //     }
    //     catch (Exception e)
    //     {
    //         log.error(errorMessage, e);
    //         return R.fail(e.getMessage());
    //     }
    // }

    @ApiOperation(value = "指定路径文件上传OBS", notes = "指定文件的上传路径上传到公开OBS", response = SysFile.class)
    // @ApiResponses(
    //         @ApiResponse(code = 200, message = "Success", response = SysFile.class)
    // )
    @PostMapping("upload-dir")
    public R<SysFile> upload(@ApiParam(value = "上传文件", required = true) MultipartFile file,
                             @ApiParam(value = "上传文件路径", required = true) String fileDir)
    {
        try
        {
            // 上传并返回访问地址
            String url = sysFileService.uploadFile(file, fileDir);
            SysFile sysFile = new SysFile();
            sysFile.setName(FileUtils.getName(url));
            sysFile.setUrl(url);
            return R.ok(sysFile);
        }
        catch (Exception e)
        {
            log.error(errorMessage, e);
            return R.fail(e.getMessage());
        }
    }


    @ApiOperation(value = "删除私有OBS内文件", notes = "用于内部服务删除私有OBS的文件")
    @DeleteMapping("delete")
    public R delete(@ApiParam(value = "文件url", required = true) @RequestParam String fileUrl){
        if(sysFileService.deleteFile(fileUrl)){
            return R.ok();
        }else {
            return R.fail();
        }
    }

    // @ApiOperation("获取上传文件OBS url信息")
    // @GetMapping("get-upload-url")
    // public R<Map> getUploadUrl(@RequestParam String filePath, @RequestParam boolean isPrivate) {
    //     Map result = ObsBootUtil.doCreateObjectUrl(filePath, isPrivate);
    //     return R.ok(result);
    //
    // }
    //
    // @ApiOperation("获取私有OBS下载文件 url 信息")
    // @GetMapping("get-download-url")
    // public R<Map> getDownloadUrl(@RequestParam String filePath) {
    //     Map result = ObsBootUtil.doDownloadUrl(filePath, null);
    //     return R.ok(result);
    //
    // }


    @ApiOperation(value = "获取公开OBS的token", notes = "用于上传、下载公开的文件，接口返回有请求需要的headers和url")
    @GetMapping("get-token")
    public R<Map> getToken(@ApiParam(value = "要上传的文件路径（包含文件名）", required = true) @RequestParam String filePath,
                           @ApiParam(value = "GET(下载)/PUT(上传)", required = true) @RequestParam String method,
                           @ApiParam(value = "headers中的 Content-Type", required = false) @RequestParam(required = false) String contentType) throws Exception {
        if(Objects.isNull(contentType)){
            contentType = "application/octet-stream";
        }
        Map headers = ObsBootUtil.createToken(method, filePath, contentType, false);
        return R.ok(headers);

    }

    @ApiOperation(value = "获取private OBS的token",notes = "用于上传、下载内部服务用的文件，接口返回有请求需要的headers和url")
    @GetMapping("get-private-obs-token")
    public R<Map> getPrivateObsToken(@ApiParam(value = "要上传的文件路径（包含文件名）", required = true) @RequestParam String filePath,
                                     @ApiParam(value = "GET(下载)/PUT(上传)", required = true) @RequestParam String method,
                                     @ApiParam(value = "headers中的 Content-Type", required = false) @RequestParam(required = false) String contentType) throws Exception {
        if(Objects.isNull(contentType)){
            contentType = "application/octet-stream";
        }
        Map headers = ObsBootUtil.createToken(method, filePath, contentType, true);
        return R.ok(headers);
    }


    @ApiOperation(value = "文件上传本地服务", notes = "上传文件到本地服务器", response = SysFile.class)
    @PostMapping("upload-local")
    public R<SysFile> uploadLocal(@ApiParam(value = "文件", required = true) MultipartFile file)
    {
        try
        {
            // 上传并返回访问地址
            String url = localSysFileService.uploadFile(file);
            SysFile sysFile = new SysFile();
            sysFile.setName(FileUtils.getName(url));
            sysFile.setUrl(url);
            return R.ok(sysFile);
        }
        catch (Exception e)
        {
            log.error(errorMessage, e);
            return R.fail(e.getMessage());
        }
    }



    @ApiOperation("删除本地文件")
    @DeleteMapping("delete-local")
    public R<SysFile> deleteLocal(@ApiParam(value = "文件URL", required = true) @RequestParam String fileUrl) throws MalformedURLException {
        if(localSysFileService.deleteFile(fileUrl)){
            return R.ok();
        }else {
            return R.fail();
        }
    }



}
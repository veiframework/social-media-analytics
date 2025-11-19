package com.chargehub.file.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.URLUtil;

import com.chargehub.admin.api.domain.SysFile;
import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.file.utils.FileUploadUtils;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Zhanghaowei
 * @date 2025/07/31 09:47
 */
@RestController
@RequestMapping("/upload")
public class AdminUploadController {

    @PostMapping
    public AjaxResult uploadLocal(@ApiParam(value = "文件", required = true) MultipartFile file) {
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String fileName = IdUtil.objectId() + extension;

        try {
            file.transferTo(FileUtil.file(URLUtil.url("/opt/resources/" + fileName)));
            SysFile sysFile = new SysFile();
            sysFile.setName(fileName);
            sysFile.setUrl(fileName);
            return AjaxResult.success(sysFile);
        } catch (IOException e) {
            return AjaxResult.error("上传失败");
        }

    }

}

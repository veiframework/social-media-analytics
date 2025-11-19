package com.chargehub.common.core.utils.poi;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Zhanghaowei
 * @date 2019-08-05
 */
@Slf4j
public final class ExcelUtils {
    private ExcelUtils() {
    }


    public static void downloadExcel(@NotNull Workbook workbook, @NotNull String filename, HttpServletResponse response) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    public static void exportExcel(List<?> list, ExportParams exportParams, Class<?> pojoClass,
                                   String fileName,
                                   HttpServletResponse response) {
        defaultExport(list, pojoClass, fileName, response, exportParams);
    }


    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName,
                                      HttpServletResponse response,
                                      ExportParams exportParams) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        if (workbook != null) {
            downloadExcel(workbook, fileName, response);
        }
    }


    public static File exportBigExcel(ExportParams exportParams, String fileName, Class<?> pojoClass, IExcelExportServer server) {
        String absolutePath = "/opt/resources/" + fileName + ".xlsx";
        return exportBigExcel(exportParams, absolutePath, pojoClass, server, null);
    }

    public static <T> File exportBigExcel(ExportParams exportParams, String absolutePath, Class<?> pojoClass, IExcelExportServer server, T queryParams) {
        File file = FileUtil.touch(absolutePath);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            Workbook workbook = ExcelExportUtil.exportBigExcel(exportParams, pojoClass,
                    server, queryParams);
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return file;
    }


    /**
     * easy poi 排除无效行的错误
     * 验证失败的数据和验证通过的数据被分开添加到了failList和list中
     *
     * @param result
     * @param <T>
     * @return
     */
    public static <T> List<T> excludeInvalidResultInFailList(ExcelImportResult<T> result) {
        boolean isFail = result.isVerifyFail();
        if (!isFail) {
            return Collections.emptyList();
        }
        Predicate<T> predicate = obj -> {
            Map<String, Object> map = BeanUtil.beanToMap(obj, false, true);
            if (MapUtils.isEmpty(map)) {
                return false;
            }
            map.remove("rowNum");
            map.remove("errorMsg");
            return !MapUtils.isEmpty(map);
        };
        List<T> failList = result.getFailList().stream().filter(predicate).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(failList)) {
            return failList;
        }
        return Collections.emptyList();
    }

    public static <T> File uploadFailExcel(ExcelImportResult<T> result, String fileName) {
        String absolutePath = "/opt/resources/" + fileName + ".xlsx";
        File file = FileUtil.touch(absolutePath);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            Workbook failWorkbook = result.getFailWorkbook();
            failWorkbook.write(outputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return file;
    }


}

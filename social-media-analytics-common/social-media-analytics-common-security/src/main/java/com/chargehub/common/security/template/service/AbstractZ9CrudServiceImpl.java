package com.chargehub.common.security.template.service;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import cn.afterturn.easypoi.excel.entity.result.ExcelVerifyHandlerResult;
import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import cn.afterturn.easypoi.handler.inter.IExcelVerifyHandler;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.chargehub.common.core.utils.poi.ExcelUtils;
import com.chargehub.common.security.template.domain.ExcelImportDto;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import com.chargehub.common.security.template.domain.Z9CrudExportResult;
import com.chargehub.common.security.template.domain.Z9CrudImportResult;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.event.*;
import com.chargehub.common.security.template.mapper.Z9BaseCrud;
import com.chargehub.common.security.template.vo.Z9CrudVo;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Zhanghaowei
 * @Transactional 注解只对当前class生效, 如果是继承则需要重新声明
 * @date 2024/04/03 15:56
 */
@Transactional(rollbackFor = Exception.class)
public abstract class AbstractZ9CrudServiceImpl<M extends Z9BaseCrud<E>, E extends Z9CrudEntity> implements Z9CrudService<E>, IExcelVerifyHandler<Z9CrudDto<E>>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    public final M baseMapper;

    private final Class<E> entityClass;

    private RemoteFileHelper remoteFileHelper;

    protected AbstractZ9CrudServiceImpl(M baseMapper) {
        this.baseMapper = baseMapper;
        this.entityClass = baseMapper.getEntity();
    }


    @Override
    public void create(Z9CrudDto<E> dto) {
        E e = BeanUtil.copyProperties(dto, this.entityClass);
        E exist = this.baseMapper.doGetExist(e, null);
        Assert.isNull(exist, duplicateMsg());
        this.publishEvent(new Z9BeforeCreateEvent<>(e, this.entityClass));
        this.baseMapper.doCreate(e);
        this.publishEvent(new Z9CreateEvent<>(e, dto, this.entityClass));
    }

    @Override
    public void edit(Z9CrudDto<E> dto, String id) {
        E e = BeanUtil.copyProperties(dto, this.entityClass);
        E exist = this.baseMapper.doGetExist(e, id);
        Assert.isNull(exist, duplicateMsg());
        this.publishEvent(new Z9BeforeUpdateEvent<>(e, this.entityClass));
        this.baseMapper.doEdit(e, id);
        this.publishEvent(new Z9UpdateEvent<>(e, dto, this.entityClass));
    }

    @Override
    public void deleteByIds(String ids) {
        String[] split = ids.split(",");
        if (ArrayUtils.isEmpty(split)) {
            return;
        }
        List<String> idList = Arrays.stream(split).collect(Collectors.toList());
        this.publishEvent(new Z9BeforeDeleteEvent<>(idList, this.entityClass));
        this.baseMapper.doDeleteByIds(idList);
        this.publishEvent(new Z9DeleteEvent<>(idList, this.entityClass));
    }

    @Override
    public void saveExcelData(List<Z9CrudDto<E>> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            return;
        }
        List<E> list = BeanUtil.copyToList(dtos, this.entityClass);
        this.publishEvent(new Z9BeforeCreateEvent<>(list, this.entityClass));
        this.baseMapper.doSaveExcelData(list);
        this.publishEvent(new Z9CreateEvent<>(list, dtos, this.entityClass));
    }

    @Override
    public Z9CrudVo getDetailById(String id) {
        E e = this.baseMapper.doGetDetailById(id);
        if (e == null) {
            return null;
        }
        return BeanUtil.copyProperties(e, voClass());
    }


    @Override
    public List<? extends Z9CrudVo> getAll(Z9CrudQueryDto<E> queryDto) {
        List<E> all = this.baseMapper.doGetAll(queryDto);
        if (CollectionUtils.isEmpty(all)) {
            return Lists.newArrayList();
        }
        return BeanUtil.copyToList(all, voClass());
    }

    @Override
    public IPage<? extends Z9CrudVo> getPage(Z9CrudQueryDto<E> queryDto) {
        return this.baseMapper.doGetPage(queryDto).convert(i -> BeanUtil.copyProperties(i, voClass()));
    }

    @Override
    public List<Map<String, Object>> listMaps() {
        return this.baseMapper.doListMaps();
    }


    @Override
    public Z9CrudExportResult exportExcel(Z9CrudQueryDto<E> queryDto) {
        String fileName = this.excelName();
        fileName += "-" + IdUtil.objectId();
        ExportParams exportParams = new ExportParams(this.excelName(), this.excelName());
        this.buildExportParams(queryDto, exportParams);
        if (getDictHandler() != null) {
            exportParams.setDictHandler(getDictHandler());
        }
        File file = ExcelUtils.exportBigExcel(exportParams, fileName, voClass(), (params, page) -> {
            queryDto.setPageNum((long) page);
            queryDto.setPageSize(partitionSize());
            return new ArrayList<>(this.getPage(queryDto).getRecords());
        });
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String excelName = fileName + ".xlsx";
            MockMultipartFile mockMultipartFile = new MockMultipartFile(excelName, excelName, null, fileInputStream);
            String downloadUrl = remoteFileHelper.upload(mockMultipartFile);
            if ("notSupport".equals(downloadUrl)) {
                return new Z9CrudExportResult(excelName);
            }
            return new Z9CrudExportResult(downloadUrl);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public Z9CrudImportResult<Z9CrudDto<E>> importExcel(ExcelImportDto importDto) {
        byte[] bytes = this.remoteFileHelper.download(importDto.getFileUrl());
        List<Z9CrudDto<E>> successList;
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            ImportParams params = new ImportParams();
            params.setDictHandler(getDictHandler());
            params.setVerifyHandler(getVerifyHandler());
            params.setNeedVerify(true);
            params.setTitleRows(1);
            params.setNeedCheckOrder(true);
            ExcelImportResult<Z9CrudDto<E>> result = ExcelImportUtil.importExcelMore(is, dtoClass(), params);
            List<Z9CrudDto<E>> failList = result.getFailList();
            List<Z9CrudDto<E>> list = result.getList();
            if (CollectionUtils.isNotEmpty(failList)) {
                String failedFileName = IdUtil.objectId();
                File file = ExcelUtils.uploadFailExcel(result, failedFileName);
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    String excelName = failedFileName + ".xlsx";
                    MockMultipartFile mockMultipartFile = new MockMultipartFile(excelName, excelName, null, fileInputStream);
                    String downloadUrl = remoteFileHelper.upload(mockMultipartFile);
                    if ("notSupport".equals(downloadUrl)) {
                        throw new IllegalArgumentException("导出失败记录地址:" + excelName);
                    }
                    throw new IllegalArgumentException("导出失败记录地址:" + downloadUrl);
                }
            }
            successList = list;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        this.saveExcelData(successList);
        return new Z9CrudImportResult<>(null, successList);
    }

    @Override
    public void exportExcelTemplate(HttpServletResponse response) {
        ExportParams exportParams = new ExportParams();
        exportParams.setSheetName("sheet1");
        exportParams.setTitle(this.excelName());
        if (getDictHandler() != null) {
            exportParams.setDictHandler(getDictHandler());
        }
        ExcelUtils.exportExcel(new ArrayList<>(), exportParams, dtoClass(), this.excelName() + ".xlsx", response);
    }

    @Override
    public Z9CrudVo getExist(Z9CrudDto<E> dto, String id) {
        E e = BeanUtil.copyProperties(dto, this.entityClass);
        E exist = this.baseMapper.doGetExist(e, id);
        if (exist == null) {
            return null;
        }
        return BeanUtil.copyProperties(exist, voClass());
    }

    @Override
    public ExcelVerifyHandlerResult verifyHandler(Z9CrudDto<E> t) {
        if (StringUtils.isNotBlank(t.getErrorMsg())) {
            return new ExcelVerifyHandlerResult(false, "");
        }
        Z9CrudVo exist = this.getExist(t, null);
        if (exist != null) {
            return new ExcelVerifyHandlerResult(false, this.duplicateMsg());
        }
        return new ExcelVerifyHandlerResult(true);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        try {
            this.remoteFileHelper = applicationContext.getBean(RemoteFileHelper.class);
        } catch (Exception e) {
            this.remoteFileHelper = HttpUtil::downloadBytes;
        }
    }


    @Override
    public void publishEvent(Object event) {
        applicationContext.publishEvent(event);
    }

    public M getBaseMapper() {
        return this.baseMapper;
    }

    /**
     * 获取字典处理器
     *
     * @return
     */
    public abstract IExcelDictHandler getDictHandler();

    @SuppressWarnings("all")
    public IExcelVerifyHandler getVerifyHandler() {
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Z9CrudDto<E>> Class<T> dtoClass() {
        Assert.notNull(this.doGetDtoClass(), "dto不得为空");
        return (Class<T>) doGetDtoClass();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends Z9CrudVo> Class<V> voClass() {
        Assert.notNull(this.doGetVoClass(), "vo不得为空");
        return (Class<V>) doGetVoClass();
    }

    /**
     * 获取class
     *
     * @return
     */
    public abstract Class<?> doGetDtoClass();

    /**
     * 获取class
     *
     * @return
     */
    public abstract Class<?> doGetVoClass();

    public Class<? extends Z9CrudQueryDto<E>> queryDto() {
        return null;
    }

    /**
     * 导出数据的业务编码
     *
     * @return
     */
    public String exportBusinessCode() {
        return this.entityClass.getSimpleName();
    }

    @EventListener
    public void crudAfterCreateChargeExport(AfterCreateExportEvent event) {
        if (!event.getBusinessCode().equals(exportBusinessCode())) {
            return;
        }
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, 1, 30,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(1),
                new NamedThreadFactory("OrderThread-", null, false, (t, e) -> {
                    ChargeExportStatusEvent chargeExportStatusEvent = new ChargeExportStatusEvent(2, event.getId(), "线程池未捕获异常--" + e.toString(), 0);
                    applicationContext.publishEvent(chargeExportStatusEvent);
                }),
                new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolExecutor.execute(() -> {
            AtomicDouble totalSeconds = new AtomicDouble();
            AtomicInteger currentNum = new AtomicInteger();
            ObjectNode requestParams = event.getRequestParams();
            Z9CrudQueryDto<E> queryDto = JSONUtil.toBean(requestParams.toString(), this.queryDto());
            this.asyncExport(event.getId(), queryDto, totalSeconds, currentNum);
        });
    }

    public void asyncExport(String exportId, Z9CrudQueryDto<E> queryDto, AtomicDouble totalSeconds, AtomicInteger currentNum) {
        try {
            String fileName = this.excelName() + "-" + IdUtil.objectId();
            ExportParams exportParams = new ExportParams(this.excelName(), this.excelName());
            this.buildExportParams(queryDto, exportParams);
            if (getDictHandler() != null) {
                exportParams.setDictHandler(getDictHandler());
            }
            IExcelExportServer server = (params, page) -> {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                queryDto.setPageNum((long) page);
                queryDto.setPageSize(partitionSize());
                IPage<? extends Z9CrudVo> iPage = this.getPage(queryDto);
                stopWatch.stop();
                List<? extends Z9CrudVo> records = iPage.getRecords();
                long total = iPage.getTotal();
                double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
                totalSeconds.addAndGet(totalTimeSeconds);
                currentNum.addAndGet(records.size());
                applicationContext.publishEvent(new ChargeExportProgressEvent(exportId, (int) total, currentNum.get(), totalSeconds.get()));
                return new ArrayList<>(records);
            };
            File file = ExcelUtils.exportBigExcel(exportParams, fileName, voClass(), server);
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                String excelName = fileName + ".xlsx";
                MockMultipartFile mockMultipartFile = new MockMultipartFile(excelName, excelName, null, fileInputStream);
                String downloadUrl = remoteFileHelper.upload(mockMultipartFile);
                String realDownloadUrl = "notSupport".equals(downloadUrl) ? excelName : downloadUrl;
                applicationContext.publishEvent(new ChargeExportStatusEvent(1, exportId, totalSeconds.get(), realDownloadUrl));
            }
        } catch (Exception e) {
            applicationContext.publishEvent(new ChargeExportStatusEvent(2, exportId, e.getMessage(), totalSeconds.get()));
        }
    }

    public void buildExportParams(Z9CrudQueryDto<E> queryDto, ExportParams exportParams) {

    }
}

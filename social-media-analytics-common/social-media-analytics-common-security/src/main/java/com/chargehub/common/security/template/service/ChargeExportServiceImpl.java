package com.chargehub.common.security.template.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.common.redis.service.RedisService;
import com.chargehub.common.security.template.domain.ChargeExport;
import com.chargehub.common.security.template.dto.ChargeExportDto;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.event.*;
import com.chargehub.common.security.template.mapper.ChargeExportMapper;
import com.chargehub.common.security.template.vo.ChargeExportVo;
import com.chargehub.common.security.template.vo.Z9CrudVo;
import com.chargehub.common.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Zhanghaowei
 * @date 2024/07/16 17:29
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ChargeExportServiceImpl extends AbstractZ9CrudServiceImpl<ChargeExportMapper, ChargeExport> implements ChargeExportService, CommandLineRunner {


    protected ChargeExportServiceImpl(ChargeExportMapper baseMapper) {
        super(baseMapper);
    }

    private int concurrency;

    public static final String TIP = "超过最大导出任务数量";

    @Autowired
    private RedisService redisService;

    public static final String EXPORT_CACHE_KEY = "export:";

    public static final String STOP_EXPORT = "stop_export:";

    @EventListener
    public void beforeCreateChargeExport(Z9BeforeCreateEvent<ChargeExport> event) {
        List<ChargeExport> data = event.getData();
        Assert.isTrue(concurrency >= data.size(), TIP);
        Long userId = SecurityUtils.getUserId();
        long count = this.baseMapper.lambdaQuery().eq(ChargeExport::getCreateBy, userId).eq(ChargeExport::getExportStatus, 0).count();
        Assert.isTrue(concurrency > count, TIP);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void exportListenAfterCreateChargeExport(Z9CreateEvent<ChargeExport> event) {
        List<ChargeExport> data = event.getData();
        for (ChargeExport chargeExport : data) {
            publishEvent(new AfterCreateExportEvent(chargeExport.getId(), chargeExport.getBusinessCode(), chargeExport.getRequestParams()));
        }
    }

    @Override
    public IPage<ChargeExportVo> getPage(Z9CrudQueryDto<ChargeExport> queryDto) {
        return this.baseMapper.doGetPage(queryDto).convert(i -> {
            ChargeExportVo z9CrudVo = BeanUtil.copyProperties(i, voClass());
            ChargeExport export = redisService.getCacheObject(EXPORT_CACHE_KEY + i.getId());
            if (export != null) {
                z9CrudVo.setExportProgress(export.getExportProgress());
                z9CrudVo.setCurrentNum(export.getCurrentNum());
                z9CrudVo.setTotalNum(export.getTotalNum());
                z9CrudVo.setSpendTime(export.getSpendTime());
            }
            return z9CrudVo;
        });
    }

    @Override
    public Z9CrudVo getDetailById(String id) {
        ChargeExport e = this.baseMapper.doGetDetailById(id);
        if (e == null) {
            return null;
        }
        ChargeExportVo z9CrudVo = BeanUtil.copyProperties(e, voClass());
        ChargeExport export = redisService.getCacheObject(EXPORT_CACHE_KEY + e.getId());
        if (export != null) {
            z9CrudVo.setExportProgress(export.getExportProgress());
            z9CrudVo.setCurrentNum(export.getCurrentNum());
            z9CrudVo.setTotalNum(export.getTotalNum());
            z9CrudVo.setSpendTime(export.getSpendTime());
        }
        return z9CrudVo;
    }

    @EventListener
    public void listenExportProgress(ChargeExportProgressEvent progressEvent) {
        Object cacheObject = redisService.getCacheObject(STOP_EXPORT + progressEvent.getId());
        Assert.isNull(cacheObject, "用户终止导出!");
        Integer totalNum = progressEvent.getTotalNum();
        Integer currentNum = progressEvent.getCurrentNum();
        double spendTime = progressEvent.getSpendTime();
        BigDecimal progress;
        if (totalNum == 0 && currentNum == 0) {
            progress = new BigDecimal("100");
        } else {
            progress = NumberUtil.div(currentNum, totalNum).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
        }
        ChargeExport export = new ChargeExport();
        export.setTotalNum(totalNum);
        export.setCurrentNum(currentNum);
        export.setSpendTime(BigDecimal.valueOf(spendTime).setScale(2, RoundingMode.HALF_UP).doubleValue());
        export.setExportProgress(progress);
        export.setId(progressEvent.getId());
        redisService.setCacheObject(EXPORT_CACHE_KEY + progressEvent.getId(), export, 1L, TimeUnit.HOURS);

    }

    @EventListener
    public void listenExportStatus(ChargeExportStatusEvent exportStatusEvent) {
        Integer status = exportStatusEvent.getStatus();
        String id = exportStatusEvent.getId();
        double spendTime = exportStatusEvent.getSpendTime();
        redisService.deleteObject(STOP_EXPORT + id);
        ChargeExport export = redisService.getCacheObject(EXPORT_CACHE_KEY + id);
        if (export == null) {
            export = new ChargeExport();
            export.setExportProgress(BigDecimal.ZERO);
            export.setCurrentNum(0);
            export.setTotalNum(0);
        }
        this.baseMapper.lambdaUpdate().set(ChargeExport::getExportStatus, status)
                .set(ChargeExport::getTotalNum, export.getTotalNum())
                .set(ChargeExport::getCurrentNum, export.getCurrentNum())
                .set(ChargeExport::getExportProgress, export.getExportProgress())
                .set(ChargeExport::getSpendTime, spendTime)
                .set(ChargeExport::getFileName, exportStatusEvent.getFileName())
                .set(ChargeExport::getUpdateTime, new Date())
                .set(StringUtils.isNotBlank(exportStatusEvent.getFailedReason()), ChargeExport::getFailedReason, exportStatusEvent.getFailedReason())
                .eq(ChargeExport::getId, id).update();
    }

    @Override
    public void export(String id) {
        Long userId = SecurityUtils.getUserId();
        long count = this.baseMapper.lambdaQuery().ne(ChargeExport::getId, id).eq(ChargeExport::getCreateBy, userId).eq(ChargeExport::getExportStatus, 0).count();
        Assert.isTrue(concurrency > count, TIP);
        ChargeExport one = this.baseMapper.lambdaQuery().eq(ChargeExport::getId, id).one();
        if (one == null) {
            return;
        }
        this.baseMapper.lambdaUpdate()
                .set(ChargeExport::getCreateTime, new Date())
                .set(ChargeExport::getFileName, "")
                .set(ChargeExport::getFailedReason, "")
                .set(ChargeExport::getExportStatus, 0)
                .eq(ChargeExport::getId, id).update();
        AfterCreateExportEvent afterCreateExportEvent = new AfterCreateExportEvent(one.getId(), one.getBusinessCode(), one.getRequestParams());
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishEvent(afterCreateExportEvent);
                }
            });
        } else {
            publishEvent(afterCreateExportEvent);
        }

    }

    @Override
    public void termination(String id) {
        this.baseMapper.lambdaUpdate()
                .set(ChargeExport::getExportStatus, 2)
                .set(ChargeExport::getUpdateTime, new Date())
                .set(ChargeExport::getFailedReason, "用户终止导出!")
                .eq(ChargeExport::getId, id)
                .update();
        redisService.setCacheObject(STOP_EXPORT + id, 1, 1L, TimeUnit.HOURS);
    }


    @Override
    public IExcelDictHandler getDictHandler() {
        return null;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return ChargeExportDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return ChargeExportVo.class;
    }

    @Override
    public String excelName() {
        return "导出列表";
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        Environment environment = applicationContext.getEnvironment();
        this.concurrency = environment.getProperty("z9.crud.export.concurrency", Integer.class, 1);
    }


    @Override
    public void run(String... args) throws Exception {

    }

}

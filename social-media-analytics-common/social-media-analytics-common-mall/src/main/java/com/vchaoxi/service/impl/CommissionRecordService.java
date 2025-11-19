package com.vchaoxi.service.impl;

import java.util.Date;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.bean.BeanUtil;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import com.vchaoxi.constant.SysConstant;
import com.vchaoxi.entity.CommissionRecord;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.mapper.CommissionRecordMapper;
import com.vchaoxi.param.CommissionRecordDto;
import com.vchaoxi.param.CommissionStatusDto;
import com.vchaoxi.param.CommissionWithdrawDto;
import com.vchaoxi.service.IVcUserUserService;
import com.vchaoxi.vo.CommissionIncomeVo;
import com.vchaoxi.vo.CommissionRecordVo;
import com.vchaoxi.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2025/08/27 18:08
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommissionRecordService extends AbstractZ9CrudServiceImpl<CommissionRecordMapper, CommissionRecord> {

    private final ChargeExcelDictHandler chargeExcelDictHandler;

    @Autowired
    private IVcUserUserService vcUserUserService;


    protected CommissionRecordService(CommissionRecordMapper baseMapper,
                                      ChargeExcelDictHandler chargeExcelDictHandler) {
        super(baseMapper);
        this.chargeExcelDictHandler = chargeExcelDictHandler;
    }


    public CommissionIncomeVo getCommissionIncome(String loginId) {
        CommissionIncomeVo commissionIncomeVo = new CommissionIncomeVo();
        String now = LocalDate.now() + " 00:00:00";
        String yesterday = LocalDate.now().minusDays(1) + " 00:00:00";
        BigDecimal yesterdayIncome = this.baseMapper.yesterdayIncome(loginId, yesterday, now);
        BigDecimal totalCommissionIncome = this.baseMapper.totalCommissionIncome(loginId);
        BigDecimal totalIncome = this.baseMapper.totalIncome(loginId);
        commissionIncomeVo.setTotalCommissionIncome(totalCommissionIncome);
        commissionIncomeVo.setTotalIncome(totalIncome);
        commissionIncomeVo.setYesterdayIncome(yesterdayIncome);
        commissionIncomeVo.setLoginId(loginId);
        return commissionIncomeVo;
    }

    public List<UserVo> getInvitedUser(String loginId) {
        List<VcUserUser> list = this.vcUserUserService.lambdaQuery().eq(VcUserUser::getInviteLoginId, loginId)
                .orderByDesc(VcUserUser::getInviteTime)
                .list();
        return BeanUtil.copyToList(list, UserVo.class);
    }

    public void updateStatus(CommissionStatusDto dto) {
        this.baseMapper.lambdaUpdate()
                .set(CommissionRecord::getStatus, dto.getStatus())
                .eq(CommissionRecord::getId, dto.getId())
                .update();
    }

    public void withdraw(CommissionWithdrawDto dto) {
        String loginId = dto.getLoginId();
        VcUserUser user = vcUserUserService.lambdaQuery().eq(VcUserUser::getLoginId, loginId).one();
        Assert.notNull(user, "用户不存在");
        Assert.hasText(user.getPhone(), "请前往个人中心完善手机号!");
        Assert.isTrue(user.getMemGrade() > 1, "请先开通会员");
        BigDecimal totalIncomeIncludeWaiting = this.baseMapper.totalIncomeIncludeWaiting(loginId);
        Assert.isTrue(totalIncomeIncludeWaiting.compareTo(BigDecimal.ZERO) > 0, "提现金额不足");
        BigDecimal amount = dto.getAmount();
        Assert.isTrue(totalIncomeIncludeWaiting.compareTo(amount) >= 0, "提现金额不足");

        CommissionRecord record = new CommissionRecord();
        record.setLoginId(loginId);
        record.setSourceLoginId(loginId);
        record.setPhone(user.getPhone());
        record.setType(SysConstant.COMMISSION_RECORD_WITHDRAW);
        record.setAmount(amount.negate());
        record.setStatus(SysConstant.COMMISSION_RECORD_WAITING);
        record.setOrderId(IdWorker.getIdStr());
        record.setGoodsInfo(Lists.newArrayList("提现"));
        this.baseMapper.insert(record);

    }

    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }


    @Override
    public Class<?> doGetDtoClass() {
        return CommissionRecordDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return CommissionRecordVo.class;
    }

    @Override
    public String excelName() {
        return "提成记录";
    }


}

package com.vchaoxi.payment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.chargehub.common.security.utils.SecurityUtils;
import com.chargehub.payment.PaymentCommandDecorator;
import com.chargehub.payment.bean.*;
import com.vchaoxi.constant.MemberConstant;
import com.vchaoxi.constant.OrderConstant;
import com.vchaoxi.entity.MemberOpenOption;
import com.vchaoxi.entity.MemberOpenRecord;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.service.IVcUserUserService;
import com.vchaoxi.service.impl.MemberOpenOptionService;
import com.vchaoxi.service.impl.MemberOpenRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 18:15
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class MallMemberPaymentHandler implements PaymentCommandDecorator {

    @Autowired
    private MemberOpenRecordService memberOpenRecordService;

    @Autowired
    private IVcUserUserService vcUserUserService;


    @Autowired
    private MemberOpenOptionService memberOpenOptionService;

    @Override
    public String businessTypeCode() {
        return "mallMember";
    }

    @Override
    public void paymentNotify(PaymentNotifyParam param) {

        MemberOpenRecord openRecord = this.memberOpenRecordService.getBaseMapper().lambdaQuery().eq(MemberOpenRecord::getId, param.getOutTradeNo()).one();
        Assert.notNull(openRecord, "开通记录不存在");

        this.memberOpenRecordService.getBaseMapper()
                .lambdaUpdate()
                .eq(MemberOpenRecord::getId, openRecord.getId())
                .set(MemberOpenRecord::getActive, 0)
                .update();

        this.memberOpenRecordService.getBaseMapper().lambdaUpdate()
                .eq(MemberOpenRecord::getId, param.getOutTradeNo())
                .set(MemberOpenRecord::getStatus, MemberConstant.PAID)
                .set(MemberOpenRecord::getActive, 1)
                .set(MemberOpenRecord::getOpenTime, new Date())
                .update();

        vcUserUserService.lambdaUpdate()
                .set(VcUserUser::getMemberExpireDate, openRecord.getEndDate())
                .set(VcUserUser::getMemGrade, MemberConstant.member)
                .eq(VcUserUser::getLoginId, openRecord.getUserId())
                .update();
    }

    @Override
    public void refundNotify(PaymentRefundNotifyParam param) {
        String outRefundNo = param.getOutRefundNo();
        MemberOpenRecord openRecord = this.memberOpenRecordService.getBaseMapper().lambdaQuery().eq(MemberOpenRecord::getRefundNo, outRefundNo).one();
        Assert.notNull(openRecord, "开通记录不存在");
        this.memberOpenRecordService.getBaseMapper().lambdaUpdate()
                .eq(MemberOpenRecord::getRefundNo, outRefundNo)
                .set(MemberOpenRecord::getStatus, MemberConstant.REFUNDED)
                .update();

        vcUserUserService.lambdaUpdate()
                .set(VcUserUser::getMemGrade, 1)
                .eq(VcUserUser::getLoginId, openRecord.getUserId())
                .update();
    }

    @Override
    public void createOrder(PaymentResult paymentResult) {
        log.info("create member order {}", paymentResult);
        Map<String, String> extendParam = paymentResult.getExtendParam();
        String memberOptionId = extendParam.get("memberOptionId");
        MemberOpenOption memberOpenOption = this.memberOpenOptionService.getBaseMapper().selectById(memberOptionId);
        Assert.notNull(memberOpenOption, "开通选项不存在");
        Long userId = SecurityUtils.getUserId();
        VcUserUser user = vcUserUserService.lambdaQuery().eq(VcUserUser::getLoginId, userId + "").one();
        Assert.notNull(user, "用户不存在");
        MemberOpenRecord openRecord = new MemberOpenRecord();
        openRecord.setId(paymentResult.getOutTradeNo());
        openRecord.setUserId(userId + "");
        openRecord.setType(memberOpenOption.getType());
        openRecord.setTypeName(memberOpenOption.getTypeName());
        openRecord.setMonths(memberOpenOption.getMonths());
        openRecord.setDays(memberOpenOption.getDays());
        openRecord.setOriginalPrice(memberOpenOption.getOriginalPrice());
        openRecord.setPrice(memberOpenOption.getPrice());
        openRecord.setPhone(user.getPhone());
        openRecord.setStatus(MemberConstant.PENDING);
        Date start = Objects.equals(user.getMemGrade(), 1) ? new Date() : user.getMemberExpireDate();
        DateTime end = Objects.equals(user.getMemGrade(), 1) ? DateUtil.offsetDay(start, memberOpenOption.getDays() + 1) : DateUtil.offsetDay(start, memberOpenOption.getDays());
        end.setHours(0);
        end.setMinutes(0);
        end.setSeconds(0);
        openRecord.setStartDate(start);
        openRecord.setEndDate(end);
        openRecord.setSource(1);
        openRecord.setActive(0);


        this.memberOpenRecordService.getBaseMapper().insert(openRecord);
    }

    @Override
    public void createRefund(PaymentRefundParam param) {
        log.info("create member refund {}", param);
        MemberOpenRecord openRecord = this.memberOpenRecordService.getBaseMapper().lambdaQuery().eq(MemberOpenRecord::getId, param.getOutTradeNo()).one();
        if (openRecord.getStatus().equals(MemberConstant.FAILED_REFUND)) {
            this.memberOpenRecordService.getBaseMapper().lambdaUpdate()
                    .eq(MemberOpenRecord::getId, openRecord.getId())
                    .set(MemberOpenRecord::getRefundNo, param.getOutRefundNo())
                    .set(MemberOpenRecord::getRefundRemarks, param.getReason())
                    .update();
            return;
        }
        this.memberOpenRecordService.getBaseMapper().lambdaUpdate()
                .eq(MemberOpenRecord::getId, param.getOutTradeNo())
                .set(MemberOpenRecord::getRefundRemarks, param.getReason())
                .set(MemberOpenRecord::getRefundNo, param.getOutRefundNo())
                .set(MemberOpenRecord::getStatus, MemberConstant.REFUNDING)
                .update();
    }

    @Override
    public void afterCreateRefund(PaymentRefundResult result) {
        if (!result.getRefundResult()) {
            this.memberOpenRecordService.getBaseMapper().lambdaUpdate()
                    .eq(MemberOpenRecord::getRefundNo, result.getOutRefundNo())
                    .set(MemberOpenRecord::getStatus, MemberConstant.FAILED_REFUND)
                    .update();
        }
    }


}

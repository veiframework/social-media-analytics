package com.vchaoxi.mapper;

import com.chargehub.common.security.template.mybatis.Z9MpCrudMapper;
import com.vchaoxi.entity.CommissionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * @author Zhanghaowei
 * @date 2025/08/27 18:07
 */
@Mapper
public interface CommissionRecordMapper extends Z9MpCrudMapper<CommissionRecord> {


    /**
     * 获取当前收益包括等待入账的
     *
     * @param loginId
     * @return
     */
    @Select("select IFNULL(sum(amount),0) as totalAmount from vc_commission_record where login_id = #{loginId}")
    BigDecimal totalIncomeIncludeWaiting(String loginId);

    /**
     * 获取当前收益
     *
     * @param loginId
     * @return
     */
    @Select("select IFNULL(sum(amount),0) as totalAmount from vc_commission_record where login_id = #{loginId}")
    BigDecimal totalIncome(String loginId);

    /**
     * 获取历史收益
     *
     * @param loginId
     * @return
     */
    @Select("select IFNULL(sum(amount),0) as totalAmount from vc_commission_record where login_id = #{loginId} and type = 'commission'")
    BigDecimal totalCommissionIncome(String loginId);


    /**
     * 获取昨日收益
     *
     * @param start
     * @param end
     * @param loginId
     * @return
     */
    @Select("select IFNULL(sum(amount),0) as totalAmount from vc_commission_record where login_id = #{loginId} and create_time >= #{start} and #{end} > create_time ")
    BigDecimal yesterdayIncome(@Param("loginId") String loginId,
                               @Param("start") String start,
                               @Param("end") String end);

}

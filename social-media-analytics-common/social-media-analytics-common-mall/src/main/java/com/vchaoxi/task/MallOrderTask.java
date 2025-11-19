package com.vchaoxi.task;

import com.chargehub.common.redis.service.RedisService;
import com.vchaoxi.constant.OrderConstant;
import com.vchaoxi.entity.VcOrderInfo;
import com.vchaoxi.entity.VcOrderOrder;
import com.vchaoxi.service.IVcOrderInfoService;
import com.vchaoxi.service.IVcOrderOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Zhanghaowei
 * @date 2025/08/11 19:23
 */
@Slf4j
public class MallOrderTask {

    @Autowired
    private RedisService redisService;

    @Autowired
    private IVcOrderInfoService vcOrderInfoService;

    @Autowired
    private IVcOrderOrderService vcOrderOrderService;


    @Scheduled(cron = "0 0/2 * * * ?")
    public void execute() {
        redisService.lock("mall_order_task", (lock) -> {
            log.info("订单超时开始...");
            vcOrderOrderService.lambdaUpdate()
                    .apply("insert_time <= DATE_SUB(NOW(), INTERVAL 2 MINUTE)")
                    .eq(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_NOT_PAY)
                    .set(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_TIMEOUT)
                    .update();
            vcOrderInfoService.lambdaUpdate()
                    .apply("insert_time <= DATE_SUB(NOW(), INTERVAL 2 MINUTE)")
                    .eq(VcOrderInfo::getStatus, OrderConstant.ORDER_DETAIL_STATUS_NOT_PAY)
                    .set(VcOrderInfo::getStatus, OrderConstant.ORDER_DETAIL_STATUS_TIMEOUT);
            log.info("订单超时结束...");
            return null;
        }, 30);
    }

    @Scheduled(cron = "0 10 5 * * ?")
    public void execute15day() {
        redisService.lock("mall_order_complete_task", (lock) -> {
            log.info("订单已完成开始...");
            vcOrderOrderService.lambdaUpdate()
                    .apply("insert_time <= DATE_SUB(NOW(), INTERVAL 7 DAY)")
                    .eq(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_SHIPPED)
                    .set(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_RECEIVED)
                    .update();
            vcOrderInfoService.lambdaUpdate()
                    .apply("insert_time <= DATE_SUB(NOW(), INTERVAL 7 DAY)")
                    .eq(VcOrderInfo::getStatus, OrderConstant.ORDER_STATUS_SHIPPED)
                    .set(VcOrderInfo::getStatus, OrderConstant.ORDER_STATUS_RECEIVED);
            log.info("订单已完成结束...");
            return null;
        }, 30);
    }

}

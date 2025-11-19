package com.vchaoxi.vo;

import lombok.Data;

//订单状态
@Data
public class OrderStatusVo {
    private Integer pendingPayment;//待付款
    private Integer pendingShipment;//待发货
    private Integer pendingDelivery;//待收货
}

package com.vchaoxi.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vchaoxi.entity.VcOrderInfo;
import com.vchaoxi.entity.VcOrderOrder;
import com.vchaoxi.entity.VcUserUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/1/10 13:55
 * @Project：vc-selling-server
 * @Package：com.vchaoxi.param
 * @Filename：OrderParam
 */
@Data
public class OrderParam {

    @NotNull(groups = {PayOrder.class,OrderRefund.class,ZjsDeliver.class ,SetTrackingNo.class,ConfirmReceipt.class, DelOrder.class})
    private Integer orderId;

    @NotEmpty(groups = {SetTrackingNo.class})
    private String trackingNo;

    //用户收费地址的id
    @NotNull(groups = {Create.class,CarCreate.class})
    private Integer receiveAddressId;

    private String goodsId;

    //添加规格的id
    @NotNull(groups = {Create.class})
    private Integer specificationId;
    //商品规格
    private String specifications;

    private Integer goodsNum;


    private VcUserUser curLoginUser;
    private String maOpenId;


    private VcOrderOrder vcOrderOrder;
    private String orderNotify;
    private String ip;
    private String tradeType;

    /**
     * 一个订单可能有多个vcOrderInfo，但是限定了都是同一箱格中的物品，所以前端请求时返回任意一个就行
     */
    private VcOrderInfo vcOrderInfo;
    private Integer plantform;

    @NotNull(groups = {Create.class,CarCreate.class})
    private Integer orderType;


    private Set<String> userImages;


    @ApiModelProperty("取件时间")
    private String pickTimeRange;

    @ApiModelProperty("是否揽件")
    private Integer pickingLogistics;

    @ApiModelProperty("是否配送")
    private Integer deliveryLogistics;

    private String group;

    public interface Create {}
    public interface CarCreate {}//通过购物车创建订单
    public interface PayOrder {}
    public interface DelOrder {}
    public interface OrderRefund {}//订单退款
    public interface SetTrackingNo{}//设置订单编号
    public interface ZjsDeliver{}//宅急送发货
    public interface ConfirmReceipt{}
}

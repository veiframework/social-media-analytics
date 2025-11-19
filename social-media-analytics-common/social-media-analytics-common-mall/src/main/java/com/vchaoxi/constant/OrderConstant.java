package com.vchaoxi.constant;



public class OrderConstant {


    /**
     * 订单状态
     * 0待支付   1已支付（待发货）  2超时未支付 3已发货 4收货完成 5退款操作 6已退款 7申请退款 8退款驳回 9 退款失败
     */
    public static final Integer ORDER_STATUS_NOT_PAY = 0;
    public static final Integer ORDER_STATUS_PAY = 1;
    public static final Integer ORDER_STATUS_TIMEOUT = 2;
    public static final Integer ORDER_STATUS_SHIPPED = 3;
    public static final Integer ORDER_STATUS_RECEIVED = 4;
    public static final Integer ORDER_STATUS_RETURNING = 5;
    public static final Integer ORDER_STATUS_RETURN = 6;
    public static final Integer ORDER_STATUS_REQUIRE_REFUND = 7;
    public static final Integer ORDER_STATUS_REJECT_REFUND = 8;
    public static final Integer ORDER_STATUS_FAILED_REFUND = 9;

    /**
     * 订单详情状态
     * 0待支付   1已支付（待发货）  2超时未支付 3已发货 4收货完成 5退款操作 6已退款
     */
    public static final Integer ORDER_DETAIL_STATUS_NOT_PAY = 0;
    public static final Integer ORDER_DETAIL_STATUS_PAY = 1;
    public static final Integer ORDER_DETAIL_STATUS_TIMEOUT = 2;
    public static final Integer ORDER_DETAIL_STATUS_SHIPPED = 3;
    public static final Integer ORDER_DETAIL_STATUS_RECEIVED = 4;
    public static final Integer ORDER_DETAIL_STATUS_RETURNING = 5;
    public static final Integer ORDER_DETAIL_STATUS_RETURN = 6;

    /**
     * 订单物流发货状态 0默认状态，可不用判断 1:已通知物流公司发货，待发货状态 2:物流公司已发货
     */
    public static final Integer DELIVERY_STATUS_DEFAULT = 0;
    public static final Integer DELIVERY_STATUS_PENDING = 1;
    public static final Integer DELIVERY_STATUS_SHIPPED = 3;

    /**
     * 订单支付记录状态
     * 支付状态  0待支付   1已支付   2超时未支付   3已发货  4收货完成 5退款操作 6已退款
     */
    public static final Integer ORDER_PAY_STATUS_NOT_PAY = 0;
    public static final Integer ORDER_PAY_STATUS_PAY = 1;
    public static final Integer ORDER_PAY_STATUS_TIMEOUT = 2;
    public static final Integer ORDER_PAY_STATUS_SHIPPED = 3;
    public static final Integer ORDER_PAY_STATUS_RECEIVED = 4;
    public static final Integer ORDER_PAY_STATUS_RETURNING = 5;
    public static final Integer ORDER_PAY_STATUS_RETURN = 6;

    /**
     * 支付方式
     * 支付方式   1微信支付 2 线下支付
     */
    public static final Integer PAY_WAY_WECHAT = 1;
    public static final Integer PAY_WAY_OFFLINE = 2;

    /**
     * 订单支付记录类型
     * 支付记录类型  1支付 2 退款操作
     */
    public static final Integer ORDER_PAY_RECORD_TYPE_PATY = 1;
    public static final Integer ORDER_PAY_RECORD_TYPE_REFUND = 2;


    /**
     * 操作类型
     * 101 管理员存放 102管理员回收
     * 201 用户开箱 202用户退还
     */
    public static final Integer OPERATE_TYPE_ADMIN_STORE = 101;
    public static final Integer OPERATE_TYPE_ADMIN_RECYCLE = 102;
    public static final Integer OPERATE_TYPE_USER = 201;
    public static final Integer OPERATE_TYPE_USER_RETURN = 202;














    /**
     * 用户类型  1用户  2配送员  3商家  4管理员
     */
    public static final Integer USER_TYPE_USER = 1;
    public static final Integer USER_TYPE_DISP = 2;
    public static final Integer USER_TYPE_SHOP = 3;
    public static final Integer USER_TYPE_ADMIN = 4;


    /**
     * 操作平台 1.用户端   2.配送员端  3.商家端  4.柜子端  5.erp端
     */
    public static final Integer PLANTFORM_USER = 1;
    public static final Integer PLANTFORM_DISP = 2;
    public static final Integer PLANTFORM_SHOP = 3;
    public static final Integer PLANTFORM_LOCKER = 4;
    public static final Integer PLANTFORM_ERP = 5;

    /**
     * 订单类型  1柜子订单  2门店订单  3预约上门 4校园订单
     */
    public static final Integer ORDER_TYPE_LOCKER = 1;
    public static final Integer ORDER_TYPE_STORE = 2;
    public static final Integer ORDER_TYPE_APPOINTMENT = 3;
    public static final Integer ORDER_TYPE_CAMPUS = 4;



    /**
     * 订单详情状态
     *
     * 0 待支付   2已取消   3超时未支付
     * 101：用户待存放   102：用户已存放   103：用户已回收    104.已退款
     * 201：配送员已取件 202：配送员已配送
     *
     * 399：配送员已取件（工厂端待入库或待出库状态）
     * 398：校园订单洗护中 infoStatus = 201 && (infoSortingStatus = 0 || infoSortingStatus = 1)
     * 397：校园订单待配送 infoStatus = 201 && infoSortingStatus = 2
     *
     *
     * 以下状态暂时不会出现在orderInfo表内的status内由factory_status字段表示
     * 301：工厂入库   302：工厂出库
     */

    public static final Integer ORDER_INFO_STATUS_NOT_PAY_INFO = -1;
    public static final Integer ORDER_INFO_STATUS_0 = 0;
    public static final Integer ORDER_INFO_STATUS_2 = 2;
    public static final Integer ORDER_INFO_STATUS_3 = 3;

    public static final Integer ORDER_INFO_STATUS_101 = 101;
    public static final Integer ORDER_INFO_STATUS_102 = 102;
    public static final Integer ORDER_INFO_STATUS_103 = 103;
    public static final Integer ORDER_INFO_STATUS_104 = 104;

    public static final Integer ORDER_INFO_STATUS_201 = 201;
    public static final Integer ORDER_INFO_STATUS_202 = 202;
    public static final Integer ORDER_INFO_STATUS_399 = 399;

    public static final Integer ORDER_INFO_STATUS_301 = 301;
    public static final Integer ORDER_INFO_STATUS_302 = 302;

    public static final Integer ORDER_INFO_STATUS_397 = 397;
}

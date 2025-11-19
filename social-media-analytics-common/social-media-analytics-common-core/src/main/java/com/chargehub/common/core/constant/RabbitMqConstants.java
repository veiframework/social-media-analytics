package com.chargehub.common.core.constant;

/**
 * @author Zhanghaowei
 * @date 2024/04/12 17:33
 */
public class RabbitMqConstants {

    private RabbitMqConstants() {
    }

    public static final String HEADER_ID_CODE = "idCode";

    //消息队列

    public static final String PILE_CONTROLLER_QUEUE = "pile.machine.control.queue";

    public static final String PILE_APP_MACHINE_QUEUE = "pile.app.machine.message.queue";

    public static final String PILE_SYSTEM_MACHINE_QUEUE = "pile.system.machine.message.queue";

    public static final String PILE_FAULT_QUEUE = "pile.app.machine.fault.queue";

    public static final String APP_ORDER_SETTLEMENT_QUEUE = "pile.app.order.statement.queue";

    public static final String SIJINENG_ORDER_SETTLEMENT_QUEUE = "pile.icn.order.push.queue";

    public static final String ICN_ORDER_QUEUE = "pile.app.icn.message.queue";

    public static final String WALLET_REFUND_QUEUE = "WALLET_REFUND_QUEUE";

    public static final String PC_REFUND_QUEUE = "PC.REFUND_QUEUE";

    public static final String PILE_ICN_MESSAGE_QUEUE = "pile.icn.machine.message.queue";

    public static final String ELECTRIC_PRICE_QUEUE = "ELECTRIC_PRICE_QUEUE";

    //-------------------------------------------------------------------------------------------

    //交换机

    public static final String EXCHANGE_SUFFIX = "Exchange";

    public static final String PILE_FANOUT_EXCHANGE = "pile-fanout-exchange";

    public static final String APP_ORDER_SETTLEMENT_EXCHANGE = "pile-app-order-statement-exchange";

    public static final String ICN_APP_ORDER_SETTLEMENT_EXCHANGE = "pile-icn-order-statement-exchange";


    //-------------------------------------------------------------------------------------------


    //生产者

    public static final String CONTROL_QUEUE = "pileControl";

    public static final String CHARGE_BILLING_TASK_PRODUCER = "chargeBillingTaskProducer";

    public static final String CHARGE_PILE_INFO_PRODUCER = "chargePileInfoProducer";

    public static final String CHARGE_PILE_ELECTRIC_PRICE_PRODUCER = "chargePileElectricProducer";

    public static final String CHARGE_PILE_START_CHARGE_PRODUCER = "chargePileStartChargeProducer";

    public static final String CHARGE_PILE_STOP_CHARGE_PRODUCER = "chargePileStopChargeProducer";

    public static final String CHARGE_ORDER_SETTLEMENT_PRODUCER = "chargeOrderSettlementProducer";

    public static final String SIJINENG_ORDER_SETTLEMENT_PRODUCER = "sijinengOrderSettlementProducer";

    public static final String CHARGE_PARAMETER_UPDATE_PRODUCER = "chargeParameterUpdateProducer";

    public static final String WALLET_REFUND_PRODUCER = "walletRefundProducer";

    public static final String ICN_ORDER_STATEMENT_PRODUCER = "icnOrderStatementProducer";

    public static final String REFUND_PRODUCER = "refundProducer";

    public static final String SWIPE_CARD_CONTROL_PRODUCER = "swipeCardControlProducer";

    public static final String DIRECT_STOP_CHARGE_PRODUCER = "directStopChargeProducer";

    public static final String REFUND_ALI_QUERY = "refundAliQueryProducer";

    public static final String INVOICE_PRODUCER = "invoiceProducer";

    public static final String ELECTRIC_PRICE_PRODUCER = "electricPriceProducer";

    public static final String SETTLEMENT_COMPLETE_PRODUCER = "settlementCompleteQueue";

    public static final String SETTLEMENT_EXCEPTION_PRODUCER = "settlementExceptionQueue";

    public static final String ICN_STATION_SYNC_PRODUCER = "icnStationSyncProducer";

    public static final String ICN_STATION_PRICE_SYNC_PRODUCER = "icnStationPriceSyncProducer";

    public static final String ICN_CONNECTOR_STATUS_SYNC_PRODUCER = "icnConnectorStatusSyncProducer";

    public static final String ICN_CHARGE_STAGE_PRODUCER = "icnChargeStageProducer";

    public static final String ICN_REALTIME_DATA_PRODUCER = "icnRealtimeDataProducer";

    public static final String ICN_CHARGE_FINISH_PRODUCER = "icnChargeFinishProducer";

    public static final String APP_MSG_PRODUCER = "pileAppMessage";
}

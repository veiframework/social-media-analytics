package com.chargehub.common.core.properties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2024/04/12 11:56
 */

public class ChargePileProperties {

    /**
     * 公共消费者队列,主要针对命令上报和命令下发的消费者
     */
    private String commandQueue;

    /**
     * 设备端控制下发消息队列
     */
    private String pileControlQueue;

    /**
     * app端和pc端各自队列不一样
     * 设备端推送消息队列名字
     */
    private String pileMessageQueue;

    /**
     * 设备故障处理队列
     */
    private String pileFaultQueue;

    /**
     * 桩通讯上报异常队列
     */
    private String pileSystemFaultQueue;

    /**
     * 设备端发送交换机
     */
    private String messageExchange;

    /**
     * app订单结算
     */
    private String appOrderStatementQueue;

    /**
     * 思极星能订单结算
     */
    private String sijinengOrderStatementQueue;

    /**
     * icn订单队列
     */
    private String icnOrderMessageQueue;

    /**
     * 钱包退款队列
     */
    private String walletRefundQueue;

    /**
     * pc退款队列
     */
    private String pcRefundQueue;

    /**
     * icn消息队列
     */
    private String pileIcnQueue;

    /**
     * 退款
     */
    private String refundQueue;

    private String aliRefundQueryQueue;

    private String invoiceQueue;

    private String electricPriceQueue;

    private String gunStateQueue;

    private String heartBeatQueue;

    private String requestElectricPriceQueue;

    private String chargeStageQueue;

    private String settlementCompleteQueue;

    /**
     * 扇形交换机队列绑定 key 交换机 value 队里集合
     */
    private List<String> fanoutExchangeQueues = new ArrayList<>();

    public List<String> getFanoutExchangeQueues() {
        return fanoutExchangeQueues;
    }

    public void setFanoutExchangeQueues(List<String> fanoutExchangeQueues) {
        this.fanoutExchangeQueues = fanoutExchangeQueues;
    }

    public String getMessageExchange() {
        return messageExchange;
    }

    public void setMessageExchange(String messageExchange) {
        this.messageExchange = messageExchange;
    }

    public String getPileControlQueue() {
        return pileControlQueue;
    }

    public void setPileControlQueue(String pileControlQueue) {
        this.pileControlQueue = pileControlQueue;
    }

    public String getPileMessageQueue() {
        return pileMessageQueue;
    }

    public void setPileMessageQueue(String pileMessageQueue) {
        this.pileMessageQueue = pileMessageQueue;
    }

    public String getPileFaultQueue() {
        return pileFaultQueue;
    }

    public String getElectricPriceQueue() {
        return electricPriceQueue;
    }

    public void setElectricPriceQueue(String electricPriceQueue) {
        this.electricPriceQueue = electricPriceQueue;
    }

    public void setPileFaultQueue(String pileFaultQueue) {
        this.pileFaultQueue = pileFaultQueue;
    }

    public String getCommandQueue() {
        return commandQueue;
    }

    public void setCommandQueue(String commandQueue) {
        this.commandQueue = commandQueue;
    }

    public String getAppOrderStatementQueue() {
        return appOrderStatementQueue;
    }

    public void setAppOrderStatementQueue(String appOrderStatementQueue) {
        this.appOrderStatementQueue = appOrderStatementQueue;
    }

    public String getSijinengOrderStatementQueue() {
        return sijinengOrderStatementQueue;
    }

    public void setSijinengOrderStatementQueue(String sijinengOrderStatementQueue) {
        this.sijinengOrderStatementQueue = sijinengOrderStatementQueue;
    }

    public String getIcnOrderMessageQueue() {
        return icnOrderMessageQueue;
    }

    public void setIcnOrderMessageQueue(String icnOrderMessageQueue) {
        this.icnOrderMessageQueue = icnOrderMessageQueue;
    }

    public String getWalletRefundQueue() {
        return walletRefundQueue;
    }

    public void setWalletRefundQueue(String walletRefundQueue) {
        this.walletRefundQueue = walletRefundQueue;
    }

    public String getPcRefundQueue() {
        return pcRefundQueue;
    }

    public void setPcRefundQueue(String pcRefundQueue) {
        this.pcRefundQueue = pcRefundQueue;
    }


    public String getPileIcnQueue() {
        return pileIcnQueue;
    }

    public void setPileIcnQueue(String pileIcnQueue) {
        this.pileIcnQueue = pileIcnQueue;
    }

    public String getRefundQueue() {
        return refundQueue;
    }

    public void setRefundQueue(String refundQueue) {
        this.refundQueue = refundQueue;
    }

    public String getPileSystemFaultQueue() {
        return pileSystemFaultQueue;
    }

    public void setPileSystemFaultQueue(String pileSystemFaultQueue) {
        this.pileSystemFaultQueue = pileSystemFaultQueue;
    }

    public String getAliRefundQueryQueue() {
        return aliRefundQueryQueue;
    }

    public void setAliRefundQueryQueue(String aliRefundQueryQueue) {
        this.aliRefundQueryQueue = aliRefundQueryQueue;
    }

    public String getInvoiceQueue() {
        return invoiceQueue;
    }

    public void setInvoiceQueue(String invoiceQueue) {
        this.invoiceQueue = invoiceQueue;
    }

    public String getGunStateQueue() {
        return gunStateQueue;
    }

    public void setGunStateQueue(String gunStateQueue) {
        this.gunStateQueue = gunStateQueue;
    }

    public String getHeartBeatQueue() {
        return heartBeatQueue;
    }

    public void setHeartBeatQueue(String heartBeatQueue) {
        this.heartBeatQueue = heartBeatQueue;
    }


    public String getRequestElectricPriceQueue() {
        return requestElectricPriceQueue;
    }

    public void setRequestElectricPriceQueue(String requestElectricPriceQueue) {
        this.requestElectricPriceQueue = requestElectricPriceQueue;
    }

    public String getChargeStageQueue() {
        return chargeStageQueue;
    }

    public void setChargeStageQueue(String chargeStageQueue) {
        this.chargeStageQueue = chargeStageQueue;
    }

    public String getSettlementCompleteQueue() {
        return settlementCompleteQueue;
    }

    public void setSettlementCompleteQueue(String settlementCompleteQueue) {
        this.settlementCompleteQueue = settlementCompleteQueue;
    }
}

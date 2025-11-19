package com.chargehub.common.core.exception.pay.baidu;

/**
 * @description: 百度支付异常
 * @author: lfy
 * @create: 2024-09-09 15:37
 */
public class BaiduPayException extends RuntimeException {

    public BaiduPayException(String msg)
    {
        super(msg);
    }
}

package com.chargehub.gateway.handler;

/**
 * @Author：xiaobaopiqi
 * @Date：2023/7/31 14:36
 * @Project：express-ark-server
 * @Package：com.youlin.express.gateway.util.exception
 * @Filename：ValidationException
 */
public class SignValidationException extends RuntimeException {

    public SignValidationException(String message) {
        super(message);
    }
}

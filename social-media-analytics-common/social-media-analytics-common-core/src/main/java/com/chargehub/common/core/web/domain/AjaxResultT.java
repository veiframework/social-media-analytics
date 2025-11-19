package com.chargehub.common.core.web.domain;

import com.chargehub.common.core.constant.HttpStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/13 15:23
 * @Project：chargehub
 * @Package：com.chargehub.common.core.web.domain
 * @Filename：AjaxResultT
 */
@Data
public class AjaxResultT<T> implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "结果描述",position = 1)
    private String msg;

    @ApiModelProperty(value = "状态码 成功 = 200",position = 2)
    private int code;

    @ApiModelProperty(value = "业务数据",position = 3)
    private T data;

    public AjaxResultT() {
    }

    /**
     * 初始化一个新创建的 AjaxResultT 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public AjaxResultT(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 初始化一个新创建的 AjaxResultT 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public AjaxResultT(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    @ApiModelProperty(hidden = true)
    public static AjaxResultT success() {
        return AjaxResultT.success("操作成功");
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    @ApiModelProperty(hidden = true)
    public static AjaxResultT<Void> successVoid() {
        return new AjaxResultT<>(HttpStatus.SUCCESS, "操作成功", null);
    }

    /**
     * 返回成功数据T
     *
     * @return 成功消息
     */
    @ApiModelProperty(hidden = true)
    public static  <U> AjaxResultT<U> success(U data) {
        return AjaxResultT.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    @ApiModelProperty(hidden = true)
    public static AjaxResultT success(String msg) {
        return AjaxResultT.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    @ApiModelProperty(hidden = true)
    public static  <U> AjaxResultT<U> success(String msg, U data) {
        return new AjaxResultT(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    @ApiModelProperty(hidden = true)
    public static AjaxResultT error() {
        return AjaxResultT.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    @ApiModelProperty(hidden = true)
    public static AjaxResultT error(String msg) {
        return AjaxResultT.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    @ApiModelProperty(hidden = true)
    public static <U> AjaxResultT<U> error(String msg, U data) {
        return new AjaxResultT(HttpStatus.ERROR, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    @ApiModelProperty(hidden = true)
    public static AjaxResultT error(int code, String msg) {
        return new AjaxResultT(code, msg, null);
    }

    /**
     * 是否为成功消息
     *
     * @return 结果
     */
    @ApiModelProperty(hidden = true)
    public boolean isSuccess() {
        return Objects.equals(HttpStatus.SUCCESS, this.code);
    }


    /**
     * 返回警告消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    @ApiModelProperty(hidden = true)
    public static AjaxResultT warn(String msg)
    {
        return AjaxResultT.warn(msg, null);
    }

    /**
     * 返回警告消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    @ApiModelProperty(hidden = true)
    public static AjaxResultT warn(String msg, Object data)
    {
        return new AjaxResultT(HttpStatus.WARN, msg, data);
    }


    /**
     * 返回成功消息
     * @param code
     * @param msg
     * @param data
     * @return
     */
    @ApiModelProperty(hidden = true)
    public static AjaxResultT warn(Integer code,String msg, Object data)
    {
        return new AjaxResultT(code, msg, data);
    }

    /**
     * 是否为错误消息
     *
     * @return 结果
     */
    @ApiModelProperty(hidden = true)
    public boolean isError() {
        return !isSuccess();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    @ApiModelProperty(hidden = true)
    public static AjaxResultT toAjax(boolean result)
    {
        return result ? success() : error();
    }


    /**
     * 响应返回结果
     *
     * @param count 受影响数据条数
     * @return 操作结果
     */
    @ApiModelProperty(hidden = true)
    public static AjaxResultT toAjax(Integer count)
    {
        return count > 0 ? success() : error();
    }


    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }
}


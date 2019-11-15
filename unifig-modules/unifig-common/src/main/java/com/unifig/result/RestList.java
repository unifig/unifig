/*
 * Copyright (C) 2017 Zhejiang BYCDAO Technology CO.,LTD.
 * All rights reserved.
 * Official Web Site: http://www.bycdao.com.
 * Developer Web Site: http://open.bycdao.com.
 */

package com.unifig.result;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * <h3>概要:</h3><p>集合数据包装</p>
 * <h3>功能:</h3><p>数据包装类</p>
 *
 * @create 2019/3/8
 * @since 1.0.0
 */
public class RestList<T> {

    /*
     * 调用结果
     */
    @ApiModelProperty(value = "调用结果")
    private boolean success;

    /*
     * 错误或成功代码
     */
    @ApiModelProperty(value = "调用结果")
    private int code;

    /*
     * 服务器端返回消息
     */
    @ApiModelProperty(value = "服务器端返回消息")
    private String msg;
    @ApiModelProperty(value = "语言")
    private String language;

    /*
     * 数据对象集合
     */
    @ApiModelProperty(value = "数据对象集合")
    private List<T> data;

    /**
     * 数据总数
     */
    @ApiModelProperty(value = "数据总数")
    private int count;

    /**
     * 页数总数
     */
    @ApiModelProperty(value = "页数总数")
    private double pageCount;

    public boolean isSuccess() {
        return success;
    }

    public RestList<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public int getCode() {
        return code;
    }

    public RestList<T> setCode(int code) {
        this.code = code;
        //获取对应的msg
        String desc = MsgCode.getMsg(code);
        setMsg(desc);
        return this;
    }

    public String getMsg() {
        if (code <= 0) {
            if (msg != null && !"".equals(msg)) {
                return msg;
            }
            code = SERVER_ERROR;
        }
        language = "zh_CN";
        return msg;
    }

    public RestList<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public RestList<T> setLanguage(String language) {
        this.language = language;
        return this;
    }

    public List<T> getData() {
        return data;
    }

    public RestList<T> setData(List<T> data) {
        this.data = data;
        return this;
    }

    public int getCount() {
        return count;
    }

    public RestList<T> setCount(int count) {
        this.count = count;
        return this;
    }

    public double getPageCount() {
        return pageCount;
    }

    public RestList<T> setPageCount(double pageCount) {
        this.pageCount = pageCount;
        return this;
    }

    public static <E> RestList<E> resultData(E t) {
        return new RestList<E>().setSuccess(true).setCode(SUCCESS)
                .setMsg(DEFAULT_SUCCESS_MSG);
    }

    public static <E> RestList<E> resultError() {
        return new RestList<E>().setSuccess(false).setCode(SERVER_ERROR)
                .setMsg(SERVER_ERROR_MSG);
    }

    /*
     * 服务器响应成功
     */
    public static final int SUCCESS = 200;
    public static final String DEFAULT_SUCCESS_MSG = "成功";

    /**
     * 服务器响应异常
     */
    public static final int SERVER_ERROR = 500;
    public static final String SERVER_ERROR_MSG = "服务器异常,请您稍后再试";
}

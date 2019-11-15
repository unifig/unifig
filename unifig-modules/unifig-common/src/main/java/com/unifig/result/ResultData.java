package com.unifig.result;

import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 返回前端的数据包装
 */
public class ResultData<T> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

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
     * 数据对象
     */
    @ApiModelProperty(value = "数据对象")
    private T data;

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

    public int getCount() {
        return count;
    }

    public ResultData setCount(int count) {
        this.count = count;
        double d = count;
        this.pageCount = Math.ceil(d / 10);
        return this;
    }

    public double getPageCount() {
        return pageCount;
    }

    public void setPageCount(double pageCount) {
        this.pageCount = pageCount;
    }

    public ResultData() {
    }

    public boolean success() {
        return success;
    }

    public int getCode() {
        return code;
    }

    public ResultData setCode(int code) {
        this.code = code;
        //获取对应的msg
        String desc = MsgCode.getMsg(code);
        setMsg(desc);
        return this;
    }


    public ResultData setCode(MsgCode code) {
        this.code = code.getCode();
        String msg = code.getMsg();
        this.msg = msg;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public ResultData setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    /**
     * 分解code返回提示信息 未设置code时，
     * 如果有msg信息直接返回msg，
     * 否则默认返回500错误并打印日志提示开发者
     */
    public String getMsg() {
        if (code <= 0) {
            if (msg != null && !"".equals(msg)) {
                return msg;
            }
            code = SERVER_ERROR;
            logger.warn("Msg Code is Invalid !");
        }
        language = "zh_CN";
        return msg;
    }

    public ResultData setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    public static ResultData result(boolean result) {
        return new ResultData().setSuccess(result)
                .setCode(result ? SUCCESS : SERVER_ERROR)
                .setMsg(result ? DEFAULT_SUCCESS_MSG : SERVER_ERROR_MSG);
    }

    public String getLanguage() {
        return language;
    }


    public T getData() {
        return data;
    }

    public ResultData setData(T data) {
        this.data = data;
        return this;
    }




    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ResultData [success=");
        stringBuilder.append(success);
        stringBuilder.append(", code=");
        stringBuilder.append(code);
        stringBuilder.append(", msg=");
        stringBuilder.append(msg);
        stringBuilder.append(", language=");
        stringBuilder.append(language);
        stringBuilder.append(", data=");
        stringBuilder.append(data);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        double d = 25d;
        double ceil = Math.ceil(d / 10);
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

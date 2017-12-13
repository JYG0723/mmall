package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * @作者: Ji YongGuang.
 * @修改时间: 19:30 2017/11/6.
 * @功能描述: 服务端响应对象
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化json的时候,如果是null的对象,key也会消失
public class ServerResponse<T> implements Serializable {
    // 要返回给前端，前后端传递需要序列化
    private int status;
    private String msg;
    private T data;

    /**
     * 构造方法私有化，外部不能new它。然后开放供外部使用的public方法。
     * 多个构造方法，应对数据类型如果和msg类型相同出现的方法调用错误的情况，
     * 而且public方法调用起来也比较优雅简明通用，static方法调用也方便且这样一来一看参数就知道如何调用。
     */
    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    //使之不在json序列化结果当中
    public boolean isSuccess() {
        // 根据id判断操作成功与否
        return this.status == ResponseCodeEnum.SUCCESS.getCode();
    }

    // 请求成功只返回状态码用于给前段判断
    public static <T> ServerResponse<T> createBySuccess() {
        return new ServerResponse<T>(ResponseCodeEnum.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccess(T data) {
        return new ServerResponse<T>(ResponseCodeEnum.SUCCESS.getCode(), data);
    }

    // 请求成功但仍旧需要返回一个文本，供前端提示使用
    public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
        return new ServerResponse<T>(ResponseCodeEnum.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
        return new ServerResponse<T>(ResponseCodeEnum.SUCCESS.getCode(), msg, data);
    }

    // 公共错误，比如404 不用描述，直接跳转404页面
    public static <T> ServerResponse<T> createByError() {
        return new ServerResponse<T>(ResponseCodeEnum.ERROR.getCode(), ResponseCodeEnum.ERROR.getDesc());
    }

    // 需要提示，比如注册用户，提示用户已经存在
    public static <T> ServerResponse<T> createByErrorMessage(String errorDesc) {
        return new ServerResponse<T>(ResponseCodeEnum.ERROR.getCode(), errorDesc);
    }

    // 针对ResponseCode类中各种封装出的错误提供的接口
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorDesc) {
        return new ServerResponse<T>(errorCode, errorDesc);
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}

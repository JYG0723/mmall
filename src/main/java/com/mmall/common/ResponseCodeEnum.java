package com.mmall.common;

/**
 * @作者: Ji YongGuang.
 * @修改时间: 19:35 2017/11/6.
 */
public enum ResponseCodeEnum {

    // 通用成功，错误码
    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),

    NEED_LOGIN(10, "NEED_LOGIN"),
    ILLEGAL_ARGUEMENT(2, "ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;

    private ResponseCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}

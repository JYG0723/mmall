package com.mmall.common;

/**
 * @作者: Ji YongGuang.
 * @修改时间: 14:53 2017/11/9.
 * @功能描述: 常量管理类
 */
public class Const {

    private Const() {
    }

    public static final String CURRENT_USER = "currentUser";

    // 这里没有使用interface进行分组是因为 这两个对象不好分组。一般对立的两个实体才好分组
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    // 普通用户和管理员是一个组，枚举过于繁重
    // 内部接口类 把常量进行分组。没有枚举重，但还可以分组，而且里面还是常量
    public interface Role {
        int ROLE_CUSTOMER = 0; // 普通用户
        int ROLE_ADMIN = 1; // 管理员
    }
}
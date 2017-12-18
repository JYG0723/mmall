package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @作者: Ji YongGuang.
 * @修改时间: 14:53 2017/11/9.
 * @功能描述: 常量管理类, final 避免被继承，private构造
 */
public final class Const {

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

    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc", "price_desc");
    }

    public enum ProductStatusEnum {
        ON_SALE(1, "在线");

        private int code;
        private String value;

        ProductStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    public interface Cart {
        int CHECKED = 1;// 购物车中该商品选中状态
        int UN_CHECKED = 0;// 购物车中该商品未选中状态

        String LIMIT_COUNT_FAIL = "LIMIT_COUNT_FAIL";
        String LIMIT_COUNT_SUCCESS = "LIMIT_COUNT_SUCCESS";
    }
}
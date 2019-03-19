package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @作者: Ji YongGuang.
 * @修改时间: 19:08 2017/11/13.
 * @功能描述:
 */
public class TokenCache {

    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    public static final String TOKEN_PREFIX = "token_";

    // 声明一个静态的内存块
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder()
            .initialCapacity(1000) // 缓存的初始容量
            .maximumSize(10000) // 缓存的最大容量,超过这个容量Guava的Cache会使用LRU算法(最近最少使用)来移除缓存项。
            .expireAfterAccess(12, TimeUnit.HOURS) // 有效期，12 - TimeUnit.HOURS[小时]
            // 默认的数据加载实现,当调用get取值的时候(43行代码处),如果key没有对应的值,就调用这个方法进行加载.
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String s) throws Exception {
                    // 防止调用时出现空指针异常
                    return "null";
                }
            });

    public static void setKey(String key, String value) {
        localCache.put(key, value);
    }

    public static String getKey(String key) {
        String value = null;
        try {
            value = localCache.get(key);
            if ("null".equals(value)) {
                return null;
            }
            return value;
        } catch (Exception e) {
            logger.error("localCache get error!", e);
        }
        return null;
    }

    /**
     * 一次成功修改密码之后，使该token失效
     *
     * @param key
     */
    public static void removeKey(String key) {
        localCache.invalidate(key);
    }

}

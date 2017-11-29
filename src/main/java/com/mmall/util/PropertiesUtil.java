package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @作者: Ji YongGuang.
 * @修改时间: 19:14 2017/11/27.
 * @功能描述: 读取配置文件的工具类
 */
public class PropertiesUtil {

    // 工具类一般最好都带上日志
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    // 因为是工具类，所以方法应该都是静态的，所以常用到的对象要声明称{全局}的且{静态}的。
    private static Properties props;

    // 因为tomcat启动的时候就要读取到配置文件。所以应该用到静态块来处理这类问题。
    // 静态代码块只会在类被加载的时候执行一次。一般用它初始化静态变量，比如声明的全局静态变量props
    // 当Java的ClassLoader加载到PropertiesUtil这个类的时候，这个静态块就会被先执行
    static {
        String fileName = "mmall.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),
                    "UTF-8"));
        } catch (IOException e) {
            logger.error("配置文件读取异常", e);
        }
    }

    public static String getProperty(String key) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            // 避免无异议的返回值，k-v都要做trim处理
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key, String defaultValue) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            // 如果配置文件中忘记配置这个key，代码中也有默认值来兜底
            value = defaultValue;
        }
        return value.trim();
    }

}

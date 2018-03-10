package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Ji YongGuang.
 * @time 18:26 2017/11/27.
 * 时间转换工具类。使用了jodaTime开源包来做
 */
public class DateTimeUtil {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);

    public static Date strToDate(String dateTimeStr, String formatStr) {
        // 根据写入的格式注册 格式化类
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        // 转换传入的时间字符串 字符串 -》 时间类
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        // 根据dateTime然后初始化一个含有时间dateTimeStr的Date
        return dateTime.toDate();
    }

    public static String dateToStr(Date date, String formatStr) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        // 通过date new一个Joda的DateTime
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    /**
     * 标准方式
     * 不需要重复传格式了，前两种对于标准的pattern调用不便
     *
     * @param dateTimeStr
     * @return
     */
    public static Date strToDate(String dateTimeStr) {
        // 根据写入的格式注册 格式化类
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        // 解析传入的字符串式的时间 -》 时间类
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * 标准方式
     * 不需要重复传格式了，前两种对于标准的pattern调用不便
     *
     * @param date
     * @return
     */
    public static String dateToStr(Date date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString("yyyy-MM-dd HH:mm:ss");
    }

}

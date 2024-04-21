package com.anynote.core.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * 向下取整到最近的小时。
     * @param date 需要处理的日期对象
     * @return 取整后的日期对象
     */
    public static Date roundDownToHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 向上取整到下一个小时。
     * @param date 需要处理的日期对象
     * @return 取整后的日期对象
     */
    public static Date roundUpToHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 1); // 如果已经是整点，这里会多加一个小时
        return calendar.getTime();
    }

    public static Calendar buildCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String getCurrentDateString() {
        // 获取当前日期
        LocalDate today = LocalDate.now();
        // 创建格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        // 格式化日期
        return today.format(formatter);
    }

    public static Date getYesterdayUsingCalendar() {
        Calendar calendar = Calendar.getInstance();  // 获取当前日期和时间的Calendar实例
        calendar.add(Calendar.DATE, -1);             // 将日期减一天来获取昨天的日期
        return calendar.getTime();                  // 将Calendar转换为Date
    }

    public static String getDateString(Date date) {
        // 将Date转换为LocalDateTime
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        // 创建格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        // 格式化日期
        return formatter.format(localDateTime);
    }

}

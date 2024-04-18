package com.anynote.core.utils;

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

}

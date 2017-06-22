package com.huangyu.mdfolder.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by huangyu on 2017-5-16.
 */
public class DateUtils {

    private static String DATE_FORMAT_yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";

    private DateUtils() {
    }

    /**
     * Date转String
     *
     * @param date 日期
     * @return String
     */
    public static String dateToString(Date date) {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_yyyyMMddHHmmss, Locale.getDefault());
        return format.format(date);
    }

    /**
     * String转Date
     *
     * @param str 时间字符串
     * @return Date
     */
    public static Date stringToDate(String str) {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_yyyyMMddHHmmss, Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 时间转换成中文
     *
     * @param time time
     * @return the string
     */
    public static String getFormatDate(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_yyyyMMddHHmmss, Locale.getDefault());
        return format.format(calendar.getTime());
    }

}

package com.xiaomi.common.util;

import org.xml.sax.SAXException;

import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeHelper {
    static final String TAG = DateTimeHelper.class.getName();

    public static final TimeZone sBeijingTimeZone = TimeZone.getTimeZone("Asia/Shanghai");

    /*
     * 获取当前timestamp（基于北京时区）
     */
    public static final long getCurrentTiemstamp() {
        return Calendar.getInstance(DateTimeHelper.sBeijingTimeZone).getTimeInMillis();
    }

    /*
     * 计算今天开始0:00 AM的timestamp（基于北京时区）
     */
    public static final long getTodayStartTimestamp() {
        long timestamp = getCurrentTiemstamp();
        return getTodayStartTimestamp(timestamp);
    }

    /*
     * 计算给定timestamp时间对应到当天0:00 AM的timestamp（基于北京时区）
     */
    public static final long getTodayStartTimestamp(long timestamp) {
        return timestamp - timestamp % sDayInMilliseconds;
    }

    /*
     * 计算给定timestamp时间对应到第二天0:00 AM的timestamp（基于北京时区）
     */
    public static final long getTomorrowStartTimestamp(long timestamp) {
        return timestamp - timestamp % sDayInMilliseconds + sDayInMilliseconds;
    }

    /*
     * 计算今天已过几分钟（基于北京时区）
     */
    public static final long getElapsedMinutesFromToday() {
        long timestamp = getCurrentTiemstamp();
        return getElapsedMinutesFromToday(timestamp);
    }

    /*
     * 计算给定timestamp时间对应到今天已过几分钟（基于北京时区）
     */
    public static final long getElapsedMinutesFromToday(long timestamp) {
        return (timestamp - getTodayStartTimestamp(timestamp)) / sMinuteInMilliseconds;
    }

    /*
     * 计算当前小时已过几分钟（基于北京时区）
     */
    public static final long getElapsedMinutesFromHour() {
        long timestamp = getCurrentTiemstamp();
        return getElapsedMinutesFromHour(timestamp);
    }

    /*
     * 计算给定timestamp时间对应到当前小时已过几分钟（基于北京时区）
     */
    public static final long getElapsedMinutesFromHour(long timestamp) {
        return getElapsedMinutesFromToday(timestamp) % sHourInMinutes;
    }

    /**
     * 以 2010-10-30 的格式解析一个时间成beijing +800时区的unix timestamp
     *
     * @param date
     * @return
     * @throws SAXException
     */
    public static long parseDate(String date) throws SAXException {
        if (TextUtils.isEmpty(date))
            return -1;

        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            gc.setTime(format.parse(date));
            gc.setTimeZone(DateTimeHelper.sBeijingTimeZone);
            return gc.getTimeInMillis();
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Failed to parse date", e);
        }
        return -1;
    }

    public static Date fromString(String src, String pattern) {
        Date date = null;
        if (!TextUtils.isEmpty(src)) {
            try {
                date = new SimpleDateFormat(pattern).parse(src);
            } catch (ParseException e) {
                Log.d(TAG, "", e);
            }
        }

        return date;
    }

    public static String toString(Date date, String pattern) {
        String ret = "";
        if (date != null) {
            ret = new SimpleDateFormat(pattern).format(date);
        }

        return ret;
    }

    public static String getCurrentString(String pattern) {
        return getTimeString(System.currentTimeMillis(), pattern);
    }

    public static String getTimeString(long time, String pattern) {
        return new SimpleDateFormat(pattern).format(new Date(time));
    }

    public static final long sDayInMilliseconds = 24 * 3600 * 1000;
    public static final long sHourInMilliseconds = 3600 * 1000;
    public static final long sMinuteInMilliseconds = 60 * 1000;
    public static final long sDayInMinutes  = sDayInMilliseconds / sMinuteInMilliseconds;
    public static final long sHourInMinutes  = sHourInMilliseconds / sMinuteInMilliseconds;

    private static final String LOG_TAG = "common/DateTimeHelper";
}

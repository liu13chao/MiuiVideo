/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  TimeUtils.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-17
 */
package com.miui.video.util;

import java.util.Calendar;

/**
 * @author tianli
 *
 */
public class TimeUtils {

    public static int getDay(){
        long ts = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ts);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    
    public static int getMonth(){
        long ts = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ts);
        return calendar.get(Calendar.MONTH ) + 1;
    }
    
    public static int getYear(){
        long ts = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ts);
        return calendar.get(Calendar.YEAR);
    }
    
    public static int getYesterdayDay(){
        long ts = System.currentTimeMillis() - 24 * 3600000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ts);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    
    public static int getYesterdayMonth(){
        long ts = System.currentTimeMillis() - 24 * 3600000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ts);
        return calendar.get(Calendar.MONTH) + 1;
    }
    
    public static int getYesterdayYear(){
        long ts = System.currentTimeMillis() - 24 * 3600000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ts);
        return calendar.get(Calendar.YEAR);
    }
  
    public static String parseTime(int ms){
        StringBuilder builder = new StringBuilder();
        int hour = ms/ 1000 / 3600; 
        if(hour < 10){
            builder.append("0");
        }
        builder.append(hour);
        builder.append(":");
        int minute = ms / 1000 % 3600 / 60;
        if(minute < 10){
            builder.append("0");
        }
        builder.append(minute);
        builder.append(":");
        int second = ms / 1000 % 3600 % 60;
        if(second < 10){
            builder.append("0");
        }
        builder.append(second);
        return builder.toString();
    }
    
    public static String parseShortTime(int ms){
        StringBuilder builder = new StringBuilder();
        int hour = ms/ 1000 / 3600; 
        if(hour > 0){
            if(hour < 10){
                builder.append("0");
            }
            builder.append(hour);
            builder.append(":");
        }
        int minute = ms / 1000 % 3600 / 60;
        if(minute < 10){
            builder.append("0");
        }
        builder.append(minute);
        builder.append(":");
        int second = ms / 1000 % 3600 % 60;
        if(second < 10){
            builder.append("0");
        }
        builder.append(second);
        return builder.toString();
    }
    
}

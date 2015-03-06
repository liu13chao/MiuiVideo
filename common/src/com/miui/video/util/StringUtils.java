/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  StringUtils.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-18
 */
package com.miui.video.util;

/**
 * @author tianli
 *
 */
public class StringUtils {

    public static String formatString(String format, Object... args){
        try{
            return String.format(format, args);
        }catch(Exception e){
        }
        return "";
    }
    
    public static String avoidNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
}

/**
 *   Copyright(c) 2012 DuoKan TV Group
 *
 *   Hex.java
 *
 *   @author tianli (tianli@duokan.com)
 *
 *   @date 2012-6-25
 */
package com.miui.video.util;

/**
 * @author tianli
 *
 */
public class Hex{
	
    public static String byte2Hex(byte[] bytes){
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String str = Integer.toHexString(0xFF & b);
            while (str.length() < 2)
            {
                str = "0" + str;
            }
            hexString.append(str);
        }
        return hexString.toString();
    }
}

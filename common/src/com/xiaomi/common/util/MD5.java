package com.xiaomi.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 这里MD5_16取了MD5_32的中间16位
 *
 */
public class MD5 {

    private static String byte2Hex(byte b) {
        int value = (b & 0x7F) + (b < 0 ? 0x80 : 0);
        return (value < 0x10 ? "0" : "")
                + Integer.toHexString(value).toLowerCase();
    }

    public static String MD5_32(String passwd) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        StringBuffer strbuf = new StringBuffer();

        md5.update(passwd.getBytes(), 0, passwd.length());
        byte[] digest = md5.digest();

        for (int i = 0; i < digest.length; i++) {
            strbuf.append(byte2Hex(digest[i]));
        }

        return strbuf.toString();
    }

    public static String MD5_16(String passwd)  {
        return MD5_32(passwd).subSequence(8, 24).toString();
    }

}

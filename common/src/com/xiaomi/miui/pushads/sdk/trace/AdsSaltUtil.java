package com.xiaomi.miui.pushads.sdk.trace;

import android.util.Base64;
import android.util.Log;

import org.apache.http.NameValuePair;

import com.xiaomi.miui.pushads.sdk.NotifyAdsDef;


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * do the encrypt with fix uuid  on parameters
 * @author liuwei
 *
 */
class AdsSaltUtil {

    public static String getKeyFromParams(List<NameValuePair> nameValuePairs, String publishId) {
        Collections.sort(nameValuePairs, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair p1, NameValuePair p2) {
                return p1.getName().compareTo(p2.getName());
            }
        });

        StringBuilder keyBuilder = new StringBuilder();
        boolean isFirst = true;
        for (NameValuePair nvp : nameValuePairs) {
            if (!isFirst) {
                keyBuilder.append("&");
            }
            keyBuilder.append(nvp.getName()).append("=").append(nvp.getValue());
            isFirst = false;
        }

        keyBuilder.append("&").append(publishId);

        String key = keyBuilder.toString();
        byte[] keyBytes = getBytes(key);
        return getMd5Digest(new String(Base64.encode(keyBytes, Base64.NO_WRAP)));
    }

    private static byte[] getBytes(String s) {
        try {
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s.getBytes();
        }
    }

    public static String getMd5Digest(String pInput) {
        if (pInput == null) return "";

        try {
            MessageDigest lDigest = MessageDigest.getInstance("MD5");
            lDigest.update(getBytes(pInput));
            BigInteger lHashInt = new BigInteger(1, lDigest.digest());
            return String.format("%1$032X", lHashInt);
        } catch (NoSuchAlgorithmException lException) {
            throw new RuntimeException(lException);
        }
    }

    public static void show(String info) {
        if (NotifyAdsDef.DEBUG_MODE)
            Log.d(LogDef.LOG_TAG, info);
    }

    public static void showStatus(String info) {
        if (NotifyAdsDef.DEBUG_MODE)
            Log.d(LogDef.LOG_TAG_STATUS, info);
    }
}


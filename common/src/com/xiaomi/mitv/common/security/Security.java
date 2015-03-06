/**
 *   Copyright(c) 2012 DuoKan TV Group
 *
 *   Security.java
 *
 *   @author tianli (tianli@duokan.com)
 *
 *   @date 2012-6-24
 */
package com.xiaomi.mitv.common.security;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.miui.video.util.DKLog;

import android.util.Base64;

/**
 * @author tianli
 */
public class Security {
	
	private static final String TAG = Security.class.getName();
    private static final String HMAC_SHA1 = "HmacSHA1";
    
    public static String signature(byte[] data, byte[] key, String algorithm) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(signingKey);
            byte[] bytes = mac.doFinal(data);
            return byte2Hex(bytes);
        }catch(Exception e){
        }
        return null;
    }
    
    public static String signature(byte[] data, byte[] key) {
        return signature(data, key, HMAC_SHA1);
    }
    
    public static String decrypt(byte[] base64Data, byte[] key) {
		if (base64Data == null || key == null) {
			return null;
		}
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES/CBC/NoPadding");
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
			byte[] decodedData = Base64.decode(base64Data, Base64.DEFAULT);
			byte[] decryptedData = cipher.doFinal(decodedData);
			return new String(decryptedData);
		} catch(Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		return null;
	}
    
    public static String byte2Hex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes)  {
            String str = Integer.toHexString(0xFF & b);
            while (str.length() < 2){
                str = "0" + str;
            }
            hexString.append(str);
        }
        return hexString.toString();
    }
   
}

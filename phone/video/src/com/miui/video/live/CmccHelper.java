/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   CmccHelper.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-8-12
 */

package com.miui.video.live;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.miui.video.util.DKLog;
import com.miui.video.util.Util;


/**
 * @author tianli
 *
 */
public class CmccHelper {
	
	public final static String TAG = "CmccHelper";
	
    
    public static final String CMCC_CHANNELID = "200900000050000";
    
    private static String CMCC_KEY = null;    //"70b21736b418ccd8d6082f81cb1fb520";
//    private static String CMCC_KEY = "70b21736b418ccd8d6082f81cb1fb520";
    private static String CMCC_APPID = null;
//    public static final String CMCC_APPID_DUOKAN = "002012091025085010076091029066008084087093";
//    public static final String CMCC_APPID_MIUI = "000088085024088095077008023023015002087090";
    public static final String CMCC_APPID_DUOKAN = "4ef73d3b82eed85e8cb3a1f3e43f6585";
    public static final String CMCC_APPID_MIUI = "1a3d3e2c766bbde8f4e09cb398780b52";
    public static final String FINGERPRINT_DUOKAN = "495a6be86a31a3f568793ea3d9883d90";
    
    public static void setSecretKey(String key) {
    	CMCC_KEY = key;
    }
    
    public static String getSecretKey(){
    	return CMCC_KEY;
    }
    
    private static String getSign(Context context) { 
    	try{
            PackageManager pm = context.getPackageManager();
            String packageName = context.getPackageName();
            int flags = PackageManager.GET_SIGNATURES;
            PackageInfo packageInfo = null;
            packageInfo = pm.getPackageInfo(packageName, flags);
            Signature[] signatures = packageInfo.signatures;
            byte[] cert = signatures[0].toByteArray();
            String digest = Util.getMD5(cert);
            return digest;
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    	return "";
    } 
    
    public static String getAppId(Context context){
    	if(CMCC_APPID == null){
    		String fingerPrint = getSign(context);
        	DKLog.d(TAG, "fingerPrint:" + fingerPrint);
    		if(FINGERPRINT_DUOKAN.equals(fingerPrint)){
    			CMCC_APPID = CMCC_APPID_DUOKAN;
    		}else{
    			CMCC_APPID = CMCC_APPID_MIUI;
    		}
    	}
    	return CMCC_APPID;
    }
    
}

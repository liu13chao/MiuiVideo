/**
 *  Copyright(C) 2012 DuoKan TV Group
 * 
 *  AppConfig.java  
 * 
 *  @author tianli (tianli@duokan.com)
 *
 *  @date 2012-6-20
 */
package com.miui.video.model;


import miui.os.Build;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.miui.video.DKApp;
import com.miui.video.api.def.DeviceTypeValueDef;

/**
 * @author tianli
 * 
 */
public class AppConfig  extends AppSingleton{
	
	public boolean mIsCmCustomization = false;
	
	private String mVersion;
	private int mVersionCode;

	private boolean mTest = false;
	
	private boolean mIsOnlyShowOnlineVideo = false;
	private boolean mIsOnlyShowLocalVideo = false;
	private boolean mIsShowAll = true;

	@Override
	public void init(Context context) {
	    super.init(context);
	    PackageManager packageManager = context.getPackageManager();
	    try {
	        PackageInfo packageInfo = packageManager.getPackageInfo(
	                context.getPackageName(), 0);
	        mVersion = packageInfo.versionName;
	        mVersionCode = packageInfo.versionCode;
	        
	        ApplicationInfo ai = packageManager.getApplicationInfo(
	                context.getPackageName(), PackageManager.GET_META_DATA);
	        mTest = ai.metaData.getBoolean("Test");
	        try{
	            mIsCmCustomization = Build.IS_CM_CUSTOMIZATION; 
	        }catch(Throwable t){
	        }
	        if( mIsCmCustomization) {
	            mIsOnlyShowLocalVideo = true;
	            mIsOnlyShowOnlineVideo = false;
	            mIsShowAll = false;
	        }
	    } catch (Throwable e) {
	    }
	}

	public String getVersion() {
		return mVersion;
	}

    public int getVersionCode() {
		return mVersionCode;
	}
	
	public String getMiuiVer() {
		return "6";
	}

	public String getApiVer() {
		DeviceInfo deviceInfo = DKApp.getSingleton(DeviceInfo.class);
		int deviceType = deviceInfo.getDeviceType();
		if(deviceType == DeviceTypeValueDef.DEVICE_TYPE_PAD) {
			return "3.0";
		}
		return "4.0";     
	}

	public boolean isTest() {
		return mTest;
	}
	
	public boolean isOnlyShowOnlineVideo() {	
		return mIsOnlyShowOnlineVideo;
	}
	
	public boolean isOnlyShowLocalVideo() {
		return mIsOnlyShowLocalVideo;
	}
	
	public boolean isShowAll() {
		return mIsShowAll;
	}
	
	public boolean isLocalVideoOn(){
		return isOnlyShowLocalVideo() || isShowAll();
	}
	
	public boolean isOnlineVideoOn(){
		return isOnlyShowOnlineVideo() || isShowAll();
	}
}

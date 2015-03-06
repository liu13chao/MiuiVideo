/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   StatisticInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-19
 */

package com.miui.video.statistic;

import java.io.Serializable;

import org.json.JSONException;

import com.miui.video.DKApp;
import com.miui.video.model.AppConfig;
import com.miui.video.model.DeviceInfo;
import com.miui.video.type.SerializeJSONObject;
import com.miui.video.util.DKLog;

/**
 *@author xuanmingliu
 *
 */

public abstract class StatisticInfo implements Serializable{
	private static final long serialVersionUID = 2L;
	private static final String TAG = StatisticInfo.class.getName();
	protected SerializeJSONObject jsonObject = new SerializeJSONObject();
	protected String mImei = "";
	private int mIpAddress;
	private int mAppVersion;
	
	public StatisticInfo(){
		DeviceInfo deviceInfo = DKApp.getSingleton(DeviceInfo.class);
		mImei = deviceInfo.getImeiMd5(); 
		mIpAddress = deviceInfo.getIpAddress();
		AppConfig config = DKApp.getSingleton(AppConfig.class);
		mAppVersion = config.getVersionCode();
	}
	
	public String formatToJson() {
		try {
			long currentTime = System.currentTimeMillis();
			if(canUploadImei()){
				jsonObject.put(StatisticTagDef.IME_TAG, mImei);
			}
			jsonObject.put(StatisticTagDef.APP_VERSION_TAG, mAppVersion);
			jsonObject.put(StatisticTagDef.TIME_TAG, currentTime);
			jsonObject.put(StatisticTagDef.IP_TAG, mIpAddress);
		} catch (JSONException e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		return jsonObject.toString();
	}
	
	protected boolean canUploadImei(){
		return true;
	}
}



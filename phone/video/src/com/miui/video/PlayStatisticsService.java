package com.miui.video;

import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.miui.video.api.DKApi;
import com.miui.video.model.DeviceInfo;
import com.miui.video.statistic.StatisticTagDef;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;

public class PlayStatisticsService extends IntentService {

	public static final String TAG = PlayStatisticsService.class.getName();
//	private String mImei = "";
	private int appVersion = DKApp.getSingleton(DeviceInfo.class).getAppVersion();
	private String upload_info;
	public PlayStatisticsService() {
		super(TAG);
	}
	
	public PlayStatisticsService(String name) {
		super(name);
	}

	@Override
	public IBinder onBind(Intent intent) {
		DKLog.d(TAG, "onBind");
		return super.onBind(intent);
	}
	
	 @Override
	 public void onCreate() {
		 DKLog.d(TAG, "onCreate"); 
		 super.onCreate(); 
	 }
	
	 @Override  
	 public void onStart(Intent intent, int startId) {  
		 DKLog.d(TAG, "onStart");  
		 super.onStart(intent, startId);  
	 }  
	 
	 @Override  
	 public int onStartCommand(Intent intent, int flags, int startId) {  
		 DKLog.d(TAG, "onStartCommand");  
		 return super.onStartCommand(intent, flags, startId);  
	 }  
	 
	@Override
	protected void onHandleIntent(Intent intent) {
		DKLog.d(TAG, "onHandleIntent");
		if(intent != null){
			Bundle bundle = intent.getExtras();
			String statistics = bundle.getString(StatisticTagDef.STATISTIC_INFO);
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(statistics);
				DeviceInfo deviceInfo = DKApp.getSingleton(DeviceInfo.class);
				if(deviceInfo != null){
					jsonObject.put(StatisticTagDef.IME_TAG, deviceInfo.getImeiMd5());
				}
				jsonObject.put(StatisticTagDef.APP_VERSION_TAG, appVersion);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if( jsonObject != null){
				upload_info =  jsonObject.toString();
			}else{
				upload_info = null;
			}
//			DKLog.d(TAG, "upload_info:" + upload_info);
			if( !Util.isEmpty(upload_info)) {
	        	DKApi.setPlayInfo(upload_info);
	        }
		}
	}
	
	@Override  
	public void onDestroy() {  
		DKLog.d(TAG, "onDestroy");  
		super.onDestroy();  
	}  
}

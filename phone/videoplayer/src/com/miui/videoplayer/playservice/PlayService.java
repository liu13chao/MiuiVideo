/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   PlayService.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-8-31
 */

package com.miui.videoplayer.playservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.pplive.sdk.OttSDK;

/**
 * @author tianli
 *
 */
public class PlayService extends Service {
	
	public static final String TAG = "PlayService";
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		init();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		System.exit(0);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	private void init(){
		startPPTV();
	}
	
	private void startPPTV(){
		OttSDK.init(this);
		OttSDK.setLibPath("/system/lib/");
		OttSDK.setLogPath(getCacheDir().getAbsolutePath());
		OttSDK.startServer();
	}
//	
//	private void cleanup(){
//	}
	
	public IPlayService.Stub mBinder = new IPlayService.Stub() {
		
		PlayTask mPlayTask = null;
//		PlayEvent mPlayEvent = null;
		
		public void startSession(int source, String vid){
//			if(mPlayEvent != null){
//				mPlayEvent.onRelease();
//			}
//			mPlayEvent = new PlayEvent(source, vid);
			if(mPlayTask != null){
				mPlayTask.closeTask();
				mPlayTask = null;
			}
			mPlayTask = TaskFractory.createTask(source);
			if(mPlayTask != null){
			    mPlayTask.startTask(vid);
			}
		}
		
		public void endSession(){
//			if(mPlayEvent != null){
//				mPlayEvent.onRelease();
//				mPlayEvent = null;
//			}
			if(mPlayTask != null){
				mPlayTask.closeTask();
				mPlayTask = null;
			}
		}
		
		@Override
		public String getPlayUrl(int source, String sdkInfo, String extraInfo)
				throws RemoteException {
			Log.d(TAG, "getPlayUrl.");
		    startSession(source, sdkInfo);
		    if(mPlayTask != null){
		    	return mPlayTask.getPlayUrl();
		    }
			return null;
		}
		
		@Override
		public void closePlay(int source, String extraInfo) throws RemoteException {
			endSession();
		}
		
		@Override
		public void onEvent(int source, String extraInfo, String key, String value) 
				throws RemoteException {
		}

	};

}

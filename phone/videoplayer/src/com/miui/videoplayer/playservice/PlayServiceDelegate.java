/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   PlayServiceDelegate.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-8-31
 */

package com.miui.videoplayer.playservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * @author tianli
 *
 */
public class PlayServiceDelegate {
	
	public final static String TAG = "PlayServiceDelegate";
	
	private Context mContext;
	private IPlayService mBinder;
	
	private Object mBindLock = new Object();
	
	private static PlayServiceDelegate mDelagate;
	
	private PlayServiceDelegate(Context context){
		mContext = context.getApplicationContext();
	}
	
	public static synchronized PlayServiceDelegate getDefault(Context context){
		if(mDelagate == null){
			mDelagate = new PlayServiceDelegate(context.getApplicationContext());
		}
		return mDelagate;
	}
	
	public String getPlayUrl(final int source, final String sdkInfo,
			final String extraInfo){
		checkBindStatus(5000);
		try {
			Object result = mBinder.getPlayUrl(source, sdkInfo, extraInfo);
			if(result instanceof String){
				return (String)result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void closePlay(int source, String extraInfo){
		try{
			checkBindStatus(5000);
			if(mBinder != null){
				mBinder.closePlay(source, extraInfo);
				return;
			}
		}catch (Exception e) {
		}
	}
	
	public void stopService(){
		try{
			mContext.unbindService(mServiceConnection);
			mBinder = null;
		}catch(Exception e){
		}
	}
	
	private void checkBindStatus(int timeout){
		if(mBinder == null){
			// do rebind.
			bind();
			synchronized (mBindLock) {
				try {
					mBindLock.wait(timeout);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void bind(){
		try{
			Intent intent = new Intent(mContext, PlayService.class);
			mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
		}catch(Exception e){
		}
	}
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "onServiceConnected"); 
			mBinder = IPlayService.Stub.asInterface(service);
			synchronized (mBindLock) {
				try {
					mBindLock.notifyAll();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
}

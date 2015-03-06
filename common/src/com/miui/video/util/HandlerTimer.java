/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   HandlerTimer.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-4
 */

package com.miui.video.util;

import android.os.Handler;

/**
 *@author xuanmingliu
 *
 */

public class HandlerTimer extends Handler{
	
	public static final String TAG = HandlerTimer.class.getName();
	
	private long  mTimeInterval;
	private OnTimerCallbackListener  mTimerCallbackListener;
	
	
	public HandlerTimer(){
	}
	
	public HandlerTimer(long timeInterval) {
		this.mTimeInterval = timeInterval;
	}
	
	public void setTimeInterval(long timeInterval) {
		this.mTimeInterval = timeInterval;
	}
	
	public long getTimeInterval() {
		return mTimeInterval;
	}
	
	public static interface OnTimerCallbackListener {
		public void OnTimerCallback();
	}
	
	public void setOnTimerCallbackListener(OnTimerCallbackListener  timerCallbackListener) {
		this.mTimerCallbackListener = timerCallbackListener;
	}
	
	public void startTimer(){
		triggerTimer();
	}
	
	public void startTimer(long timeInterval){
		this.mTimeInterval = timeInterval;
		startTimer();
	}
	
	public void stopTimer(){
		destroyTimer();
	}
	
	private void triggerTimer(){
		 postDelayed(mTimerCallback, mTimeInterval);
	}
	
	private void destroyTimer(){
		removeCallbacks(mTimerCallback);
	}
	
	private Runnable mTimerCallback  = new Runnable(){
		@Override
		public void run() {
			if( null != mTimerCallbackListener) {
				mTimerCallbackListener.OnTimerCallback();
				triggerTimer();
			}		
		}	
	};
}



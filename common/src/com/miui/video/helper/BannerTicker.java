/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  BannerTicker.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-11
 */
package com.miui.video.helper;

import com.miui.video.util.HandlerTimer;
import com.miui.video.util.HandlerTimer.OnTimerCallbackListener;

/**
 * @author tianli
 *
 */
public class BannerTicker implements LifeCycle{
	
	private HandlerTimer mTimer;
	private int mCount;
	
	public OnTimerCallbackListener mTick = new OnTimerCallbackListener(){
		@Override
		public void OnTimerCallback() {
			tick();
		}
	};
	
	public void tick(){
		
	}
	
	public void setCount(int count){
		mCount = count;
	}
	
	public int getCount(){
		return mCount;
	}
	
	public void start(){
		mTimer.startTimer();
	}
	
	public void stop(){
		mTimer.stopTimer();
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onStart() {
		mTimer.startTimer();
	}

	@Override
	public void onStop() {
		mTimer.stopTimer();
	}

	@Override
	public void onDestroy() {
	}
}

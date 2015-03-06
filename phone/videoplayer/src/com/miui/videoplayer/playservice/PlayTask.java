/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   PlayTask.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-9-2
 */

package com.miui.videoplayer.playservice;

/**
 * @author tianli
 *
 */
public abstract class PlayTask {
	
	public static final String KEY_PREPARE = "prepare";
	public static final String KEY_ONPREPARED = "onprepared";
	public static final String KEY_SEEK = "seek";
	public static final String KEY_BUFFERING = "buffering";
	public static final String KEY_BUFFERING_START = "bufferingstart";
	public static final String KEY_BUFFERING_END = "bufferingend";
	public static final String KEY_RESUME = "resume";
	public static final String KEY_PAUSE = "pause";
	public static final String KEY_LOADING_END = "loadingend";
	
	protected String mSdkInfo;
	
	final public void startTask(String sdkInfo){
		mSdkInfo = sdkInfo;
	}
	
	public abstract String getPlayUrl();
	
	final public void onEvent(String key, String value){
		if(KEY_BUFFERING_START.equals(key)){
			onBufferStart();
		}else if(KEY_BUFFERING_END.equals(key)){
			onBufferEnd();
		}else if(KEY_SEEK.equals(key)){
			onSeek();
		}else if(KEY_ONPREPARED.equals(key)){
			onPrepared();
		}else if(KEY_RESUME.equals(key)){
			onResume();
		}else if(KEY_PAUSE.equals(key)){
			onPause();
		}else if(KEY_PREPARE.equals(key)){
			onPrepare();
		}else if(KEY_LOADING_END.equals(key)){
			onLoadingEnd();
		}
	}
	
	public abstract void closeTask();
	
	protected abstract void onPause();

	protected abstract void onSeek();
	
	protected abstract void onBufferStart();
	
	protected abstract void onBufferEnd();

	protected abstract void onPrepare();
	
	protected abstract void onLoadingEnd();
	
	protected abstract void onPrepared();

	protected abstract void onResume();

}

/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  LifeCycle.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-7
 */
package com.miui.video.helper;

/**
 * @author tianli
 *
 */
public interface LifeCycle{
	
	public void onCreate();
	public void onStart();
	public void onStop();
	public void onDestroy();

}

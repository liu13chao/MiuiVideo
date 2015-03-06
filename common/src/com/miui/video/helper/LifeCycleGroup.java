/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  LifeCycleGroup.java  
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
public class LifeCycleGroup extends Group<LifeCycle> implements LifeCycle {

	public LifeCycleGroup() {
	}

	@Override
	public synchronized void onCreate() {
		for(LifeCycle item : mItems){
			item.onCreate();
		}
	}

	@Override
	public synchronized void onStart() {
		for(LifeCycle item : mItems){
			item.onStart();
		}
	}

	@Override
	public synchronized void onStop() {
		for(LifeCycle item : mItems){
			item.onStop();
		}
	}

	@Override
	public synchronized void onDestroy() {
		for(LifeCycle item : mItems){
			item.onDestroy();
		}
	}
}

/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  LifeCycleManager.java  
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
public class LifeCycleManager extends LifeCycleGroup {
	
	private static LifeCycleManager sManager = new LifeCycleManager();
	
	public static LifeCycleManager getInstance(){
		return sManager;
	}
}

/**
 *  Copyright(c) 2013 XiaoMi TV Group
 *    
 *  AppSingleton.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2013-10-26
 */
package com.miui.video.model;

import android.content.Context;

/**
 * @author tianli
 *
 */
public abstract class AppSingleton {

	protected Context mContext;
	
	public AppSingleton(){
	}

	public void init(Context context){
	    mContext = context;
	}
}

/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   SafeHandler.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-4
 */

package com.miui.videoplayer.model;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Looper;

/**
 * @author tianli
 *
 */
public class SafeHandler<T> extends Handler {
	
	private WeakReference<T> mRef = null;
	
	public SafeHandler(T ref){
		mRef = new WeakReference<T>(ref);
	}
	
	public SafeHandler(T ref, Looper looper){
	    super(looper);
        mRef = new WeakReference<T>(ref);
    }
	
	public T getReference(){
		WeakReference<T> ref = mRef;
		if(ref != null){
			return ref.get();
		}
		return null;
	}

}

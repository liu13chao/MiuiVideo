/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   OnTouchInterceptor.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-20 
 */
package com.miui.video.helper;

import android.view.MotionEvent;

/**
 * @author tianli
 * 
 */
public interface OnTouchInterceptor {
	public static int SCROLL_LEFT = 0;
	public static int SCROLL_RIGHT = 1;
	public static int SCROLL_UP = 2;
	public static int SCROLL_DOWN = 3;

	public boolean onIntercept(int scrollDirection, MotionEvent event);

	public void onPreIntercept(MotionEvent event);
}

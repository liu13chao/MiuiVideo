/**
 *   Copyright(c) 2012 DuoKan TV Group
 *   
 *   AlertMessage.java
 *  
 *   @author tianli(tianli@duokan.com)
 * 
 *   @date 2012-8-3 
 */
package com.miui.video.util;

import com.miui.video.DKApp;

import android.content.Context;
import android.widget.Toast;

/**
 * @author tianli
 *
 */
public class AlertMessage {
	
	private static Toast curToast = null;
	
	public static void show(String message) {
		show(DKApp.getAppContext(), message, false);
	}
	
	public static void show(Context context, String message) {
		show(context, message, false);
	}
	
	public static void show(Context context, String message, boolean isLong) {
		if( curToast != null)
			curToast.cancel();
		
		try {
			if (Util.isEmpty(message)) {
				return;
			}
			curToast = Toast.makeText(context, message, isLong ? 
						Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
			curToast.show();
		} catch (Exception e) {
		}
	}
	
	public static void show(int resId) {
		show(DKApp.getAppContext(), resId, false);
	}
	
	public static void show(Context context, int resId) {
		show(context, resId, false);
	}
	
	public static void show(Context context, int resId, boolean isLong) {
		show(context, context.getString(resId), isLong);
	}
}

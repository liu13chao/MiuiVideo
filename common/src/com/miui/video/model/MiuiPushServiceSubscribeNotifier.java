/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   MiuiPushServiceNotifier.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-11-7
 */

package com.miui.video.model;

import android.content.Context;
import android.content.Intent;

/**
 *@author xuanmingliu
 *
 */

public class MiuiPushServiceSubscribeNotifier {
	
	private static final String ACTION_SUBSCRIBE_MIUIPUSHSERVICE = "com.xiaomi.micloudpush.SUBSCRIBE";
	
	public static void NofityMiuiPushServiceSubscribed(Context context) {
		
		Intent intent = new Intent(ACTION_SUBSCRIBE_MIUIPUSHSERVICE);
		context.sendBroadcast(intent);
	}
}

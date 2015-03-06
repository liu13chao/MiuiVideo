package com.miui.videoplayer.common;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;

public class DuoKanUtils {
	public static final String TAG = "DuoKanUtils";
	
	public static boolean isValidFormatVideo(Uri uri) {
		if (uri == null) {
			return false;
		}
		if (!DuoKanConstants.IS_MAINLAND_CHINA_VERSION) {
			return true;
		}

		return true;
	}
	
	public static boolean isMilink(Context context) {
		if (context == null || context.getPackageManager() == null) {
			return false;
		}
		try {
			if ((context.getPackageManager().getPackageInfo(
					"com.milink.service", PackageManager.PERMISSION_GRANTED)) != null) {
				Log.e(TAG, "is milink");
				return true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Log.e(TAG, "is airkan");
		return false;
	}
	
}

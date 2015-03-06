package com.miui.videoplayer.framework.utils;

import com.miui.videoplayer.framework.DuoKanConstants;

import android.net.Uri;

public class DuoKanUtils {
	
	public static boolean isValidFormatVideo(Uri uri) {
		if (uri == null) {
			return false;
		}
		if (!DuoKanConstants.IS_MAINLAND_CHINA_VERSION) {
			return true;
		}

		return true;
	}
	
}

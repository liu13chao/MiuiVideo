package com.miui.video.util;

import android.util.Log;

public final class DKLog {

	private static boolean sIsEnabled = true;

	private DKLog() {
	}

	public static boolean isEnabled() {
		return sIsEnabled;
	}

	public static void setEnable(boolean enabled) {
		sIsEnabled = enabled;
	}

	public static int v(String tag, String msg) {
		if (sIsEnabled && !Util.isEmpty(msg)) {
			return Log.v(tag, msg);
		} else {
			return 0;
		}
	}

	public static int v(String tag, String msg, Throwable tr) {
		if (sIsEnabled && !Util.isEmpty(msg)) {
			return Log.v(tag, msg, tr);
		} else {
			return 0;
		}
	}

	public static int d(String tag, String msg) {
		if (sIsEnabled && !Util.isEmpty(msg)) {
			return Log.d(tag, msg);
		} else {
			return 0;
		}
	}

	public static int d(String tag, String msg, Throwable tr) {
		if (sIsEnabled && !Util.isEmpty(msg)) {
			return Log.d(tag, msg, tr);
		} else {
			return 0;
		}
	}

	public static int i(String tag, String msg) {
		if (sIsEnabled && !Util.isEmpty(msg)) {
			return Log.i(tag, msg);
		} else {
			return 0;
		}
	}

	public static int i(String tag, String msg, Throwable tr) {
		if (sIsEnabled && !Util.isEmpty(msg)) {
			return Log.i(tag, msg, tr);
		} else {
			return 0;
		}
	}

	public static int w(String tag, String msg) {
		if (sIsEnabled && !Util.isEmpty(msg)) {
			return Log.w(tag, msg);
		} else {
			return 0;
		}

	}

	public static int w(String tag, String msg, Throwable tr) {
		if (sIsEnabled && !Util.isEmpty(msg)) {
			return Log.w(tag, msg, tr);
		} else {
			return 0;
		}
	}

	public static int w(String tag, Throwable tr) {
		if (sIsEnabled) {
			return Log.w(tag, tr);
		} else {
			return 0;
		}
	}

	public static int e(String tag, String msg) {
		if(!Util.isEmpty(msg)) {
			return Log.e(tag, msg);
		} else {
			return 0;
		}
	}

	public static int e(String tag, String msg, Throwable tr) {
		if(!Util.isEmpty(msg)) {
			return Log.e(tag, msg, tr);
		} else {
			return 0;
		}
	}
}

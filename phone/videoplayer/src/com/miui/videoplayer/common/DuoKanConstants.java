package com.miui.videoplayer.common;

import miui.os.Build;
import android.util.Log;

public class DuoKanConstants {
	public static final String TAG = "DuoKanConstants";
	
//	public static final String AIRKAN_DEVICE_XIAOMI_PHONE = "airkan_device_xiaomi_phone";
	
//	public static final String INTENT_KEY_STRING_URI = "uri";
//	public static final String INTENT_KEY_BOOLEAN_DIRECT_AIRKAN = "direct_airkan";
//	public static final String INTENT_KEY_STRING_DEVICE_NAME = "device_name";
//	public static final String INTENT_KEY_STRING_ARRAY_URI_LIST = "uri_list";
//	public static final String INTENT_KEY_INT_PLAY_INDEX = "play_index";
//	public static final String INTENT_KEY_STRING_MEDIA_TITLE = "mediaTitle"; 
	
	public static final int BRIGHTNESS_MAX_VALUE = 15;
	public static final int BRIGHTNESS_STEP = 255 / BRIGHTNESS_MAX_VALUE;
	
	public static final String SHARED_PEREFERENCE_KEY_LAST_BRIGHTNESS_AUTO = "shared_pereference_key_last_brightness_auto";
	public static final String SHARED_PEREFERENCE_KEY_INITIAL = "shared_pereference_key_last_brightness_initial";
	public static final String SHARED_PEREFERENCE_KEY_LAST_BRIGHTNESS = "shared_pereference_key_last_brightness";
//	public static final String SHARED_PEREFERENCE_KEY_ORIENTATION_SENSOR = "shared_pereference_key_orientation_sensor";
	
	public static final String SHARED_PEREFERENCE_KEY_OFFLINE_USER_STTING = "shared_pereference_key_offline_user_setting";
	
	public static final String SHARED_PEREFERENCE_KEY_AIREKAN_USER_USED = "shared_pereference_key_airekan_user_uesed";
	
	public static final String SHARED_PEREFERENCE_KEY_NETWORK_ALERT = "shared_pereference_key_network_alert";
	
	public static final boolean ENABLE_AIRKAN = true;
	public static final boolean ENABLE_V5_UI = true;
	
	public static final boolean FOR_MI_APPSTORE = false;
	
	public static final String PATH_DATA_LIB = "/data/data/com.miui.video/lib/";
	public static final String PATH_DATA_LIBS = "/data/data/com.miui.video/libs/";
	public static final String PATH_DATA = "/data/data/com.miui.video/";
	
	public static final boolean IS_TW_VERSION;
	public static final boolean IS_HK_VERSION;
	public static final boolean IS_INTERNATIONAL_VERSION;
	public static final boolean IS_MAINLAND_CHINA_VERSION;
	
	public static final boolean IS_CM_CUSTOMIZATION;

	// X1
	public static final boolean IS_X1;
	// X2
	public static final boolean IS_X2;
	public static final boolean IS_X2A;
	// X3
	public static final boolean IS_X3_TD;
	public static final boolean IS_X3_WC;
	public static final boolean IS_X3;
	// X4
	public static final boolean IS_X4_LTE;

	// HM
	// HM1
	public static final boolean IS_H2_WC;
	// HM1TD
	public static final boolean IS_H2_TD;
	public static final boolean IS_H2;
	public static final boolean IS_H2A;
	public static final boolean IS_H2S;
	// HMNote1
	public static final boolean IS_H3;
	// HMNote1W
	public static final boolean IS_H3_WC;
	// HMNote1TD
	public static final boolean IS_H3_TD;
	// HMNoteLte
	public static final boolean IS_H3_LTE;
	public static final boolean IS_HONGMI;

	// pad
	public static final boolean IS_P1;
	
	static {
		IS_TW_VERSION = isTWVersion();
		IS_HK_VERSION = isHKVersion();
		IS_INTERNATIONAL_VERSION = isInternationalVersion();
		IS_MAINLAND_CHINA_VERSION = isMainlandChinaVersion();
		IS_CM_CUSTOMIZATION = isCMCustomization();
		
		IS_X1 = isX1();
		IS_X2 = isX2();
		IS_X2A = isX2A();
		IS_X3 = isX3();
		IS_X3_TD = isX3TD();
		IS_X3_WC = isX3WC();
		IS_X4_LTE = isX4LTE();
		IS_H2_TD = isH2TD();
		IS_H2_WC = isH2WC();
		IS_H2A = isH2A();
		IS_H2S = isH2S();
		IS_H2 = isH2();
		IS_H3_WC = isH3WC();
		IS_H3_TD = isH3TD();
		IS_H3_LTE = isH3LTE();
		IS_H3 = isH3();
		IS_HONGMI = isHongmi();
		IS_P1 = isP1();
	}
	
	private static boolean isTWVersion() {
		try {
			return Build.IS_TW_BUILD;
		} catch (Throwable t) {
			Log.e(TAG, "IS_TW_BUILD not exists in miui.os.Build");
		}
		return false;
	}
	
	private static boolean isHKVersion() {
		try {
			return Build.IS_HK_BUILD;
		} catch (Throwable t) {
			Log.e(TAG, "IS_HK_BUILD not exists in miui.os.Build");
		}
		return false;
	}
	
	private static boolean isInternationalVersion() {
		try {
			return Build.IS_INTERNATIONAL_BUILD;
		} catch (Throwable t) {
			Log.e(TAG, "IS_INTERNATIONAL_BUILD not exists in miui.os.Build");
		}
		return false;
	}
	
	private static boolean isMainlandChinaVersion() {
		return !isTWVersion() && !isHKVersion() && !isInternationalVersion();
	}
	
	private static boolean isCMCustomization() {
		boolean is = false;
		try {
			is = Build.IS_CM_CUSTOMIZATION;
		} catch (Throwable t) {
			Log.e(TAG, "IS_CM_CUSTOMIZATION not exists in miui.os.Build");
		}
		Log.i(TAG, "cm custom: " + is);
		return is;
	}
	
	private static boolean isX1() {
		boolean is = false;
		try {
			is = Build.IS_MIONE || Build.IS_MI1S;
		} catch (Throwable t) {
			Log.e(TAG, "IS_MIONE or IS_MI1S not exists in miui.os.Build");
			is =  "mione".equals(Build.DEVICE) || "mione_plus".equals(Build.DEVICE);
		}
		Log.i(TAG, "x1: " + is);
		return is;
	}
	
	private static boolean isX2() {
		boolean is = false;
		try {
			is = Build.IS_MITWO;
		} catch (Throwable t) {
			Log.e(TAG, "IS_MITWO not exists in miui.os.Build");
			is =  "aries".equals(Build.DEVICE);
		}
		Log.i(TAG, "x2: " + is);
		return is;
	}
	
	private static boolean isX2A() {
		boolean is = false;
		try {
			is = Build.IS_MI2A;
		} catch (Throwable t) {
			Log.e(TAG, "IS_MI2A not exists in miui.os.Build");
			is =  "taurus".equals(Build.DEVICE);
		}
		Log.i(TAG, "x2a: " + is);
		return is;
	}
	
	private static boolean isX3TD() {
		boolean is = false;
		try {
			is = Build.IS_MITHREE_TDSCDMA;
		} catch (Throwable t) {
			Log.e(TAG, "IS_MITHREE_TDSCDMA not exists in miui.os.Build");
			is =  "pisces".equals(Build.DEVICE);
		}
		Log.i(TAG, "x3td: " + is);
		return is;
	}
	
	private static boolean isX3WC() {
		boolean is = false;
		try {
//			is = Build.IS_MITHREE_WCDMA;
			is = true;
		} catch (Throwable t) {
			Log.e(TAG, "IS_MITHREE_WCDMA not exists in miui.os.Build");
			is =  "cancro".equals(Build.DEVICE);
		}
		Log.i(TAG, "x3wc: " + is);
		return is;
	}
	
	private static boolean isX3() {
		boolean is = false;
		try {
			is = Build.IS_MITHREE;
		} catch (Throwable t) {
			Log.e(TAG, "IS_MITHREE not exists in miui.os.Build");
			is =  isX3TD() || isX3WC();
		}
		Log.i(TAG, "x3: " + is);
		return is;
	}
	
	private static boolean isX4LTE() {
		return "cancro".equals(Build.DEVICE);
	}
	
	private static boolean isHongmi() {
		boolean is = false;
		try {
			is = Build.IS_HONGMI;
		} catch (Exception e) {
			Log.e(TAG, "IS_HONGMI not exists in miui.os.Build");
		}
		Log.i(TAG, "hongmi: " + is);
		return is;
	}
	
	private static boolean isH2() {
		boolean is = false;
		try {
			is = Build.IS_HONGMI_TWO;
		} catch (Throwable t) {
			Log.e(TAG, "IS_HONGMI_TWO not exists in miui.os.Build");
			is = isH2WC() || isH2TD() || isH2A() || isH2S();
		}
		Log.i(TAG, "h2: " + is);
		return is;
	}
	
	private static boolean isH2WC() {
		boolean is = "HM2013023".equals(Build.DEVICE) || "wt98007".equals(Build.DEVICE);
		Log.i(TAG, "h2wc: " + is);
		return is;
	}
	
	private static boolean isH2TD() {
		boolean is = "HM2013022".equals(Build.DEVICE) || "wt93007".equals(Build.DEVICE);
		Log.i(TAG, "h2td: " + is);
		return is;
	}
	
	private static boolean isH2A() {
		boolean is = false;
		try {
			is = Build.IS_HONGMI_TWO_A;
		} catch (Throwable t) {
			Log.e(TAG, "IS_HONGMI_TWO_A not exists in miui.os.Build");
			is =  "armani".equals(Build.DEVICE);
		}
		Log.i(TAG, "h2a: " + is);
		return is;
	}
	
	private static boolean isH2S() {
		boolean is = false;
		try {
//			is = Build.IS_HONGMI_TWO_S;
			is = true;
		} catch (Throwable t) {
			Log.e(TAG, "IS_HONGMI_TWO_S not exists in miui.os.Build");
			is =  "HM2014011".equals(Build.DEVICE) || "HM2014012".equals(Build.DEVICE) || "HM2014501".equals(Build.DEVICE);
		}
		Log.i(TAG, "h2s: " + is);
		return is;
	}
	
	private static boolean isH3WC() {
		boolean is = "lcsh92_wet_jb9".equals(Build.DEVICE) || "W8500".equals(Build.DEVICE);
		Log.i(TAG, "h3wc: " + is);
		return is;
	}
	
	private static boolean isH3TD() {
		boolean is = "lcsh92_wet_tdd".equals(Build.DEVICE) || "T8850".equals(Build.DEVICE);
		Log.i(TAG, "h3td: " + is);
		return is;
	}

	private static boolean isH3LTE() {
		boolean is = false;
		try {
//			is = Build.IS_HONGMI_THREE_LTE;
			is = true;
		} catch (Throwable t) {
			Log.e(TAG, "IS_HONGMI_THREE_LTE not exists in miui.os.Build");
			is = "dior".equals(Build.DEVICE);
		}
		Log.i(TAG, "h3lte: " + is);
		return is;
	}

	private static boolean isH3() {
		boolean is = false;
		try {
//			is = Build.IS_HONGMI_THREE;
			is = true;
		} catch (Throwable t) {
			Log.e(TAG, "IS_HONGMI_THREE not exists in miui.os.Build");
			is = isH3WC() || isH3TD();
		}
		Log.i(TAG, "h3: " + is);
		return is;
	}
	
	private static boolean isP1() {
		boolean is = "mocha".equals(Build.DEVICE);
		Log.i(TAG, "p1: " + is);
		return is;
	}
}

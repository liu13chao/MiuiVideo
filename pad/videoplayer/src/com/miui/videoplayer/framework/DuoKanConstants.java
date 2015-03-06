package com.miui.videoplayer.framework;


public interface DuoKanConstants {
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
	public static final String SHARED_PEREFERENCE_KEY_ORIENTATION_SENSOR = "shared_pereference_key_orientation_sensor";
	
	public static final String SHARED_PEREFERENCE_KEY_OFFLINE_USER_STTING = "shared_pereference_key_offline_user_setting";
	
	public static final String SHARED_PEREFERENCE_KEY_AIREKAN_USER_USED = "shared_pereference_key_airekan_user_uesed";
	
	public static final boolean ENABLE_AIRKAN = true;
	public static final boolean ENABLE_V5_UI = true;
	
	public static final boolean IS_TW_VERSION = miui.os.Build.IS_TW_BUILD;
	public static final boolean IS_HK_VERSION = miui.os.Build.IS_HK_BUILD;
	public static final boolean IS_INTERNATIONAL_VERSION = miui.os.Build.IS_INTERNATIONAL_BUILD;
	public static final boolean IS_MAINLAND_CHINA_VERSION = !IS_TW_VERSION && !IS_HK_VERSION && !IS_INTERNATIONAL_VERSION;
}

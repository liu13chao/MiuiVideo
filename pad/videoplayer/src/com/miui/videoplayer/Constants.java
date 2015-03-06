package com.miui.videoplayer;

import android.net.Uri;

public interface Constants {
//	public static final String INTENT_KEY_STRING_URI = "uri";
	public static final String INTENT_KEY_BOOLEAN_DIRECT_AIRKAN = "direct_airkan";
	public static final String INTENT_KEY_STRING_DEVICE_NAME = "device_name";
	public static final String INTENT_KEY_STRING_ARRAY_URI_LIST = "uri_list";
	public static final String INTENT_KEY_INT_PLAY_INDEX = "play_index";
	public static final String INTENT_KEY_STRING_MEDIA_TITLE = "mediaTitle";

	public static final String MEDIA_ID = "media_id";
	public static final String CURRENT_EPISODE = "current_episode";
	public static final String AVAILABLE_EPISODE_COUNT = "available_episode_count";
	public static final String MEDIA_CLARITY = "media_clarity";
	public static final String MEDIA_SOURCE = "media_source";
	public static final String MEDIA_URL = "media_url";
	public static final String MEDIA_HTML5_URL = "media_h5_url";
	public static final String MEDIA_SUBTITLE = "subtitle";	
	public static final String MEDIA_SET_STYLE = "media_set_style";
	
	public static final String MEDIA_NAME = "media_name";	
	public static final String MEDIA_DATE = "media_date";
	
	public static final String OFFLINE_OPERATION = "offline_operation";
	public static final String OFFLINE_STATUS = "offline_status";
	public static final String OFFLINE_SOURCE = "offline_source";
	//
	public final static int OFFLINE_NONE = -1;
	public final static int OFFLINE_STATE_IDLE = 0;
	public final static int OFFLINE_STATE_FINISH = 1;
	public final static int OFFLINE_STATE_PAUSE = 2;
	public final static int OFFLINE_STATE_INIT = 3;
	public final static int OFFLINE_STATE_LOADING = 4;
	public final static int OFFLINE_STATE_CONNECT_ERROR = 5;
	public final static int OFFLINE_STATE_FILE_ERROR = 6;
	public final static int OFFLINE_STATE_SOURCE_ERROR = 7;
	
	public final static int OFFLINE_OPERATION_ADD = 0;
	public final static int OFFLINE_OPERATION_START = 1;
	public final static int OFFLINE_OPERATION_PAUSE = 2;
	public final static int OFFLINE_OPERATION_DELETE = 3;
	
	
	//
	public static final int MEDIA_TYPE_TELEPLAY = 0; // teleplay
	public static final int MEDIA_TYPE_VARIETY = 1; //  varierty
	
	public static final String OFFLINE_BROADCAST_ACTION = "com.miui.video.ReceiveOfflineOperationReceiver";
	
	public static final String AUTHORITY = "com.miui.video.provider.MediaInfoForPlayerProvider";
	public static final String MEDIAINFO_TABLE_NAME = "media_url_table";
	public static final Uri CONTENT_MEDIAURL_URI = Uri.parse("content://" +AUTHORITY +"/" +MEDIAINFO_TABLE_NAME);
	public static final Uri CONTENT_MEDIAINFO_URI = Uri.parse("content://" +AUTHORITY +"/" +MEDIAINFO_TABLE_NAME + "/all");
	
	
}

package com.miui.videoplayer.common;

import android.net.Uri;

public interface Constants {
//	public static final String INTENT_KEY_STRING_URI = "uri";
	public static final String INTENT_KEY_BOOLEAN_DIRECT_AIRKAN = "direct_airkan";
	public static final String INTENT_KEY_STRING_DEVICE_NAME = "device_name";
	public static final String INTENT_KEY_STRING_ARRAY_URI_LIST = "uri_list";
	public static final String INTENT_KEY_INT_PLAY_INDEX = "play_index";
	public static final String INTENT_KEY_STRING_MEDIA_TITLE = "mediaTitle";
	public static final String INTENT_KEY_STRING_MEDIA_SDKINFO = "sdkinfo";
	public static final String INTENT_KEY_STRING_MEDIA_SDKDISABLE = "sdkdisable";
    public static final String INTENT_KEY_BOOLEAN_SCREENSAVER = "screensaver";

    public static final String STATISTIC_BUFFERTIME = "bufferTime";
    public static final String STATISTIC_DATE = "date";
    public static final String STATISTIC_PLAYINGTIME = "playingTime";
    public static final String STATISTIC_ADDURATION = "adduration";
    public static final String STATISTIC_ADPLAYDURATION = "adplayduration";
    public static final String STATISTIC_CONTENT = "content";
    public static final String STATISTIC_DATEPLAYINFO = "dateplayinfo";
    public static final String STATISTIC_MEDIAID = "mediaid";
    public static final String STATISTIC_MEDIACI = "mediaci";
    public static final String STATISTIC_MEDIAURL = "mediaurl";
    public static final String STATISTIC_PLAYINFOS = "playinfos";
    public static final String STATISTIC_All = "statistic";
    
    public static final String STATISTIC_SUB_STARTTIME = "starttime";
    public static final String STATISTIC_SUB_ENDTIME = "endtime";
    public static final String STATISTIC_SUB_BACKGROUNDTIME = "backgroundtime";
    public static final String STATISTIC_SUB_BUFFERTIMES = "buffertimes";
    public static final String STATISTIC_SUB_BUFFERDURATION = "bufferduration";
    public static final String STATISTIC_SUB_ADTIME = "adtime";
    public static final String STATISTIC_SUB_LOADINGTIMES = "loadingtimes";
    public static final String STATISTIC_SUB_PAUSEDURATION = "pauseduration";
    public static final String STATISTIC_SUB_PAUSETIMES = "pausetimes";
    public static final String STATISTIC_SUB_PLAYTIMEPOINT = "playtimepoint";    
    
	public static final String MEDIA_ID = "media_id";
	public static final String CURRENT_EPISODE = "current_episode";
	public static final String AVAILABLE_EPISODE_COUNT = "available_episode_count";
	public static final String MEDIA_CLARITY = "media_clarity";
	public static final String MEDIA_SOURCE = "media_source";
	public static final String MEDIA_URL = "media_url";
	public static final String MEDIA_HTML5_URL = "media_h5_url";
	public static final String MEDIA_POSTER_URL = "media_poster_url";
	public static final String MEDIA_SUBTITLE = "subtitle";	
	public static final String MEDIA_TYPE = "media_type";
	public static final String MULTI_SET = "multi_set";
	public static final String VIDEO_TYPE = "video_type";
	public static final String MEDIA_PLAY_URL = "media_play_url";
	public static final String MEDIA_SET_STYLE = "media_set_style";
	public static final String MEDIA_SET_NAME = "media_set_name";
	public static final String MEDIA_NAME = "media_name";	
	public static final String MEDIA_DATE = "media_date";
	
	public static final String OFFLINE_OPERATION = "offline_operation";
	public static final String OFFLINE_STATUS = "offline_status";
	public static final String OFFLINE_SOURCE = "offline_source";

	public static final String XVX_PEER_ID = "peer_id";
	public static final String XVX_CERT_FILE = "cert_file";
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
	
	public static final String MEDIAID_TAG = "mediaid";
	public static final String MEDIACI_TAG = "mediaci";
	public static final String MEDIAURL_TAG = "mediaurl";
	public static final String DATEPLAYINFO_TAG = "dateplayinfo";
	public static final String PLAYINFOS_TAG = "playinfos";
	
	public static final String STATISTIC_INFO = "statisticinfo";
	
	//
	public static final int MEDIA_TYPE_SERIES = 0; // teleplay
	public static final int MEDIA_TYPE_VARIETY = 1; //  varierty
	
	public static final int MEDIA_TYPE_LONG = 0; // 
	public static final int MEDIA_TYPE_SHORT = 1; //  
	public static final String OFFLINE_BROADCAST_ACTION = "com.miui.video.ReceiveOfflineOperationReceiver";
	
	public static final String AUTHORITY = "com.miui.video.provider.MediaInfoForPlayerProvider";
	public static final String MEDIAINFO_TABLE_NAME = "media_url_table";
	public static final String MEDIA_URL_INFO = "media_url_info";
	public static final String LOCAL_PATH = "local_path";
	
	public static final Uri CONTENT_MEDIAURL_URI = Uri.parse("content://" +AUTHORITY +"/" +MEDIAINFO_TABLE_NAME);
	public static final Uri CONTENT_MEDIAINFO_URI = Uri.parse("content://" +AUTHORITY +"/" +MEDIAINFO_TABLE_NAME + "/all");
	
	public static final Uri CONTENT_MEDIA_URL_INFO = Uri.parse("content://" +AUTHORITY +"/" + MEDIA_URL_INFO);	
	
	public static final Uri CONTENT_MEDIA_PLAY_URL = Uri.parse("content://" +AUTHORITY +"/" + MEDIA_PLAY_URL);	
	
    public static final String INTENT_KEY_ID = "id";
    public static final String INTENT_KEY_VID = "vid";
    public static final String INTENT_KEY_SID = "sid";
    public static final String INTENT_KEY_TVID = "tvid";
    public static final String INTENT_KEY_URI = "uri";
    public static final String INTENT_KEY_APPKEY = "appkey";
    public static final String INTENT_KEY_TASKID = "taskId";
    public static final String INTENT_KEY_TITLE = "title";
    public static final String INTENT_KEY_KEYWORD = "keyword";
    public static final String INTENT_KEY_URI_INDEX = "uri_index";
    public static final String INTENT_KEY_VIDEO_LIST = "video_list";
    public static final String INTENT_KEY_STARTPOSITION = "start_position";
    public static final String TEST_APPKEY = "test_asfdfljw;ekjr!wklvlk";
    public static final String SOHU_APPKEY = "xiaomi_##)!bc=#s$d-(--009";
    public static final String TEST_PARTNER = "000";
}

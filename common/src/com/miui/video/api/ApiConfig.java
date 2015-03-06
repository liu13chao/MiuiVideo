/**
 *   Copyright(c) 2013 XiaoMi TV Group
 *    
 *   ApiConfig.java
 *
 *   @author tianli(tianli@xiaomi.com)
 *
 *   2013-7-20 
 */
package com.miui.video.api;

/**
 * @author tianli
 *
 */
public class ApiConfig {
	
//	public static final String SERVER_URL = "mobile.duokanbox.com";
//	public static final String SERVER_URL = "preview.mobile.duokanbox.com";
//	public static final String SERVER_URL = "172.27.9.105";
	public static final String SERVER_URL = "mobile.duokanbox.com";
//    public static final String SERVER_URL = "172.27.17.63:8888";
//    public static final String SERVER_URL = "mobile.n.duokanbox.com";
	
	public static final String PARAM_DEVICE_TYPE = "devicetype";
	public static final String PARAM_PLATFORM = "ptf";
	public static final String PARAM_VER = "ver";
	public static final String PARAM_MIUI_VER = "miuiver";
	public static final String PARAM_DEVICEID = "deviceid";
	public static final String PARAM_APIVER = "apiver";
	public static final String PARAM_TIMESTAMP = "ts";
	public static final String PARAM_NONCE = "nonce";
	public static final String PARAM_SIGNATURE = "signature";
	public static final String PARAM_USERID = "userid";
	
	public static final String PARAM_OPAQUE = "opaque";
	public static final String PARAM_TOKEN = "token";
	public static final String PARAM_API_KEY = "key";
	
    public static final String API_KEY = "581582928c881b42eedce96331bff5d3";
    public static final String API_TOKEN = "0f9dfa001cba164d7bda671649c50abf";
    
    public static final String API_ACTIVE_TOKEN = "7a3689fa91bc4693a658db0d08aa780f";
    public static final String API_ACTIVE_KEY = "a2f571c79d0c4867992ab53cafa7e623";
    
	public static final int PLATFORM_ANDROID_LOW_RESOLUTION = 198;
	public static final int PLATFORM_ANDROID_HIGH_RESOLUTION = 199;
	public static final int PLATFORM_ANDROID_SUPER_RESOLUTION = 196;
	
	public static final int PLATFORM_MI_ONE = 200;
	public static final int PLATFORM_MI_TWO = 201;
	public static final int PLATFORM_MI_THREE = 197;
	public static final int PLATFORM_MI_RED_TWO = 195;
	
	public static final int PLATFORM_PAD_N7 = 500;
	
	/*television background color*/
	public static final int COLOR_TRANSPARENT = 0;
	public static final int COLOR_ORANGE = 1;
	public static final int COLOR_RED = 2;
	public static final int COLOR_GREEN = 3;
	public static final int COLOR_BLUE = 4;
	public static final int COLOR_YELLOW = 5;
	
	/*mediaType*/
	public static final int ID_TYPE_MEDIA =	0;
	public static final int ID_TYPE_PERSON = 1;
	public static final int ID_TYPE_ACTOR_DIRECTOR = 2;
	public static final int ID_TYPE_SPECIALSUBJECT = 100;
	public static final int ID_TYPE_TV = 200;
	public static final int ID_TYPE_ADDON = 300;
	
//	// vc
//	public static final int VIDEO_CAPABILITY_MP4 = 0x00000001;
//	public static final int VIDEO_CAPABILITY_M3U8 = 0x00000002;
//	public static final int VIDEO_CAPABILITY_TS = 0x00000004;
//	public static final int VIDEO_CAPABILITY_ALL = 0x00000007;
//
//	// ac
//	public static final int AUDIO_CAPABILITY_MP3 = 0x00000001;
//	public static final int AUDIO_CAPABILITY_WMA = 0x00000002;
//	public static final int AUDIO_CAPABILITY_WAV = 0x00000004;
//	public static final int AUDIO_CAPABILITY_ALL = 0x00000007;
//
//	// ic
//	public static final int IMAGE_CAPABILITY_BMP = 0x00000001;
//	public static final int IMAGE_CAPABILITY_JPG = 0x00000002;
//	public static final int IMAGE_CAPABILITY_GIF = 0x00000004;
//	public static final int IMAGE_CAPABILITY_ALL = 0x00000007;
	
	// televison entry
	public static final int ENTRY_BANNER = 0;
	public static final int ENTRY_HOME = 1;
	public static final int ENTRY_HOT = 2;
	public static final int ENTRY_ALL = 3;
	public static final int ENTRY_RECENT_PLAY = 4;
	public static final int ENTRY_FAVORATE = 5;
	public static final int ENTRY_CHOICE = 6;
	
	// ordertype of GetMediaInfoList
	public static final int ORDER_BY_UPDATETIME = 0;
	public static final int ORDER_BY_HOT = 1;
	public static final int ORDER_BY_ISSUEDATE = 2;
	public static final int ORDER_BY_UPDATETIME_ASC = 3;
	public static final int ORDER_BY_HOT_ASC = 4;
	public static final int ORDER_BY_ISSUEDATE_ASC = 5;
	public static final int ORDER_BY_SCORE_DESC = 6;
	public static final int ORDER_BY_SCORE_ASC = 7;

	// listtype of GetMediaInfoList
	public static final int MEDIA_LIST_TYPE_LIST = 0; // actors and director
	public static final int MEDIA_LIST_TYPE_ICON = 1; // no actors and director

	// postertype of GetMediaInfoList
	public static final int POSTER_TYPE_BIG = 0;
	public static final int POSTER_TYPE_SMALL = 1;
	
	//nSearchMask 参数
	public static final int SEARCH_MASK_ALL	= 0;			// 全部
	public static final int	SEARCH_MASK_NAME = 1;			// 按名字搜索
	public static final int SEARCH_MASK_DIRECTOR = 2;			// 按导演搜索
	public static final int SEARCH_MASK_ACTOR = 4;			  // 按演员/歌手/主持搜索
	public static final int SEARCH_MASK_MOVIE = 1024;		  // 在电影中搜索
	public static final int SEARCH_MASK_TV = 2048;		      // 在电视剧中搜索
	public static final int SEARCH_MASK_CARTOON = 4096;		  // 在动漫中搜索
	public static final int SEARCH_MASK_SYNTHESIS = 8192;		// 在综艺中搜索
	public static final int SEARCH_MASK_DOCUMENTARY = 16384;		// 在纪录片中搜索
	public static final int SEARCH_MASK_MUSIC_VIDEO = 32768;		// 在音乐中搜索
	public static final int SEARCH_MASK_EDUCATION = 65536;		// 在教育中搜索

	
	// mediaNameSearchType
	public static final int SEARCH_MEDIA_BY_PY = 1; // 按名字搜索除 MV 外的视频
	public static final int SEARCH_MV_BY_PY = 2; // 按名字搜索 MV
	public static final int SEARCH_CINEASTE_BY_PY = 3; // 按人名搜索除 MV 外的影人
	public static final int SEARCH_SINGER_BY_PY = 4; // 按歌手名搜索 MV影人

	public static final int SEARCH_MEDIA_BY_FUZZY_PY = 101; // 按T9键盘名字搜索除 MV
															// 外的视频
	public static final int SEARCH_MV_BY_FUZZY_PY = 102; // 按T9键盘名字搜索 MV
	public static final int SEARCH_CINEASTE_BY_FUZZY_PY = 103; // 按T9键盘人名搜索除 MV
																// 外的影人
	public static final int SEARCH_SINGER_BY_FUZZY_PY = 104; // 按T9键盘歌手名搜索 MV影人

	public static final int SEARCH_MEDIA_BY_KEYWORD = 1001; // 按关键字搜索除 MV 外的视频
	public static final int SEARCH_MV_BY_KEYWORD = 1002; // 按关键字搜索 MV
	public static final int SEARCH_CINEASTE_BY_KEYWORD = 1003; // 按关键字搜索除 MV
																// 外的影人
	public static final int SEARCH_SINGER_BY_KEYWORD = 1004; // 按关键字搜索 MV 影人

	public static final int SEARCH_MOBILE_BY_KEYWORD = 1101; // 搜索热门关键字
	public static final int SEARCH_CHANNEL_SUMMARY_BY_KEYWORD = 1102; // 搜索结果按分类给出
	
	
	public static final int STATUS_SUCCESS = 0;

	public static final int STATUS_SYNC_TS = 105;
	
	public static final int STATUS_NETWORK_ERROR = 10000;
	public static final int STATUS_SERVER_ERROR = 10001;
	public static final int STATUS_UNKOWN_ERROR = 10002;


}

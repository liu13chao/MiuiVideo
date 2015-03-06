package com.miui.video.db;

import android.net.Uri;

public class DBUtil {
	//media url for player db
	public static final String MEDIAINFO_DB_NAME = "media_url_db";
	public static final String MEDIAINFO_TABLE_NAME = "media_url_table";
	public static final int VERSION = 2;

	//offline db
	public static final String OFFLINE_DB_NAME = "offline_media_db";
	public static final String OFFLINE_TABLE_NAME = "offline_media_table";
	public static final int OFFLINE_VERSION = 7;//data base
	public static final int OFFLINE_FILE_VERSION = 2;//for offline file obscure

	//offline file path
	public static final String OFFLINEMEDIA_DIR = "MIUI/Video/files/";
	public static final String OFFLINE_MEDIA_CONFIG = "cache.cfg";

	
	//media url for player field
	public static final String MEDIA_ID = "media_id";
	public static final String PLAY_INDEX = "play_index";
	public static final String CURRENT_EPISODE = "current_episode";
	public static final String AVAILABLE_EPISODE_COUNT = "available_episode_count";
	public static final String MEDIA_CLARITY = "media_clarity";
	public static final String MEDIA_SOURCE = "media_source";
	public static final String PLAY_TYPE = "playtype";
	public static final String REMOTE_URL = "remote_url";
	public static final String MULTI_SET = "multi_set";
	public static final String MEDIA_HTML5_URL = "media_h5_url";
    public static final String MEDIA_INFO = "media_info";
	public static final String MEDIA_POSTER_URL = "media_poster_url";
	public static final String MEDIA_POSTER_MD5 = "media_poster_md5";
	public static final String MEDIA_DATE = "media_date";
	public static final String MEDIA_SET_STYLE = "media_set_style";  //0 series; 1 varierty
	public static final String KEY_MEDIA_TITLE = "mediaTitle";
	public static final String KEY_MEDIA_SDKINFO = "sdkinfo";
	public static final String KEY_MEDIA_SDKDISABLE = "sdkdisable";
	public static final String VIDEO_TYPE = "video_type";
	public static final String MEDIA_SET_NAME = "media_set_name";
	public static final String KEY_MEDIA_IS_HTML = "is_html";

	//offline field
	public static final String MEDIA_BUCKET_NAME = "media_bucket_name";
	public static final String MEDIA_TYPE = "media_type";
	public static final String MEDIA_COMPLETE_LINES = "complete_lines";
	public static final String MEDIA_IS_MULTSET = "is_multset";
	public static final String MEDIA_LENGTH = "media_len";
	public static final String MEDIA_NAME = "media_name";
	public static final String MEDIA_EP_NAME = "media_ep_name";
	public static final String LOCAL_PATH = "local_path";
	public static final String MEDIA_STATUS = "media_status";
	public static final String MEDIA_FILE_SIZE = "file_size";
	public static final String MEDIA_COMPLETE_SIZE = "complete_size";
	public static final String OFFLINE_STATUS = "offline_status";
	public static final String OFFLINE_SOURCE = "offline_source";
	
	//media url for player provider
	public static final int CODE_MEDIA_INFO_URL = 1;
	public static final int CODE_MEDIA_INFO_URL_ALL = 2;
	public static final String AUTHORITY = "com.miui.video.provider.MediaInfoForPlayerProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" +AUTHORITY +"/" +MEDIAINFO_TABLE_NAME);
}

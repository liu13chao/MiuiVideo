/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   StatisticTagDef.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-19
 */

package com.miui.video.statistic;

/**
 *@author xuanmingliu
 *
 */

public class StatisticTagDef {
    public static final String ANDROID_ID_TAG = "androidId";                        //手机串号
	public static final String IME_TAG = "ime";              		   //手机串号
	public static final String TIME_TAG = "time";                     //当前时间
	public static final String APP_VERSION_TAG = "appVersion";        //app version
	public static final String IP_TAG = "ip";                          //ip地址
	public static final String VIDEOID_TAG = "vid"; 				   //视频id
	public static final String SOURCEPATH_TAG = "source_path";              //进入详情页面来源路径
	public static final String SOURCEPOSITION_TAG = "position";        //位置索引，从1开始
	public static final String PLAYMODE_TAG = "playmode";        //播放方式
	public static final String MEDIASETTYPE_TAG = "mediatype";            //视频类型：单集，多集连载中、多集已完结
	public static final String VIDEOTYPE_TAG = "videotype";            //视频类型：单集，多集连载中、多集已完结
	public static final String ISFREE_TAG = "isfree";                  //是否付费
	public static final String CATEGORYID_TAG = "categoryid";          //类别  
	public static final String FILTERID_TAG = "filter";                 //filter
	public static final String SPECIALLISTID_TAG = "speciallistid";    //专题id
	public static final String SEARCHFILTERID_TAG = "searchfilterid";  //搜索分类id
	public static final String MEDIASOURCE_TAG = "media_source";                 //视频来源, sohu iqiyi
	public static final String FROM_TAG = "from";                      //来源
	public static final String SEARCHKEY_TAG = "keyword";                   //搜索关键词    
	public static final String SEARCHKEY_POSITION_TAG = "position";         //关键词来源位置
	public static final String TOPLISTID_TAG = "toplistid";                 //获取排行榜时id
	public static final String SUBJECT_POSITION_TAG = "position";           //某个专题在专题列表中的position
	public static final String CHANNEL_MEDIAINFOLIST_TYPE_TAG = "type";     //热播、最新、精选
	public static final String CHANNEL_MEDIAINFOLIST_FILTER_TAG = "filter"; //channel media list 过滤选项
	public static final String CHANNEL_MEDIAINFOLIST_START_TAG = "start";   //media list请求的start position
	public static final String UUID_TAG = "uuid";   //uuid
	public static final String ISADS_TAG = "isads";   //is ads
	public static final String TIMESTAMP_TAG = "timestamp";   //timestamp
	
	public static final String MEDIAID_TAG = "mediaid";
	public static final String MEDIACI_TAG = "mediaci";
	public static final String MEDIAURL_TAG = "mediaurl";
	public static final String DATEPLAYINFO_TAG = "dateplayinfo";
	public static final String PLAYINFOS_TAG = "playinfos";
	
	//live channel statistic
	public static final String TV_CHANNELNAME_TAG = "channelname";
	public static final String TV_CHANNELID_TAG = "channelid";
	public static final String TV_VIDEOIDENTIFYING_TAG = "videoidentifying";
    public static final String TV_AD_END_TIME_TAG = "adendtime"; 
    public static final String TV_AD_DURATION_TAG = "adduration"; 
	public static final String TV_AD_START_TIME_TAG = "adstarttime";   // ad start time
	public static final String TV_PLAY_TIME_TAG = "playtime";   // play time
	public static final String TV_SOURCE_TAG = "source";   // source
	public static final String TV_ID_TAG = "tvid";   //直播id
	public static final String TV_ENTRY_TAG = "entry";   //直播入口
	public static final String TV_START_TIME_TAG = "starttime";   //直播开始时间
	public static final String TV_END_TIME_TAG = "endtime";   //直播结束时间
	public static final String TV_LOADING_TIME_TAG = "loadingtime";   //直播加载时间
	public static final String TV_BUFFER_COUNT_TAG = "buffercount";   //直播缓冲次数
	
	public static final String MY_FAVORITE_ACTION_TAG = "favoriteAction";  //1收藏, -1取消收藏
	public static final String COMMENT_SCORE_TAG = "commentScore";  //用户评分1~5
	public static final String BANNER_CATEGORY_TAG = "bannerCategory";  //banner的分类
	
	public static final String COM_USER_DATA_TYPE_TAG = "comUserDataType";  //用户数据的类型
	public static final String SEARCH_RESULT_HIT_TAG = "searchResultHit";  //搜索结果
	public static final String STATISTIC_INFO = "statisticinfo";
}



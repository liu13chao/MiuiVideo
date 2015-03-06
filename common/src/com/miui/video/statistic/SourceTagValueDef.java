/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   SourceValueDef.java
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

//进入MediaDetialActivity的source值定义
public class SourceTagValueDef {
	//phone start
	public static final String  UNKNOWN_VALUE = "unknown";                        //未知
	public static final String  HOMEACTIVITY_VALUE = "mainpage";                  //首页推荐     homeactivity
	public static final String  CHANNELACTIVITY_RANK_VALUE = "toplist_list";      //排行聚合页面     channelactivity中的排行
	public static final String  RANKACTIVITY_VALUE = "toplist_detail";            //排行详情     rankmediaactivity
	public static final String  HOMEBANNER_VALUE = "banner_main";                 //首页Banner  homeactivity中的banner
	public static final String  CHANNELBANNER_VALUE = "banner_category";          //分类Banner channelactivity的精选的banner
	public static final String  FEATURELIST_VALUE = "list_feature";               //精选        channelactivity中的精选
	public static final String  HOTLIST_VALUE = "list_hot";                       //热门         channelactivity中的热门
	public static final String  NEWESTLIST_VALUE = "list_newest";                 //最新         channelactivity中的最新
	public static final String  SPECIALLIST_VALUE = "list_special";               //专题list  SubjectListActivity
	public static final String  RECOMMENDATION_VALUE = "recommend_detail";        //详情页面推荐     MediaDetialActivity
	public static final String  RECOMMENDATION_USERBEHAVIOUR_VALUE = "recommend_userbehaviour";    //用户浏览历史推荐
	public static final String  FAVOURITELOCAL_VALUE = "favourite_local";         //本地收藏视频
	public static final String  SEARCH_VALUE = "search";                          //搜索
	public static final String  DETAIL_VALUE = "detail";                          //详情页
	public static final String  RECENTPLAY_VALUE = "recentplay";                  //recent play activity
	public static final String  MYFAVORITE_VALUE = "myfavorite";                  //my favorite
	public static final String  NOTIFICATION_VALUE = "notification";              //notification
	public static final String  SEARCH_RESULT_INFO_VALUE = "search_result_info";  //搜索结果页面
	public static final String  SEARCH_RESULT_RECOMMEND_VALUE = "search_result_recommend";  //搜索结果推荐页面
	
	//player start
	public static final String  VIDEOPLAYER_VALUE = "videoPlayer";                //videoPlayer

	//pad start
	public static final String PAD_HOME_BANNER_VALUE = "pad_home_banner";                       //首页banner页面
	public static final String PAD_HOME_SPECIAL_VALUE = "pad_home_special";                     //首页专题页面
	public static final String PAD_HOME_ONLINE_VALUE = "pad_home_online";                       //首页在线页面
	public static final String PAD_PLAY_HIS_VALUE = "pad_home_play_his";                        //首页本地页面
	public static final String PAD_DETAIL_VALUE = "pad_detail";                                 //详情页面
	public static final String PAD_DETAIL_SELECT_EP_VALUE = "pad_detail_select_ep";             //详情剧集选择页面
	public static final String PAD_DETAIL_SELECT_VARIETY_VALUE = "pad_detail_select_variety";   //详请综艺选择页面
	public static final String PAD_DETAIL_RECOMMEND_VALUE = "pad_detail_recommend";             //详情推荐页面
	public static final String PAD_FEATURE_LIST_VALUE = "pad_feature_list";                     //专题列表页面
	public static final String PAD_FEATURE_MEDIA_VALUE = "pad_feature_media";                   //专题影片页面
	public static final String PAD_MY_FAVORITE_VALUE = "pad_my_favorite";                       //我的收藏页面
	public static final String PAD_SEARCH_RESULT_VALUE = "pad_search_result";                   //搜索结果页面
	public static final String PAD_CHANNEL_ALL_VALUE = "pad_channel_all";                       //频道全部页面
	public static final String PAD_CHANNEL_CHOICE_VALUE = "pad_channel_choice";                 //频道精选页面
	public static final String PAD_CHANNEL_RANK_VALUE = "pad_channel_rank";                     //频道排行页面
	public static final String PAD_CHANNEL_RANK_MEDIA_VALUE = "pad_channel_rank_media";         //频道排行更多页面
	public static final String PAD_DOWNLOAD_SELECT_VALUE = "pad_download_select";               //下载选择页面
	public static final String PAD_TV_CHANNEL_LIST_VALUE = "pad_tv_channel_list";               //直播频道页面
	public static final String PAD_TV_PLAYER_VALUE = "pad_tv_player";                           //直播播放页面

	//phone v6 start
	public static final String PHONE_V6_BANNER_VALUE = "phone_v6_banner";                                 //banner页面
	public static final String PHONE_V6_HOME_SPECIAL_VALUE = "phone_v6_home_special";                     //首页专题页面
	public static final String PHONE_V6_HOME_ONLINE_VALUE = "phone_v6_home_online";                       //首页在线页面
	public static final String PHONE_V6_PLAY_HIS_VALUE = "phone_v6_play_his";                             //播放历史页面
	public static final String PHONE_V6_DETAIL_VALUE = "phone_v6_detail";                                 //详情页面
	public static final String PHONE_V6_DETAIL_SELECT_EP_VALUE = "phone_v6_detail_select_ep";             //详情剧集选择页面
	public static final String PHONE_V6_DETAIL_SELECT_VARIETY_VALUE = "phone_v6_detail_select_variety";   //详请综艺选择页面
	public static final String PHONE_V6_DETAIL_RECOMMEND_VALUE = "phone_v6_detail_recommend";             //详情推荐页面
	public static final String PHONE_V6_FEATURE_LIST_VALUE = "phone_v6_feature_list";                     //专题列表页面
	public static final String PHONE_V6_FEATURE_MEDIA_VALUE = "phone_v6_feature_media";                   //专题影片页面
	public static final String PHONE_V6_MY_FAVORITE_VALUE = "phone_v6_my_favorite";                       //我的收藏页面
	public static final String PHONE_V6_SEARCH_RESULT_VALUE = "phone_v6_search_result";                   //搜索结果页面
	public static final String PHONE_V6_CHANNEL = "phone_v6_channel";                                     //频道页面
	public static final String PHONE_V6_CHANNEL_ALL_VALUE = "phone_v6_channel_all";                       //频道全部页面
	public static final String PHONE_V6_CHANNEL_CHOICE_VALUE = "phone_v6_channel_choice";                 //频道精选页面
	public static final String PHONE_V6_CHANNEL_RANK_VALUE = "phone_v6_channel_rank";                     //频道排行页面
	public static final String PHONE_V6_CHANNEL_RANK_MEDIA_VALUE = "phone_v6_channel_rank_media";         //频道排行更多页面
	public static final String PHONE_V6_DOWNLOAD_SELECT_VALUE = "phone_v6_download_select";               //下载选择页面
	public static final String PHONE_V6_TV_CHANNEL_LIST_VALUE = "phone_v6_tv_channel_list";               //直播频道页面
	public static final String PHONE_V6_TV_PLAYER_VALUE = "phone_v6_tv_player";                           //直播播放页面
	public static final String PHONE_V6_SHORT_VIDEO_VALUE = "phone_v6_short_video";                       //短视频播放页面
} 



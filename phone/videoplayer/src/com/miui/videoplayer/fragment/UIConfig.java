/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   UIConfig.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-4
 */

package com.miui.videoplayer.fragment;

/**
 * @author tianli
 *
 */
public class UIConfig {
	
	public static enum MediaStyle{
		IDLE,
		SEEK,
		BUFFER,
	};
	
	public final static int MSG_WHAT_PLAY_CI = 100001;
	public final static int MSG_WHAT_VIDEO_START = 100002;
	public final static int MSG_WHAT_VIDEO_PAUSE = 100003;
	public final static int MSG_WHAT_VIDEO_SEEK = 100004;
	public final static int MSG_WHAT_QIYI_ADS_END = 100005;
	public final static int MSG_WHAT_HIDE_CORE_FRAGMENT = 100006;
	public final static int MSG_WHAT_PLAY_NEXT = 100007;
	public final static int MSG_WHAT_ENTER_AIRKAN_MODE = 100008;
	public final static int MSG_WHAT_EXIT_AIRKAN_MODE = 100009;
	public final static int MSG_WHAT_LOADING_VIDEO = 100010;
	public final static int MSG_WHAT_HIDE_FULLSCREEN_BG = 100011;
	
	public final static int MSG_WHAT_SHOW_MEDIA_INFO = 100012;
	public final static int MSG_WHAT_HIDE_MEDIA_INFO = 100013;
	public final static int MSG_WHAT_FINISH = 100014;
	
	public final static int MSG_WHAT_SCALE_SCREEN = 100015;
	public final static int MSG_WHAT_SWITCH_CLARITY = 100016;
	public final static int MSG_WHAT_PLAY_SOURCE_RESOLUTION = 100017;
	
	public final static int VIDEO_SIZE_STYLE_AUTO = 0;
	public final static int VIDEO_SIZE_STYLE_FULL_SCREEN = 1;
	public final static int VIDEO_SIZE_STYLE_ADAPT_WIDTH = 2;
	public final static int VIDEO_SIZE_STYLE_ADAPT_HEIGHT = 3;
	public final static int VIDEO_SIZE_STYLE_16_9 = 4;
	public final static int VIDEO_SIZE_STYLE_4_3 = 5;
}

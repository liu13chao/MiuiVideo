/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   ChannelMediaInfoListTypeDef.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-20
 */

package com.miui.video.statistic;

/**
 *@author xuanmingliu
 *
 */

public class ChannelMediaInfoListTypeDef {

	public static final int  LIST_UNKNOWN_TYPE_CODE = -1;
	public static final int  LIST_HOT_TYPE_CODE = 0;
	public static final int  LIST_NEWEST_TYPE_CODE = 1;
	public static final int  LIST_FEATURE_TYPE_CODE = 2;
	
	public static final String LIST_UNKNOWN_TYPE = "unknown";
	public static final String LIST_HOT_TYPE = "hot";                //热播
	public static final String LIST_NEWEST_TYPE = "newest";          //最新
	public static final String LIST_FEATURE_TYPE = "feature";        //精选
	
	public static String getMediaInfoListType(int type)
	{
		switch(type)
		{
		case LIST_HOT_TYPE_CODE:
			return LIST_HOT_TYPE;
		case LIST_NEWEST_TYPE_CODE:
			return LIST_NEWEST_TYPE;
		case LIST_FEATURE_TYPE_CODE:
			return LIST_FEATURE_TYPE;
		default:
			return LIST_UNKNOWN_TYPE;
		}
	}
}



/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   MediaIsFreeDef.java
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

public class MediaIsFreeDef {

	public static final String MEDIA_ISFREE = "Y";
	public static final String MEDIA_NOT_FREE = "N";
	
	public static String getMediaIsFree(boolean isFree)
	{
		if(isFree)
			return MEDIA_ISFREE;
		else
			return MEDIA_NOT_FREE;
	}
}



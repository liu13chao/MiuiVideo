/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   PlayUrlMediaSourceInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-24
 */

package com.miui.video.type;

/**
 *@author xuanmingliu
 *
 */

public abstract class MediaSourceInfo {
	public int mediaId;
	public int ci;
	public int quality;
	public String mediaName;
	public int mediaSetType;    // 0 单集   1 多集      
	
	public int getMediaId()
	{
		return mediaId;
	}
	
	public int getMediaCi()
	{
		return ci;
	}
	
	public int getQuality()
	{
		return quality;
	}
	
	public String getMediaName()
	{
		return mediaName;
	}
	
	public int getMediaSetType()
	{
		return mediaSetType;
	}
	
	public abstract String formatPlayParameterToJson();
}



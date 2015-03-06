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

import org.json.JSONObject;

/**
 *@author xuanmingliu
 *
 */

public class PlayUrlMediaSourceInfo {

	public int ciidx;
	public int source = -1;
	public int box = 0;            // 0盒子上没有源， 1盒子上有源(2种播放方式：自己播放器，第3放播放器)
	public int mediaid;
	public String medianame;
	public int ci;    //第几集
	public int setcount;  //总集数
	public int setnow;  //当前集数
	public int ismultset;  //单集,多集
	public int resolution;  //清晰度
	public String issuedate;  //综艺日期
	public JSONObject sourceinfo;	     //play parameter
	
	public boolean isMultiSetType()
	{
		if( ismultset > 0)
			return true;
		
		return false;
	}
}



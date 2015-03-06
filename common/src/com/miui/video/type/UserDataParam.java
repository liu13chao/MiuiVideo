/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   UserDataParam.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-12
 */

package com.miui.video.type;

/**
 *@author xuanmingliu
 *
 */

/*
 * 获取历史播放记录
 */
public class UserDataParam {
	//当前所有channel的视频播放记录保存在一起，并且仅维持30条
	public int channelID;    //请求所有channel视频的playhistory，取值 -1
	public int pageNo;       //默认传1
	public int pageSize;     //默认30
	public int latestDays;   //请求所有channel视频的playhistory，取值 -1
}



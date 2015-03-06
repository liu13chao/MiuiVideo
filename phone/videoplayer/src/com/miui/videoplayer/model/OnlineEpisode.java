/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OnlineEpisode.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-9
 */

package com.miui.videoplayer.model;

/**
 * @author tianli
 *
 */
public class OnlineEpisode extends Episode {
	
	private int mMediaStyle;
	
	private String mDate;

	public int getMediaStyle() {
		return mMediaStyle;
	}

	public void setMediaStyle(int mediaStyle) {
		this.mMediaStyle = mediaStyle;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String date) {
		this.mDate = date;
	}
	
}

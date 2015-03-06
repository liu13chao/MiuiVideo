/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   PlayVideoInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-10-15
 */

package com.miui.video.type;

import android.graphics.drawable.Drawable;

/**
 * @author xuanmingliu
 * 
 */

public class VideoInfo {
	public String vId;

	// 是否是本地视频
	public boolean localVideo;

	// 播放到的位置，取thumbnail
	public String playSeconds;
	// 视频路径
	public String videoUri;
	public String html5Page;

	public Drawable thumbDrawable;

	// 当failedCount = 2时，不在继续获取该video的thumbnail
	private static int MAX_FAILED_COUNT = 3;
	// 失败计数
	private int failedCount = 0;

	public synchronized void setFailedOnce() {
		failedCount += 1;
	}

	public synchronized boolean isFailed() {
		if (failedCount >= VideoInfo.MAX_FAILED_COUNT - 1)
			return true;

		return false;
	}
}

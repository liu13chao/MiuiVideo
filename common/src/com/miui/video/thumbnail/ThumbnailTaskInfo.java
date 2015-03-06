/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  ThumbnailTaskInfo.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-20
 */
package com.miui.video.thumbnail;

import com.miui.video.controller.BitmapFilter;
import com.miui.video.util.Util;

/**
 * @author tianli
 *
 */
public class ThumbnailTaskInfo{
	
	private static int MAX_FAILED_COUNT = 3;
	private String mThumbnailKey;
	private long mPlayPosition;  // millisecond.
	private String mVideoUri;
	
	private BitmapFilter mBitmapFilter;

	private int mFailedCount = 0;
	
	public ThumbnailTaskInfo(String url, long position){
		mVideoUri = url;
		mPlayPosition = position;
		mThumbnailKey = Util.getMD5(mVideoUri + "&pos=" + mPlayPosition);
	}

	public String getVideoUri() {
		return mVideoUri;
	}

	public long getPlayPosition() {
		return mPlayPosition;
	}

	public synchronized void incFailedCount() {
		mFailedCount += 1;
	}

	public synchronized boolean isFailed() {
		if (mFailedCount >= MAX_FAILED_COUNT - 1){
			return true;
		}
		return false;
	}
	
	public String getThumbnailKey(){
		return mThumbnailKey;
	}
	
	public void setBitmapFilter(BitmapFilter filter){
	    mBitmapFilter = filter;
	}
	
	public BitmapFilter getBitmapFilter(){
	    return mBitmapFilter;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof ThumbnailTaskInfo){
			return ((ThumbnailTaskInfo)o).mThumbnailKey.equals(mThumbnailKey);
		}
		return super.equals(o);
	}
	
}

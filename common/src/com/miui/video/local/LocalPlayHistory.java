/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  LocalPlayHistory.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-30
 */
package com.miui.video.local;

import com.miui.video.offline.OfflineMedia;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.type.LocalMedia;

/**
 * @author tianli
 *
 */
public class LocalPlayHistory extends PlayHistory {

	private static final long serialVersionUID = 2L;
	
	public LocalMedia mLocalMedia;
	
	public LocalPlayHistory(LocalMedia localMedia){
		if(localMedia == null) {
			return;
		}
		mLocalMedia = localMedia;
		completeData();
	}
	
	public LocalPlayHistory(OfflineMedia offlineMedia) {
		if(offlineMedia == null) {
			return;
		}
		LocalMedia localMedia = new LocalMedia();
		localMedia.mediaTitle = offlineMedia.mediaName;
		localMedia.mediaPath = offlineMedia.localPath;
		//OfflineMedia playLength的单位是s，得转化成ms
		localMedia.mediaDuration = offlineMedia.playLength * 1000;
		
		mLocalMedia = localMedia;
		completeData();
	}

	@Override
	public String getDesc() {
		if(mLocalMedia != null) {
			return mLocalMedia.getDesc();
		}
		return "";
	}
	
	@Override
	public String getUrl() {
		if(mLocalMedia != null) {
			return mLocalMedia.getPath();
		}
		return "";
	}

	@Override
	public BaseMediaInfo getPlayItem() {
		return mLocalMedia;
	}
	
	@Override
	public ImageUrlInfo getPosterInfo() {
//		if(mThumbnailTaskInfo == null && mLocalMedia != null) {
//			if(!Util.isEmpty(mLocalMedia.mediaPath)) {
//				mThumbnailTaskInfo = new ThumbnailTaskInfo(mLocalMedia.mediaPath, 3);
//			}
//		}
		return null;
	}

	private void completeData() {
		if(mLocalMedia == null) {
			return;
		}
		this.mediaId = -1;
		this.mediaUrl = mLocalMedia.getPath();
	}

}

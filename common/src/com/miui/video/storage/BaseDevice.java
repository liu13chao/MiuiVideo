package com.miui.video.storage;

import java.util.ArrayList;
import java.util.List;

import com.miui.video.type.BaseMediaInfo;

public abstract class BaseDevice extends BaseMediaInfo {
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final int FAIL_REASON_OTHER = -1;
	
	public DirMediaItem mRootDirMediaItem;
	
	public List<MediaItem> mVideoMediaItemList;
	public List<MediaItem> mImageMediaItemList;//keep
	public List<MediaItem> mAudioMediaItemList;
	
	protected int mPriority = 100;
	
	private String mStartPath = null;
	private String mStartMediaUrl = null;//the mediaFile to play from history
	
	protected int mVideoSize;

	public int getPriority(){
		return mPriority;
	}
	
	public void setVideoSize(int videoSize) {
		this.mVideoSize = videoSize;
	}
	
	public int getVideoSize(){
	    return mVideoSize;
	}
	
	public void setRootDirMediaItem(DirMediaItem rootDirMediaItem) {
		mRootDirMediaItem = rootDirMediaItem;
		if( mVideoMediaItemList == null)
			mVideoMediaItemList = new ArrayList<MediaItem>();
		else
			mVideoMediaItemList.clear();
		
		if( mImageMediaItemList == null)
			mImageMediaItemList = new ArrayList<MediaItem>();
		else
			mImageMediaItemList.clear();
		
		if( mAudioMediaItemList == null)
			mAudioMediaItemList = new ArrayList<MediaItem>();
		else
			mAudioMediaItemList.clear();
		
	}
	
	public DirMediaItem getRootDirMediaItem() {
		return mRootDirMediaItem;
	}
	
//	public void reset(){
//		mCancel = false;
//	}
//	
//	public void cancel(){
//		mCancel = true;
//	}
//	
//	protected boolean isCancel(){
//		return mCancel;
//	}
	
	public boolean needRefresh(){
		return false;
	}
	
	public abstract boolean isRemoveable();
	public abstract String getRootPath();
	public abstract void startBrowsing(DeviceScanTask scanTask, OnBrowseCompleteListener listener);
	
	public String getStartPath() {
		return mStartPath;
	}

	public void setStartPath(String mStartPath) {
		this.mStartPath = mStartPath;
	}

	public String getStartMediaUrl() {
		return mStartMediaUrl;
	}

	public void setStartMediaUrl(String mStartMediaUrl) {
		this.mStartMediaUrl = mStartMediaUrl;
	}

	public interface OnBrowseCompleteListener {
		public void onBrowseFileComplete(ArrayList<MediaItem> items);
		public void onBrowseFileReady(ArrayList<MediaItem> items);
		public void onBrowseDir(MediaItem item);
		public void onBrowseFail(int reason);
		public void onBrowseCompelete();
		
		public void onBrowseDirComplete(ArrayList<MediaItem> items);
	}

    @Override
    public String getMediaStatus() {
        return "";
    }

    @Override
    public String getSubtitle() {
        return null;
    }
}


package com.miui.video.storage;

import java.util.ArrayList;
import java.util.List;
import android.os.AsyncTask;
import android.os.Handler;

import com.miui.video.storage.BaseDevice.OnBrowseCompleteListener;
import com.miui.video.util.OrderUtil;

public class DeviceBrowseTask extends AsyncTask<Void, Void, Void> {

	private final static int STATE_INIT = 0;
	private final static int STATE_SEARCH_VIDEO = 1;
	private final static int STATE_SEARCH_ALL_VIDEO = 2;
	private final static int STATE_BROWE_ALL_VIDEO = 3;
	private final static int STATE_ERROR = 4;
	private final static int STATE_DONE = 5;
	
	private final static String VideoEn = "Video";
	private final static String AllVideoEn = "All Video";
	private final static String VideoCn = "视频";
	private final static String AllVideoCn = "所有视频";
	
	
	private int mState = STATE_INIT;

	private BaseDevice mDevice;
	private String mPath;
	
	private List<MediaItem> mItems = new ArrayList<MediaItem>();
	
	private List<DeviceBrowseCompleteListener> mListeners = new ArrayList<DeviceBrowseCompleteListener>();
	
	private Handler mHandler = new Handler();
	
	public DeviceBrowseTask(BaseDevice baseDevice) {
		mDevice = baseDevice;
		if(mDevice != null) {
			mPath = mDevice.getRootPath();
		}
	}
	
	protected void addListener(DeviceBrowseCompleteListener listener) {
		if(listener != null && !mListeners.contains(listener)) {
			mListeners.add(listener);
		}
	}
	
	protected void removeListener(DeviceBrowseCompleteListener listener) {
		if(listener != null) {
			mListeners.remove(listener);
		}
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		if(mDevice == null) {
			return null;
		}
		mState = STATE_SEARCH_VIDEO;
		browsePath(mPath);
		return null;
	}
	
	//packaged method
	private void notifyDeviceBrowseComplete() {
		mHandler.post(mNotifyDeviceBrowseCompleteRunnable);
	}
	
	//UI task
	private Runnable mNotifyDeviceBrowseCompleteRunnable = new Runnable() {
		
		@Override
		public void run() {
			for(int i = 0; i < mListeners.size(); i++) {
				DeviceBrowseCompleteListener listener = mListeners.get(i);
				if(listener != null) {
					listener.onDeviceBrowseComplete(mDevice, mItems);
				}
			}
		}
	};
	
	//扫描某个目录
	private void browsePath(String path){
		DeviceScanTask task = new DeviceScanTask(mDevice, path);
		mDevice.startBrowsing(task, mOnBrowseCompleteListener);
	}
	
	private OnBrowseCompleteListener mOnBrowseCompleteListener = new OnBrowseCompleteListener() {
		
		@Override
		public void onBrowseFileReady(ArrayList<MediaItem> items) {

		}
		
		@Override
		public void onBrowseFileComplete(ArrayList<MediaItem> items) {
			if(mState == STATE_BROWE_ALL_VIDEO){
				mItems.clear();
				mItems.addAll(items);
			}
		}
		
		@Override
		public void onBrowseFail(int reason) {
			
		}
		
		@Override
		public void onBrowseDirComplete(ArrayList<MediaItem> items) {
			if(mState == STATE_SEARCH_VIDEO || mState == STATE_SEARCH_ALL_VIDEO){
				mItems.clear();
				mItems.addAll(items);
			}
		}
		
		@Override
		public void onBrowseDir(MediaItem item) {
			
		}
		
		@Override
		public void onBrowseCompelete() {
			if(mState == STATE_SEARCH_VIDEO){
				mPath = "";
				for(int i = 0; i < mItems.size(); i++) {
					MediaItem mediaItem = mItems.get(i);
					if(mediaItem != null && mediaItem.getName() != null) {
						String name = mediaItem.getName();
						if(name.equalsIgnoreCase(VideoEn) || name.equals(VideoCn)){
							mPath = mediaItem.getPath();
						}
					}
					
				}
				if(mPath == ""){
					mState = STATE_ERROR;
				} else{
					mState = STATE_SEARCH_ALL_VIDEO;
					browsePath(mPath);
				}								
			} else if(mState == STATE_SEARCH_ALL_VIDEO){
				mPath = "";
				for(int i = 0; i < mItems.size(); i++) {
					MediaItem mediaItem = mItems.get(i);
					if(mediaItem != null && mediaItem.getName() != null) {
						String name = mediaItem.getName();
						if(name.equalsIgnoreCase(AllVideoEn) || name.equals(AllVideoCn)){
							mPath = mediaItem.getPath();
						}
					}
				}
				if(mPath == ""){
					mState = STATE_ERROR;
				}else{
					mState = STATE_BROWE_ALL_VIDEO;
					browsePath(mPath);
				}								
			} else if(mState == STATE_BROWE_ALL_VIDEO){
				mState = STATE_DONE;
				OrderUtil.orderItems(mItems);
				mDevice.setVideoSize(mItems.size());
				
				notifyDeviceBrowseComplete();
			}
		}
	};
	
	//self def class
	public interface DeviceBrowseCompleteListener {
		public void onDeviceBrowseComplete(BaseDevice baseDevice, List<MediaItem> mediaItems);
	}
}

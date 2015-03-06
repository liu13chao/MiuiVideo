/**
 * 
 */
package com.miui.video.storage;

import java.util.ArrayList;
import java.util.List;

import com.miui.video.storage.BaseDevice.OnBrowseCompleteListener;
import com.miui.video.util.DKLog;

/**
 * @author tianli
 *
 */
public class DeviceScanTask extends Thread{
	
	public static final String TAG = "MediaScanTask";
	private final String mPath;

	public final static int STATE_INIT = 0;
	public final static int STATE_FILE_READY = 1;
	public final static int STATE_FILE_COMPLETED = 2;
	public final static int STATE_COMPLETED = 3;
	
	private int mState = STATE_INIT;
	
	private boolean mIsPaused = false;
	private boolean mIsStopped = false;
	private boolean mIsFail = false;
	private boolean mCancelSort = false;
	
	private int mTotalDirCount = 0;
	
	private BaseDevice mDevice;
	private ScanStateListener mStateListener;
	
	private ArrayList<MediaItem> mRawList = new ArrayList<MediaItem>();

	public DeviceScanTask(BaseDevice device, String path) {
		mPath = path;
		mDevice = device;
	}
	
	public boolean isCompleted(){
		return mState == STATE_COMPLETED;
	}
	
	public boolean isFailed(){
		return mIsFail;
	}
	
	public boolean isFileReady(){
		return mState == STATE_FILE_READY;
	}
	
	public void setStateListener(ScanStateListener stateListener) {
		this.mStateListener = stateListener;
	}

	@Override
	public void run() {
		DKLog.i(TAG, "in MediaScanTask " + mPath);
		try {
			mTotalDirCount = 0;
			mDevice.startBrowsing(this, new OnBrowseCompleteListener() {
				@Override
				public void onBrowseFileComplete(final ArrayList<MediaItem> items) {
					mState = STATE_FILE_COMPLETED;
					DKLog.d(TAG, "file complete." + mStateListener);
					final ScanStateListener stateListener = mStateListener;
					if(stateListener != null){
						stateListener.onItemUpdated(items, mState);
					} 
				}
				@Override
				public void onBrowseFileReady(ArrayList<MediaItem> items) {
					mState = STATE_FILE_READY;
					if(items != null) {
						mRawList.addAll(items);
					}
				}
				@Override
				public void onBrowseFail(int reason) {
					mIsFail = true;
					final ScanStateListener stateListener = mStateListener;
					if(stateListener != null){
						stateListener.onScanFailed();
					}
				}
				@Override
				public void onBrowseDir(MediaItem item) {
					DKLog.i(TAG, "onBrowseDir, item: " + item.getName());
					mTotalDirCount++;
					ArrayList<MediaItem> list = new ArrayList<MediaItem>();
					list.add(item);
					final ScanStateListener stateListener = mStateListener;
					if(stateListener != null){
						stateListener.onItemUpdated(list, mState);
					}
				}
				@Override
				public void onBrowseCompelete() {
					mState = STATE_COMPLETED;
					final ScanStateListener stateListener = mStateListener;
					if(stateListener != null){
						stateListener.onScanCompleted();
					}
				}
				
				@Override
				public void onBrowseDirComplete(ArrayList<MediaItem> items) {
					mTotalDirCount += items.size();
					final ScanStateListener stateListener = mStateListener;
					if(stateListener != null){
						stateListener.onItemUpdated(items, mState);
					}
				}
			});
		} catch (Exception e) {
			DKLog.e(TAG, "browse dir failed.", e);
		}
		if(mState != STATE_COMPLETED){
			final ScanStateListener stateListener = mStateListener;
			if(stateListener != null){
				stateListener.onScanCompleted();
			}
		}
		mState = STATE_COMPLETED;
		mRawList.clear();
	}

	public String getPath() {
		return mPath;
	}

	public boolean isPaused() {
		return mIsPaused;
	}

	public void setPaused(boolean paused) {
		this.mIsPaused = paused;
	}
	
	public void setStopped(boolean stopped) {
		this.mIsStopped = stopped;
	}

	public boolean isStopped() {
		return mIsStopped;
	}

	public synchronized void pauseTask() {
		try {
			wait();
		} catch (Exception e) {
		}
	}

	public synchronized void resumeTask() {
		mIsPaused = false;
		notifyAll();
	}
	
	public interface ScanStateListener{
		public void onItemUpdated(final List<MediaItem> mediaItems, int state);
		public void onScanCompleted();
		public void onScanFailed();
	}

}

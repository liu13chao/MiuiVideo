package com.miui.video.offline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.content.Context;

import com.miui.video.DKApp;
import com.miui.video.db.DBOperationCallback;
import com.miui.video.db.OfflineDBManager;
import com.miui.video.model.AppSingleton;
import com.miui.video.offline.OfflineDownloader.OfflineLoaderListener;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;

public class OfflineMediaManager extends AppSingleton implements OfflineLoaderListener {
	public static final String TAG = "OfflineMediaManager";
	
	private static final int MAX_RUNNING_LOADER = 1;
	private static final Executor WORKER = Executors.newCachedThreadPool();
	
	// unfinished
	private Map<String, OfflineDownloader> mLoaders = new TreeMap<String, OfflineDownloader>();
	
	// finished
	private Map<String, OfflineMedia> mFinishedMedias = new TreeMap<String, OfflineMedia>();
	
	private OfflineDBManager mDBManager;
	
	@Override
    public void init(Context context) {
        super.init(context);
        mDBManager = DKApp.getSingleton(OfflineDBManager.class);
        loadAllMedias();
    }

    private String getKey(int mediaId, int episode) {
		return mediaId + "_" + episode;
	}
	
	private OfflineDownloader getOfflineloader(int mediaId, int episode) {
		String key = getKey(mediaId, episode);
		return mLoaders.get(key);
	}
	
	private OfflineDownloader getOfflineloader(OfflineMedia media) {
		if (media == null) {
			return null;
		}
		return mLoaders.get(media.getKey());
	}
	
	public int getOfflineMediaCount(){
	    return mLoaders.size() + mFinishedMedias.size();
	}
	
	public int getFinishedMediaCount(){
        return mFinishedMedias.size();
    }
	
	public int getUnfinishedMediaCount(){
        return mLoaders.size();
    }
	
	public OfflineMedia getOfflineMedia(int mediaId, int episode) {
		String key = getKey(mediaId, episode);
		OfflineDownloader loader = mLoaders.get(key);
		if (loader != null) {
			return loader.getOfflineMedia();
		} else {
			return mFinishedMedias.get(key);
		}
	}
	
	public List<OfflineMedia> getUnfinishedMedias() {
//		final long start = System.currentTimeMillis();
		List<OfflineMedia> medias = new ArrayList<OfflineMedia>();
		List<OfflineMedia> medias_loading = new ArrayList<OfflineMedia>();
		List<OfflineMedia> medias_waiting = new ArrayList<OfflineMedia>();
		List<OfflineMedia> medias_paused = new ArrayList<OfflineMedia>();
		for (OfflineDownloader loader : mLoaders.values()) {
			if (loader != null && loader.getOfflineMedia() != null) {
				if(loader.isLoading()){
					medias_loading.add(loader.getOfflineMedia());
				}else if(loader.isWaiting()){
					medias_waiting.add(loader.getOfflineMedia());
				}else{
					medias_paused.add(loader.getOfflineMedia());
				}
			}
		}
		medias.addAll(medias_loading);
		medias.addAll(medias_waiting);
		medias.addAll(medias_paused);
//		Collections.sort(medias);
//		DKLog.d(TAG, "unfinisheds duration: " + (System.currentTimeMillis() - start));
		return medias;
	}
	
	public List<OfflineMedia> getOfflineMediaList() {
        List<OfflineMedia> medias = new ArrayList<OfflineMedia>();
        medias.addAll(getFinishedMedias());
        medias.addAll(getUnfinishedMedias());
        return medias;
    }
	
	public List<OfflineMedia> getFinishedMedias() {
		List<OfflineMedia> medias = new ArrayList<OfflineMedia>();
		for (OfflineMedia media : mFinishedMedias.values()) {
			if (media != null) {
				medias.add(media);
			}
		}
		return medias;
	}
	
	private void loadAllMedias() {
		mDBManager.getAllRecords(new DBOperationCallback<List<OfflineMedia>>() {
			@Override
			public void onResult(List<OfflineMedia> result) {
				List<OfflineMedia> medias = result;
				if (medias != null) {
					for (OfflineMedia media : medias) {
					    if(media == null){
					        continue;
					    }
						if (!isMediaLoaded(media) && !isMediaLoading(media)) {
							if (!media.isFinished()) {
								OfflineDownloader loader = new OfflineDownloader(media, WORKER);
								loader.setOfflineLoaderListener(OfflineMediaManager.this);
								mLoaders.put(media.getKey(), loader);
								DKLog.d(TAG, "add key " + media.getKey() + ", loaders: " + mLoaders.size());
								startLoader(loader, true);
							} else {
								mFinishedMedias.put(media.getKey(), media);
							}
						}
					}
				}
				startAllLoaders(false);
				onFinishedMediasChanged();
				onUnfinishedMediasChanged();
				onMediasCountChanged();
			}
		});
	}
	
	public void deleteUnfinishedMedias() {
		mDBManager.deleteUnfinishedRecords(null);
		for (OfflineDownloader loader : mLoaders.values()) {
			if (loader != null) {
				deleteOfflineFile(loader.getOfflineMedia());
				loader.stop();
			}
		}
		mLoaders.clear();
		
		onUnfinishedMediasChanged();
		onMediasCountChanged();
	}
	
	public void deleteFinishedMedias() {
		mDBManager.deleteFinishedRecords(null);
		for (OfflineMedia media : mFinishedMedias.values()) {
			deleteOfflineFile(media);
		}
		mFinishedMedias.clear();
		
		onFinishedMediasChanged();
		onMediasCountChanged();
	}
	
	public void deleteMedias(List<OfflineMedia> medias) {
		if (medias == null || medias.size() <= 0) {
			return;
		}
		for (OfflineMedia media : medias) {
			if (media != null) {
				// delete record in db
				mDBManager.deleteRecord(media, null);
				// delete file from fs
				deleteOfflineFile(media);
				
				final String key = media.getKey();
				OfflineDownloader loader = mLoaders.get(key);
				if (loader != null) {
					loader.stop();
					mLoaders.remove(key);
				} else {
					mFinishedMedias.remove(key);
				}
			}
		}
		startAllLoaders(false);
		onUnfinishedMediasChanged();
		onFinishedMediasChanged();
		onMediasCountChanged();
	}
	
	public void deleteMedia(OfflineMedia media) {
		// delete record in db
		mDBManager.deleteRecord(media, null);
		// delete file from fs
		deleteOfflineFile(media);
		
		final String key = media.getKey();
		OfflineDownloader loader = mLoaders.get(key);
		if (loader != null) {
			loader.stop();
			mLoaders.remove(key);
			
			startAllLoaders(false);
			
			onUnfinishedMediasChanged();
		} else {
			mFinishedMedias.remove(key);
			
			onFinishedMediasChanged();
		}
		onMediasCountChanged();
	}
	
	private void deleteOfflineFile(OfflineMedia media) {
		if (media == null || media.localPath == null) {
			return;
		}
		final int lastSlash = media.localPath.lastIndexOf(File.separator);
		if (lastSlash < 0) {
			return;
		}
		Util.delDir(media.localPath.substring(0, lastSlash));
	}
	
	public boolean isMediaLoading(OfflineMedia media) {
		if (media != null && mLoaders.containsKey(media.getKey())) {
			DKLog.d(TAG, "loading, media key: " + media.getKey());
			return true;
		}
		return false;
	}
	
	public boolean isMediaLoaded(OfflineMedia media) {
		if (media != null && mFinishedMedias.containsKey(media.getKey())) {
			DKLog.d(TAG, "loaded, media key: " + media.getKey());
			return true;
		}
		return false;
	}
	
	public void addMedia(final OfflineMedia media) {
	    if (media == null || isMediaLoaded(media) || isMediaLoading(media)) {
	        return;
	    }
	    // add record into db
	    mDBManager.addRecord(media, null);
	    // create and start loader
	    OfflineDownloader loader = new OfflineDownloader(media, WORKER);
	    loader.setOfflineLoaderListener(OfflineMediaManager.this);
	    mLoaders.put(media.getKey(), loader);
	    DKLog.d(TAG, "add key " + media.getKey() + ", loaders: " + mLoaders.size());
	    startLoader(loader, true);
	    // notify to refresh UI
	    onUnfinishedMediasChanged();
	    onMediasCountChanged();
	}
	
	public boolean startDownloader(OfflineMedia media) {
		return startLoader(getOfflineloader(media), true);
	}
	
	public boolean startDownloader(int mediaId, int episode) {
		return startLoader(getOfflineloader(mediaId, episode), true);
	}
	
	private boolean startLoader(OfflineDownloader loader, boolean wake) {
		if (loader == null) {
			DKLog.d(TAG, "OfflineMediaDownloader: " + loader);
			return false;
		}
		if (isMaxRunningLoaders()) {
			loader.idle();
			return false;
		}
		final boolean finished = loader.isFinished();
		if (finished) {
			DKLog.d(TAG, "OfflineMediaDownloader isFinished: " + finished);
			return false;
		}
		final boolean unrecovable = loader.isUnrecovrableError();
		if (unrecovable) {
			DKLog.d(TAG, "OfflineMediaDownloader unrecovable: " + unrecovable);
			return false;
		}
		final boolean loading = loader.isLoading();
		if (loading) {
			DKLog.d(TAG, "OfflineMediaDownloader isLoading: " + loading);
			return true;
		}
		final boolean paused = loader.isPaused();
		if (!wake && paused) {
			DKLog.d(TAG, "OfflineMediaDownloader pausing: " + paused);
			return false;
		}
		loader.start();
		return true;
	}
	
	private int getRunningLoaders() {
		int runningLoaders = 0;
		for (OfflineDownloader loader : mLoaders.values()) {
			if (loader != null && loader.isLoading()) {
				runningLoaders++;
			}
		}
		return runningLoaders;
	}
	
	private boolean isMaxRunningLoaders() {
		final int count = getRunningLoaders();
		if (count >= MAX_RUNNING_LOADER) {
			DKLog.d(TAG, "running OfflineMediaDownloader count: " + count);
			return true;
		}
		return false;
	}
	
	public void startAllDownloaders() {
		startAllLoaders(true);
	}
	
	private void startAllLoaders(boolean wake) {
		if (isMaxRunningLoaders()) {
			return;
		}
		int count = 0;
		for (OfflineDownloader loader : mLoaders.values()) {
			if (startLoader(loader, wake)) {
				count++;
				if (count >= MAX_RUNNING_LOADER) {
					return;
				}
			}
		}
	}
	
	public void pauseDownloader(OfflineMedia media) {
		pauseLoader(getOfflineloader(media));
	}
	
	public void pauseDownloader(int mediaId, int episode) {
		pauseLoader(getOfflineloader(mediaId, episode));
	}
	
	private void pauseLoader(OfflineDownloader loader) {
		if (loader != null) {
			loader.pause();
		}
	}
	
	public void pauseAllDownloaders() {
		pauseAllLoaders();
	}
	
	private void pauseAllLoaders() {
		for (OfflineDownloader loader : mLoaders.values()) {
			pauseLoader(loader);
		}
	}

	private void onFinishedMediasChanged() {
		DKLog.i(TAG, "onFinishedMediasChanged, listeners: " + mFinishedListeners.size());
		List<OfflineMedia> medias = getFinishedMedias();
		for (OfflineMediasChangeListener l : mFinishedListeners) {
			l.onOfflineMediasChange(medias);
		}
	}
	
	private void onUnfinishedMediasChanged() {
		DKLog.i(TAG, "onUnfinishedMediasChanged, listeners: " + mUnfinishedListeners.size());
		List<OfflineMedia> medias = getUnfinishedMedias();
		for (OfflineMediasChangeListener l : mUnfinishedListeners) {
			l.onOfflineMediasChange(medias);
		}
	}
	
	private void onMediasCountChanged() {
		for (OfflineMediasCountChangeListener l : mCountListener) {
			l.onOfflineMediasCountChange(mLoaders.size() + mFinishedMedias.size());
		}
	}
	
	private List<OfflineMediasChangeListener> mFinishedListeners = new ArrayList<OfflineMediasChangeListener>();
	private List<OfflineMediasChangeListener> mUnfinishedListeners = new ArrayList<OfflineMediasChangeListener>();
	private List<OfflineMediasCountChangeListener> mCountListener = new ArrayList<OfflineMediasCountChangeListener>();
	public void registerFinishedMediasChangeListener(OfflineMediasChangeListener l) {
		if (l != null && !mFinishedListeners.contains(l)) {
			mFinishedListeners.add(l);
			DKLog.d(TAG, "add finished listener: " + l + ", listeners: " + mFinishedListeners.size());
		}
	}
	
	public void unregisterFinishedMediasChangeListener(OfflineMediasChangeListener l) {
		mFinishedListeners.remove(l);
		DKLog.d(TAG, "remove finished listener: " + l + ", listeners: " + mFinishedListeners.size());
	}
	
	public void registerUnfinishedMediasChangeListener(OfflineMediasChangeListener l) {
		if (l != null && !mUnfinishedListeners.contains(l)) {
			mUnfinishedListeners.add(l);
			DKLog.d(TAG, "add unfinished listener: " + l + ", listeners: " + mUnfinishedListeners.size());
		}
	}
	
	public void unregisterUnfinishedMediasChangeListener(OfflineMediasChangeListener l) {
		mUnfinishedListeners.remove(l);
		DKLog.d(TAG, "remove unfinished listener: " + l + ", listeners: " + mUnfinishedListeners.size());
	}

	public void registerMediasCountChangeListener(OfflineMediasCountChangeListener l) {
		if (l != null && !mUnfinishedListeners.contains(l)) {
			mCountListener.add(l);
		}
	}
	
	public void unregisterMediasCountChangeListener(OfflineMediasCountChangeListener l) {
		mCountListener.remove(l);
	}
	
	public static interface OfflineMediasChangeListener {
		public void onOfflineMediasChange(List<OfflineMedia> medias);
	}

	public static interface OfflineMediasCountChangeListener {
		public void onOfflineMediasCountChange(int num);
	}
	
	@Override
	public void onMediaChange(OfflineDownloader loader) {
		mDBManager.updateRecord(loader.getOfflineMedia(), null);
	}

	@Override
	public void onProgressChange(OfflineDownloader loader) {
		onUnfinishedMediasChanged();
	}

	@Override
	public void onStatusChange(OfflineDownloader loader) {
		if (loader.isFinished()) {
			mDBManager.updateRecord(loader.getOfflineMedia(), null);
			mLoaders.remove(loader.getKey());
			
			mFinishedMedias.put(loader.getKey(), loader.getOfflineMedia());
			
			startAllLoaders(false);
			
			onFinishedMediasChanged();
		} else if (loader.isPaused() || loader.isUnrecovrableError()) {
			startAllLoaders(false);
		}
		onUnfinishedMediasChanged();
	}

	@Override
	public void onLengthChange(OfflineDownloader loader) {
		mDBManager.updateRecord(loader.getOfflineMedia(), null);
		onUnfinishedMediasChanged();
	}

}

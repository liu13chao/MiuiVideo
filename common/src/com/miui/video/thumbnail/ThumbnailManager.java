/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   VideoThumbManager.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-10-11
 */

package com.miui.video.thumbnail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.model.AppEnv;
import com.miui.video.model.AppSingleton;
import com.miui.video.thumbnail.ThumbnailTaskStack.TaskType;
import com.miui.video.util.DKLog;

/**
 * @author xuanmingliu
 * 
 */

public class ThumbnailManager extends AppSingleton {

	private static final String TAG = "ThumbnailManager";

	private int mVideoThumbWidth;
	private int mVideoThumbHeight;

	private static final int MEM_CACHE_SIZE = 21;

	private String mCacheFolder = null;
	// file cache
	private static final String VIDEO_THUMB_CACHE_FOLDER = "/videoThumbCache";

	// memory cache
	private ThumbnailCache mVideoThumbCache;

	private ThumbnailTaskStack mTaskManager;

	// public void setMaxTaskNum(int max) {
	// if (mTaskManager != null) {
	// mTaskManager.clearFileCachePendingTask();
	// mTaskManager.clearLocalVideoPendingTask();
	// mTaskManager.clearNetworkVideoPendingTask();
	// // mTaskManager.setMaxNum(max);
	// }
	// }

	@Override
    public void init(Context context) {
        super.init(context);
        prepareCahceFolder();
        Resources res = mContext.getResources();
        mVideoThumbWidth = res
                .getDimensionPixelSize(R.dimen.video_thumb_width);
        mVideoThumbHeight = res
                .getDimensionPixelSize(R.dimen.video_thumb_height);
        mVideoThumbCache = new ThumbnailCache(MEM_CACHE_SIZE);
        mTaskManager = new ThumbnailTaskStack();
    }

    private Bitmap getThumbnailInMemCache(String thumbKey) {
		return mVideoThumbCache.getThumbnail(thumbKey);
	}

	public Bitmap getThumbnail(ThumbnailTaskInfo taskInfo) {
		DKLog.i(TAG, "taskInfo: " + taskInfo);
		if (taskInfo == null)
			return null;
		// load from memory cache
		String key = taskInfo.getThumbnailKey();
		Bitmap bitmap = getThumbnailInMemCache(key);
		DKLog.i(TAG, "bitmap in mem cache: " + bitmap);
		// Drawable drawable = getThumbnailInMemCache(key);
		if (bitmap == null) {
			// load from file cache
			String filePath = getVideoThumbnailPath(key);
			File file = new File(filePath);
			if (file.exists()) {
				try {
					bitmap = BitmapFactory.decodeFile(filePath);
					// Bitmap fileBmp = BitmapFactory.decodeByteArray(data, 0,
					// data.length);
					if (bitmap == null) {
						file.delete();
					} else {
						// drawable = new BitmapDrawable(Resources.getSystem(),
						// fileBmp);
						// put in memory cache
						mVideoThumbCache.putThumbnail(key, bitmap);
						// putThumbnailInMemCache(key, drawable);
					}

				} catch (Exception e) {
					DKLog.e(TAG, " Exception : " + e);
				}
			}
		}
		DKLog.i(TAG, "bitmap in disk cache: " + bitmap);
		if (bitmap == null) {
			// retrieve thumbnail
			long start = System.currentTimeMillis();
			bitmap = retrieveThumbnail(taskInfo);
			long end = System.currentTimeMillis();
			Thread curThread = Thread.currentThread();
			long id = curThread.getId();
			// String name = curThread.getName();
			DKLog.i(TAG, "thread id : " + id + ",  retrieveThumbnail costs "
					+ " " + (end - start) + "ms.");
			DKLog.i(TAG, "bitmap retrieve: " + bitmap);
			if (bitmap != null) {
				DKLog.i(TAG, "thumbBmp width = " + bitmap.getWidth()
						+ " height = = " + bitmap.getHeight());
				mVideoThumbCache.putThumbnail(key, bitmap);
				saveToFile(key, bitmap);
			} else {
				taskInfo.incFailedCount();
			}
		}
		DKLog.i(TAG, "bitmap final: " + bitmap);
		return bitmap;
	}

	private Uri toUri(String path){
		Uri uri = null;
		String fileSchemeFile = "file://";
		String fileSchemeContent = "content://";
		if(path.startsWith(fileSchemeFile) || path.startsWith(fileSchemeContent)) {
			uri = Uri.parse(path);
		} else {
			uri = Uri.fromFile(new File(path));
		}
		return uri;
	}
	
	public static boolean canUseDuokanRetriver(){
		try {
			System.loadLibrary("xiaomimediaplayer");
			return true;
		} catch (UnsatisfiedLinkError e) {
			Log.e("DuoKanCodecConstants", "Can not load duokan codec, use origin codec");
			return false;
		}
	}
	
	private Bitmap getFrameAt(com.duokan.MediaMetadataRetriever retriver, String path, long position){
		if (path.startsWith("http://")) {
			retriver.setDataSource(path, new HashMap<String, String>());
		} else {
			retriver.setDataSource(mContext, toUri(path));
		}
		return retriver.getFrameAtTime(position * 1000, 
				com.duokan.MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
	}
	
	private Bitmap getFrameAt(MediaMetadataRetriever retriver, String path, long position){
		if (path.startsWith("http://")) {
			retriver.setDataSource(path, new HashMap<String, String>());
		} else {
			retriver.setDataSource(mContext, toUri(path));
		}
		return retriver.getFrameAtTime(position * 1000, 
				MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
	}
	
	private Bitmap retrieveThumbnail(ThumbnailTaskInfo videoInfo) {
		if (videoInfo == null){
			return null;
		}
		Thread curThread = Thread.currentThread();
		long id = curThread.getId();
		String name = curThread.getName();
		DKLog.i(TAG, "thread id : " + id + " name : " + name
				+ " start retrive thumb.");
		Bitmap thumbnailBmp = null;
		String path = videoInfo.getVideoUri();
		DKLog.i(TAG, "thread id : " + id + " name : " + name + " " + path);
		if(TextUtils.isEmpty(path)){
			DKLog.e(TAG, "thread id : " + id + " path can not be null");
			return null;
		}
		MediaMetadataRetriever originRetriever = null;
		com.duokan.MediaMetadataRetriever duokanRetriever = null;
		try {
			DKLog.i(TAG, "thread id : " + id + " name : " + name + "  MediaMetadataRetriever()");
			if(canUseDuokanRetriver()){
				duokanRetriever = new com.duokan.MediaMetadataRetriever();
				thumbnailBmp = getFrameAt(duokanRetriever, path, videoInfo.getPlayPosition());
			}else{
				originRetriever = new MediaMetadataRetriever();
				thumbnailBmp = getFrameAt(originRetriever, path, videoInfo.getPlayPosition());
			}
			DKLog.i(TAG, "thread id : " + id + " name : " + name + " setDataSource() " + path);
			DKLog.i(TAG, "thread id : " + id + " name : " + name + " getFrameAtTime()" +
					 " position : " + videoInfo.getPlayPosition());
			if (thumbnailBmp != null) {
				int width = thumbnailBmp.getWidth();
				int height = thumbnailBmp.getHeight();
				DKLog.i(TAG, "width : " + width + "  height : " + height);
				if (width > mVideoThumbWidth || height > mVideoThumbHeight) {
					Bitmap scaledBmp = Bitmap.createScaledBitmap(thumbnailBmp,
							mVideoThumbWidth, mVideoThumbHeight, true);
					thumbnailBmp.recycle();
					thumbnailBmp = scaledBmp;
				}
			}
			DKLog.i(TAG, "thread id : " + id + " name : " + name
					+ " end retrive thumb");
		} catch (Exception ex) {
			DKLog.e(TAG, "thread id : " + id + " name : " + name + " Exception " + ex);
		} finally {
			try {
				if (duokanRetriever != null) {
					duokanRetriever.release();
					DKLog.i(TAG, "duokan retriever release finish");
				}
				if (originRetriever != null) {
					originRetriever.release();
					DKLog.i(TAG, "android retriever release finish");
				}
			} catch (Exception ex) {
				DKLog.e(TAG, "thread id : " + id + " name : " + name + " release Exception " + ex);
			}
		}
		return thumbnailBmp;
	}


	private void saveToFile(final String key, final Bitmap bitmap) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				saveToFileImp(key, bitmap);
			}
		}).start();
	}
	
	private void saveToFileImp(String key, Bitmap bitmap) {
		if (bitmap == null)
			return;
		File cahceFile = new File(getVideoThumbnailPath(key));
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(cahceFile);
			bitmap.compress(CompressFormat.JPEG, 90, outputStream);
			outputStream.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void clearMemCache() {
		mVideoThumbCache.clear();
		// videoThumbCacheHashtable.clear();
	}

	private void clearCacheFile() {
		String cacheDirPath = getCachePathFolder();
		File cacheDir = new File(cacheDirPath);
		if (!cacheDir.exists())
			return;

		File[] cacheFiles = cacheDir.listFiles();
		for (File cacheFile : cacheFiles) {
			cacheFile.delete();
		}
	}

	public void clearCache() {
		try {
			mTaskManager.pause();
			clearCacheFile();
			mTaskManager.resume();
		} catch (Exception ex) {
			DKLog.e(TAG, "clearCache() " + ex);
		}
	}

	private String getVideoThumbnailPath(String key) {
		return getCachePath(key);
	}

	public boolean isVideoCacheFileExist(String key) {
		String filePath = getVideoThumbnailPath(key);
		File file = new File(filePath);
		return file.exists();
	}

	private String getCachePath(String key) {
		StringBuilder cachePathBuilder = new StringBuilder();
		cachePathBuilder.append(getCachePathFolder());
		cachePathBuilder.append(File.separator);
		cachePathBuilder.append(key);
		cachePathBuilder.append(".jpg");
		return cachePathBuilder.toString();
	}

	private String getCachePathFolder() {
		if (mCacheFolder == null) {
			StringBuilder builder = new StringBuilder();
			builder.append(DKApp.getSingleton(AppEnv.class)
					.getInternalFilesDir());
			builder.append(VIDEO_THUMB_CACHE_FOLDER);
			mCacheFolder = builder.toString();
		}
		return mCacheFolder;
	}

	private void prepareCahceFolder() {
		String cacheFolder = getCachePathFolder();
		File file = new File(cacheFolder);
		if (file.exists() && !file.isDirectory()) {
			file.delete();
		}
		file.mkdirs();
	}

	private static class ThumbnailCache extends LinkedHashMap<String, Bitmap> {
		private static final long serialVersionUID = 2L;
		private int mSize;

		public ThumbnailCache(int size) {
			super(0, .75F, true);
			mSize = size;
		}

		@Override
		protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
			return size() > mSize;
		}

		public synchronized Bitmap getThumbnail(String key) {
			Bitmap bitmap = get(key);
			return bitmap;
		}

		public synchronized void putThumbnail(String key, Bitmap thumbnail) {
			put(key, thumbnail);
		}
	}

	public void deletaCacheFiles() {
		String cachePathFolder = getCachePathFolder();
		File cacheFolder = new File(cachePathFolder);
		File[] cacheFiles = cacheFolder.listFiles();
		for (File cacheFile : cacheFiles)
			cacheFile.delete();
	}

	public interface ThumbnailReadyListener {
		public void onThumbnailReady(ThumbnailTaskInfo videoInfo, View view, Bitmap bitmap);
	}

	public void clearPendingVideoThumbTask(boolean retainFileCacheTask) {
		if (!retainFileCacheTask) {
			mTaskManager.clearFileCachePendingTask();
		}
		mTaskManager.clearLocalVideoPendingTask();
		mTaskManager.clearNetworkVideoPendingTask();
	}
	
	public boolean fetchThumbnail(ThumbnailTaskInfo taskInfo, View view) {
	    return fetchThumbnail(taskInfo, view, null, false);
	}
	
	public boolean fetchThumbnail(ThumbnailTaskInfo taskInfo, View view,
			ThumbnailReadyListener listener, boolean lowPriority) {
		if (taskInfo == null) {
			return false;
		}
		DKLog.d(TAG, "fetchThumbnail " + taskInfo.getVideoUri());
		if(isTaskDone(view, taskInfo)){
			return true;
		}
		Task task = new Task();
		task.taskInfo = taskInfo;
		if(view != null){
			view.setTag(task);
		}
		DKLog.d(TAG, "fetchThumbnail: push back task.");
		int taskType = getFetchThumbnailTaskType(taskInfo);
		FetchThumbnailTask thumbTask = new FetchThumbnailTask(taskInfo, view, taskType, listener);
		if (listener == null){
			mTaskManager.pushBack(thumbTask, true);
		}else{
			mTaskManager.pushBack(thumbTask, lowPriority);
		}
		return false;
	}
	
	private boolean isTaskDone(View view, ThumbnailTaskInfo taskInfo){
		Object tag = view.getTag();
		if(tag instanceof Task){
			Task task = (Task)tag;
			if(task != null && taskInfo != null){
				if(task.taskInfo.getThumbnailKey().equals(taskInfo.getThumbnailKey())){
					return task.isDone;
				}
			}
		}
		return false;
	}
	
	public static class Task{
		ThumbnailTaskInfo taskInfo;
		boolean isDone = false;
	}

	public void fetchThumbnail(ThumbnailTaskInfo taskInfo,
			ThumbnailReadyListener listener, boolean lowPriority) {
		fetchThumbnail(taskInfo, null, listener, lowPriority);
	}

	
//	private void fetchCacheFileThumbnail(VideoInfo videoInfo,
//			ThumbnailReadyListener listener, boolean lowPriority) {
//		if (videoInfo == null)
//			return;
//		int taskType = getFetchThumbnailTaskType(videoInfo);
//		if (taskType != TaskType.TASK_FILECACHE_TYPE)
//			return;
//		FetchThumbnailTask task = new FetchThumbnailTask(videoInfo,
//				TaskType.TASK_FILECACHE_TYPE, listener);
//		if (listener == null)
//			mTaskManager.pushBack(task, true);
//		else
//			mTaskManager.pushBack(task, lowPriority);
//	}
	
	private boolean isOnline(String uri){
		if(uri != null && uri.startsWith("http://")){
			return true;
		}
		return false;
	}

//	public String getVideoThumbKey(ThumbnailTaskInfo taskInfo){
//		assert(taskInfo != null);
//	    StringBuilder strBuilder = new StringBuilder();
//    	strBuilder.append(taskInfo.mVideoUri);
////	    if( taskInfo.localVideo) {
////	    	strBuilder.append(taskInfo.videoUri);
////	    } else {
////	    	if( taskInfo.html5Page.length() != 0)
////	    		strBuilder.append(taskInfo.html5Page);
////	    	else
////	    		strBuilder.append(taskInfo.videoUri);
////	    }
//	    strBuilder.append("&pos=" + taskInfo.mPlayPosition);
//	    return  Util.getMD5(strBuilder.toString());
//	}

	private int getFetchThumbnailTaskType(ThumbnailTaskInfo videoInfo) {
		assert (videoInfo != null);
//		String key = getVideoThumbKey(videoInfo);
		String filePath = getVideoThumbnailPath(videoInfo.getThumbnailKey());
		File cachefile = new File(filePath);
		if (cachefile.exists()){
			return TaskType.TASK_FILECACHE_TYPE;
		}else if (isOnline(videoInfo.getVideoUri())) {
			return TaskType.TASK_NETWORKVIDEO_TYPE;
		} else {
			return TaskType.TASK_LOCALVIDEO_TYPE;
		}
	}

	protected static class FetchThumbnailTask extends ThumbnailAsyncLoadTask {
		private ThumbnailReadyListener mListener;
		private ThumbnailTaskInfo mTaskInfo;
//		private Bitmap mThumbnail;
		private View mView;
		private Handler mUIHandler;

		FetchThumbnailTask(ThumbnailTaskInfo taskInfo, View view, int taskType,
				ThumbnailReadyListener listener) {
			super(taskInfo.getThumbnailKey(), taskType);
			mTaskInfo = taskInfo;
			mListener = listener;
			mView = view;
			mUIHandler = new Handler(Looper.getMainLooper());
		}

		@Override
		public void postResult(final Object object) {
			if(object instanceof Bitmap){
				final Bitmap bitmap = (Bitmap)object;
				mUIHandler.post(new Runnable() {
					@SuppressWarnings("deprecation")
                    @Override
					public void run() {
						if(mView != null){
							Object tag = mView.getTag();
							if(tag instanceof Task){
								Task taskHolder = (Task)tag;
								taskHolder.isDone = true;
								if(taskHolder.taskInfo.equals(mTaskInfo)){
								    if(mView instanceof ImageView){
	                                    ((ImageView)mView).setImageBitmap(bitmap);
								    }else{
								        mView.setBackgroundDrawable(new BitmapDrawable(bitmap));
								    }
								}
							}
						}
						if(mListener != null){
							mListener.onThumbnailReady(mTaskInfo, mView, bitmap);
						}
					}
				});
			}
		}

		@Override
		public void load() {
			ThumbnailManager videoThumbCacheManager = DKApp.getSingleton(ThumbnailManager.class);
			mResult = videoThumbCacheManager.getThumbnail(mTaskInfo);
		}
	}
}

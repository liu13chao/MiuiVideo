package com.miui.video.model.loader;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.miui.video.DKApp;
import com.miui.video.model.AppEnv;
import com.miui.video.type.LocalMedia;
import com.miui.video.type.LocalMediaList;
import com.miui.video.util.Util;

/**
 *@author tangfuling
 *
 */

public class LocalMediaLoader {
	
	public static final String CAMERA_DIR = "Camera";
	
	private static LocalMediaLoader mLocalMediaStore;
	private ArrayList<LocalMediaList> mLocalMedias = new ArrayList<LocalMediaList>();
	
	private WeakReference<OnLocalMediaLoadListener> mOnLocalMediaLoadListener;
	private AsyncLocalMediaScanTask mAsyncLocalMediaScanTask;
	private static ExecutorService mExecutorService = Executors.newCachedThreadPool();
	private boolean mLoaded;
	
	private LocalMediaLoader() {
		
	}
	
	public static LocalMediaLoader getInstance() {
		if (mLocalMediaStore == null) {
			mLocalMediaStore = new LocalMediaLoader();
		}
		return mLocalMediaStore;
	}
	
	public void getLocalMedias(OnLocalMediaLoadListener listener, boolean bReload) {
		startLocalMediaLoad(listener, bReload);
	}
	
	public void delLocalMediaLists(List<LocalMediaList> localMediaLists) {
		AsyncDelLocalMediaListsTask asyncDelLocalMediasTask = new AsyncDelLocalMediaListsTask(localMediaLists);
		asyncDelLocalMediasTask.executeOnExecutor(mExecutorService);
	}
	
	public void delLocalMedias(List<LocalMedia> localMedias) {
		AsyncDelLocalMediasTask asyncDelLocalMediasTask = new AsyncDelLocalMediasTask(localMedias);
		asyncDelLocalMediasTask.executeOnExecutor(mExecutorService);
	}
	
	//packaged method
	private void startLocalMediaLoad(OnLocalMediaLoadListener onLocalMediaLoadListener, 
	        boolean bReLoad){
		mOnLocalMediaLoadListener = new WeakReference<OnLocalMediaLoadListener>(onLocalMediaLoadListener);
		if ( mLoaded && !bReLoad) {
			notifyLocalMediaDone();
		}
		
		stopLocalMediaLoad();
		mLoaded = false;
		mAsyncLocalMediaScanTask = new AsyncLocalMediaScanTask(DKApp.getAppContext().getContentResolver());
		mAsyncLocalMediaScanTask.executeOnExecutor(mExecutorService);
	}
	
	private void stopLocalMediaLoad() {
		if ( mAsyncLocalMediaScanTask != null) {
			mAsyncLocalMediaScanTask.cancel(true);
		}
	}
	
	private void notifyLocalMediaDone() {
	    WeakReference<OnLocalMediaLoadListener> ref = mOnLocalMediaLoadListener;
	    if(ref != null){
	        OnLocalMediaLoadListener listener = ref.get();
	        if (listener != null) {
	            listener.onLocalMediaDone(mLocalMedias);
	        }
	    }
	}
	
	private void delLocalMediaList(LocalMediaList localMediaList) {
		if(localMediaList == null) {
			return;
		}
		for (int i = 0; i < localMediaList.size(); i++) {
			LocalMedia localMedia = localMediaList.get(i);
			delLocalMedia(localMedia);
		}
	}
	
	private void delLocalMedia(LocalMedia localMedia) {
		if(localMedia == null) {
			return;
		}
		String path = localMedia.getPath();
		if (!Util.isEmpty(path)) {
			File file = new File(path);
			Util.delDir(file);
			
			//delete row from db
			ContentResolver contentResolver = DKApp.getAppContext().getContentResolver();
			String where = MediaStore.Video.Media.DATA + " = ?";
			contentResolver.delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, 
					where, new String[]{path});
		}
	}
	
	//background task
	private class AsyncDelLocalMediaListsTask extends AsyncTask<Void, Void, Void> {

		private List<LocalMediaList> delLocalMediaLists;
		
		public AsyncDelLocalMediaListsTask(List<LocalMediaList> delLocalMediaLists) {
			this.delLocalMediaLists = delLocalMediaLists;
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			if (delLocalMediaLists == null) {
				return null;
			}
			mLocalMedias.removeAll(delLocalMediaLists);
			for (int i = 0; i < delLocalMediaLists.size(); i++) {
				LocalMediaList localMediaList = delLocalMediaLists.get(i);
				delLocalMediaList(localMediaList);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			notifyLocalMediaDone();
		}
	}
	
	private class AsyncDelLocalMediasTask extends AsyncTask<Void, Void, Void> {

		private List<LocalMedia> delLocalMedias;
		
		public AsyncDelLocalMediasTask(List<LocalMedia> delLocalMedias) {
			this.delLocalMedias = delLocalMedias;
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			if (delLocalMedias == null) {
				return null;
			}
			for(Iterator<LocalMediaList> iterator = mLocalMedias.iterator(); iterator.hasNext();) {
				LocalMediaList localMediaList = iterator.next();
				if(localMediaList != null) {
					localMediaList.removeAll(delLocalMedias);
					if(localMediaList.size() == 0) {
						iterator.remove();
					}
				}
			}
			for (int i = 0; i < delLocalMedias.size(); i++) {
				LocalMedia localMedia = delLocalMedias.get(i);
				delLocalMedia(localMedia);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			notifyLocalMediaDone();
		}
	}
	
	private class AsyncLocalMediaScanTask extends AsyncTask<Void, Void, Void> {
		private ContentResolver  contentResolver = null;
		
		public AsyncLocalMediaScanTask(ContentResolver contentResolver) {
			this.contentResolver = contentResolver;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			loadLocalMedia();
			return null;
		}
		
		private void  loadLocalMedia() {
//			String dcimRootPath = DKApp.getSingleton(AppEnv.class).getDcimDir();

//			StringBuilder strBuilder = new StringBuilder();
//			if(dcimRootPath != null) {
//				strBuilder.append(dcimRootPath);
//				strBuilder.append(File.separator);
//				strBuilder.append(CAMERA_DIR);
//			}
//			String cameraPath = strBuilder.toString();
			
	        String[] projection = { MediaStore.Video.Media._ID,  
	                MediaStore.Video.Media.DATA, 
	                MediaStore.Video.Media.TITLE,  
	                MediaStore.Video.Media.MIME_TYPE,  
	                MediaStore.Video.Media.DISPLAY_NAME,
	                MediaStore.Video.Media.SIZE,
	                MediaStore.Video.Media.DURATION,
	                MediaStore.Video.Media.DATE_MODIFIED,
	                MediaStore.Video.Media.BUCKET_ID,
	                MediaStore.Video.Media.BUCKET_DISPLAY_NAME};  
	        String orderByClause =  MediaStore.Video.Media.BUCKET_DISPLAY_NAME + " ASC, " + MediaStore.Video.Media.DEFAULT_SORT_ORDER + " ASC" ;
	        
	        Cursor cursor = null;
	        try {
	        	cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, orderByClause);	         
		        if (isCancelled()) {
		        	return;
		        }
		        if (cursor != null) {
		        	ArrayList<LocalMediaList> localMediaArrayList = new ArrayList<LocalMediaList>();
		        	LocalMediaList localMediaList = null;
		        	
		        	int index = 0;
		        	String lastBucketName = null;
		        	while (cursor.moveToNext()) {
						if (isCancelled()) {
							return;
						}
		        		LocalMedia  localMedia = new LocalMedia();
		        		index = cursor.getColumnIndex(MediaStore.Video.Media._ID);
		        		index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
		        		String mediaPath = cursor.getString(index);
		        		localMedia.mediaPath = mediaPath;
						String parentPath = "";
						if(mediaPath.lastIndexOf(File.separator) != -1) {
							parentPath = mediaPath.substring(0, mediaPath.lastIndexOf(File.separator));
						}
		        	
		        		index = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
		        		localMedia.mediaTitle = cursor.getString(index);
		        		
		        		index = cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE); 
		        		localMedia.mediaMimeType = cursor.getString(index);
		        		
		        		index = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
		        		localMedia.displayName = cursor.getString(index);
	
		        		if (Util.isEmpty(localMedia.displayName)) {
		        			localMedia.displayName = mediaPath.substring(mediaPath.lastIndexOf('/') + 1);
		        		}
		        		
		        		index = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
		        		localMedia.mediaSize = cursor.getInt(index);
		        		
		        		index = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
		        		localMedia.mediaDuration = cursor.getLong(index);
		        		if (localMedia.mediaDuration < 0)
		        			localMedia.mediaDuration = 0;
		        		
		        		index = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED);
		        		localMedia.mediaLastModified = cursor.getLong(index);
		        		
		        		index = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID);
		        		localMedia.bucketId = cursor.getString(index);
		        		
		        		index = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
		        		String curBucketName = cursor.getString(index);
		        		localMedia.bucketName = curBucketName;
		        		
		        		boolean existInCamera;
//		        		= parentPath.equals(cameraPath);
//		        		if (DKApp.getSingleton(DeviceInfo.class).getPlatform() == ApiConfig.PLATFORM_MI_RED_TWO
//		        				||DKApp.getSingleton(DeviceInfo.class).getPlatform() == ApiConfig.PLATFORM_PAD_N7) {
							existInCamera = parentPath.startsWith(DKApp.getSingleton(AppEnv.class).getInnerCamara())
											|| parentPath.startsWith(DKApp.getSingleton(AppEnv.class).getExternalCamara());
//						}
		        		
		        		if (Util.isEmpty(lastBucketName) || Util.isEmpty(curBucketName)
		        				|| !lastBucketName.equals(curBucketName) || localMedia.existsInSdRootDir()) {
		        			localMediaList = new LocalMediaList();
		        			if( existInCamera) {
		        				localMediaList.setIsCamera();
		        				localMediaArrayList.add(0, localMediaList);
		        			} else {
		        				localMediaArrayList.add(localMediaList);
		        			}
		        		}
		        		
		        		if (localMediaList != null) {
		        			localMediaList.add(localMedia);
		        		}
		        		
		        		lastBucketName = curBucketName;
		        	}
		        	cursor.close();
		        	
		        	mLocalMedias = localMediaArrayList;
		        }
	        } catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(cursor != null){
					cursor.close();
				}
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mLoaded = true;
			notifyLocalMediaDone();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onCancelled(Void result) {
			super.onCancelled(result);
			mLoaded = false;
		}
	}
	
	public static interface OnLocalMediaLoadListener {
		public void onLocalMediaDone(ArrayList<LocalMediaList> localMedias);
	}
}

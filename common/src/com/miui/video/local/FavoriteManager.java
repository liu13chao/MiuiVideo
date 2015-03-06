/**
 *  Copyright(c) 2013 XiaoMi TV Group
 *    
 *  FavoriteManager.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2013-12-01
 */
package com.miui.video.local;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import com.miui.video.DKApp;
import com.miui.video.api.DKApi;
import com.miui.video.model.UserManager;
import com.miui.video.response.GetMyFavoriteResponse;
import com.miui.video.statistic.MyFavoriteStatisticInfo;
import com.miui.video.type.FavoriteItem;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.DKLog;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 * @author tianli
 * 
 */
public class FavoriteManager extends SyncManager {
	
	public static final String TAG = "FavoriteManager";

	// Data Store
	private FavoriteStore mStore;
	
	private UserManager mUserManager;
	
	// Listeners
	private List<OnFavoriteChangedListener> mListeners = new ArrayList<OnFavoriteChangedListener>();
	
	private List<Favorite> mFavList = new ArrayList<Favorite>();
	private List<Favorite> mUIFavList = new ArrayList<Favorite>();
	
	private static HandlerThread mSyncThread;
	private static Handler mHandler;
	
	static {
		mSyncThread = new HandlerThread("FavoriteThread");
		mSyncThread.start();
		mHandler = new Handler(mSyncThread.getLooper());
	}

	@Override
    public void init(Context context) {
        super.init(context);
        mUserManager = DKApp.getSingleton(UserManager.class);
        mStore = new FavoriteStore();
    }

    @Override
	protected void scheduleBackgroundTask(Runnable task) {
		mHandler.post(task);
	}
		
	//public method
	public void addListener(OnFavoriteChangedListener listener){
		if(listener != null && !mListeners.contains(listener)){
			mListeners.add(listener);
		}
	}
	
	public void removeListener(OnFavoriteChangedListener listener){
		if(listener != null && mListeners.contains(listener)){
			mListeners.remove(listener);
		}
	}
	
	public List<Favorite> getFavoriteList(){
		return mUIFavList;
	}
	
	public void loadFavorite() {
		scheduleBackgroundTask(mLoadFavorites);
		syncFavorite();
	}
	
	public boolean isFavorite(MediaInfo mediaInfo) {
		if(mediaInfo == null) {
			return false;
		}
		for(int i = 0; i < mUIFavList.size(); i++) {
			Favorite favorite = mUIFavList.get(i);
			if(favorite != null) {
				String mediaId = mediaInfo.mediaid +"";
				if(favorite.getId().equals(mediaId)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void addFavorite(MediaInfo mediaInfo){
		DKLog.d(TAG, "add favorite");
		List<MediaInfo> list = new ArrayList<MediaInfo>();
		list.add(mediaInfo);
		scheduleBackgroundTask(new AddFavorite(list));
	}

	public void delFavorite(List<MediaInfo> mediaList){
		DKLog.d(TAG, "del favorite");
		if(mediaList == null || mediaList.size() == 0) {
			return;
		}
		List<MediaInfo> list = new ArrayList<MediaInfo>();
		list.addAll(mediaList);
		scheduleBackgroundTask(new DelFavorite(list));
	}
	
	public void delFavorite(MediaInfo mediaInfo){
		List<MediaInfo> list = new ArrayList<MediaInfo>();
		list.add(mediaInfo);
		scheduleBackgroundTask(new DelFavorite(list));
	}
	
	public void checkAccount(){
		if(mUserManager.isAccountChanged()){
			scheduleBackgroundTask(mLoadFavorites);
		}
	}
	
	//packaged mthod
	private void saveFavorites() {
		DKLog.d(TAG, "save favorite");
		mStore.saveFavorites(mFavList, getAccount()); 
		scheduleUITask(mFavoriteNotify);
	}
	
	private int[] buildIds(List<OnlineFavorite> addList){
		int[] ids = new int[addList.size()];
		for(int i = 0; i < addList.size(); i++){
			ids[i] = addList.get(i).getMediaInfo().mediaid;
		}
		return ids;
	}
	
	private void notifyFavoriteLoaded(){
		mUIFavList.clear();
		mUIFavList.addAll(mStore.getUIFavoriteList());
		for(OnFavoriteChangedListener listener : mListeners){
			if(listener != null){
				listener.onFavoritesChanged(mUIFavList);
			}
		}
	}
	
	private void syncFavorite() {
		if(!mUserManager.needAuthenticate()){
			scheduleBackgroundTask(mSyncFavorites);
		}
	}
	
	private void addFavoriteToServer(List<OnlineFavorite> addList) {
		if(mUserManager.needAuthenticate() || addList == null || addList.size() == 0) {
			return;
		}
		for(int i = 0; i < addList.size(); i++){
			OnlineFavorite onlineFavorite = addList.get(i);
			if(onlineFavorite != null) {
				FavoriteItem item = new FavoriteItem();
				item.mediaInfo = onlineFavorite.getMediaInfo();
				item.utime = onlineFavorite.getCreateTime();
				DKApi.setMyFavoriteMedia(item, prepareSetFavoriteStatistic(item), null);
			}
		}
	}
	
	private void delFavoriteFromServer(List<OnlineFavorite> delList) {
		if(mUserManager.needAuthenticate() || delList == null || delList.size() == 0) {
			return;
		}
		DKApi.deleteMyFavoriteMedia(buildIds(delList), 
				prepareDelFavoriteStatistic(delList), null);
	}
	
	//background task
	private Runnable mLoadFavorites = new Runnable() {
		@Override
		public void run() {
			DKLog.d(TAG, "load favorite start");
			mStore.loadFavorite(getAccount());
			mFavList.clear();
			mFavList.addAll(mStore.getFavoriteList());
			
			scheduleUITask(mFavoriteNotify);
			DKLog.d(TAG, "load favorite end");
		}
	};
	
	private Runnable mSyncFavorites = new Runnable() {
		@Override
		public void run() {
			DKLog.d(TAG, "sync favorite start");
			DKApi.getMyFavoriteMedia(mObserver);
		}
	};
	
	private class AddFavorite implements Runnable{
		private List<MediaInfo> mMediaList = null;
		public AddFavorite(List<MediaInfo> mediaList){
			mMediaList = mediaList;
		}
		@Override
		public void run() {
			DKLog.d(TAG, "add favorite start");
			if(mMediaList != null){
				addOnline(mMediaList);
			}
			DKLog.d(TAG, "add favorite end");
		}
		private void addOnline(List<MediaInfo> mediaList){
			DKLog.d(TAG, "add online");
			List<OnlineFavorite> addList = new ArrayList<OnlineFavorite>();
			for(MediaInfo mediaInfo : mediaList){
				OnlineFavorite fav = new OnlineFavorite(mediaInfo);
				fav.mStatus = Favorite.STATUS_ADDED;
				fav.mCreateTime = System.currentTimeMillis();
				int pos = mFavList.indexOf(fav);
				if(pos >= 0){
					Favorite old = mFavList.get(pos);
					if(old.getStatus() != Favorite.STATUS_SYNC){
						old.setStatus(Favorite.STATUS_ADDED);
						old.setCreateTime(System.currentTimeMillis());
						addList.add(fav);
					}
				}else{
					mFavList.add(0, fav);
					addList.add(fav);
				}
			}
			saveFavorites();
			
			addFavoriteToServer(addList);
		}
	};
	
	private class DelFavorite implements Runnable{
		private List<MediaInfo> mMediaList = null;
		public DelFavorite(List<MediaInfo> mediaList){
			mMediaList = mediaList;
		}
		@Override
		public void run() {
			DKLog.d(TAG, "del favorite start");
			if(mMediaList != null){
				delOnline(mMediaList);
			}
			DKLog.d(TAG, "del favorite end");
		}
		private void delOnline(List<MediaInfo> mediaList){
			List<OnlineFavorite> delList = new ArrayList<OnlineFavorite>();
			for(MediaInfo mediaInfo : mediaList){
				OnlineFavorite fav = new OnlineFavorite(mediaInfo);
				delList.add(fav);
				fav.mStatus = Favorite.STATUS_DELETED;
				int pos = mFavList.indexOf(fav);
				if(pos >= 0){
					Favorite old = mFavList.get(pos);
					if(old.getStatus() != Favorite.STATUS_DELETED){
						old.setStatus(Favorite.STATUS_DELETED);
					}
				}
			}
			saveFavorites();
			
			delFavoriteFromServer(delList);
		}
	};
	
	private class MergeFavoriteRunnable implements Runnable {

		private FavoriteItem[] favList;
		
		public MergeFavoriteRunnable(FavoriteItem[] favList) {
			this.favList = favList;
		}
		
		@Override
		public void run() {
			List<Favorite> newFavList = new ArrayList<Favorite>();
			List<OnlineFavorite> delList = new ArrayList<OnlineFavorite>();
			List<OnlineFavorite> addList = new ArrayList<OnlineFavorite>();
			
			DKLog.d(TAG, "merge favorite");
			//merge server to local
			if(favList != null) {
				for(FavoriteItem item : favList){
				    if(item == null || item.mediaInfo == null){
				        continue;
				    }
					OnlineFavorite fav = new OnlineFavorite(item.mediaInfo);
					fav.mCreateTime = item.utime;
					fav.mStatus = Favorite.STATUS_SYNC;
					newFavList.add(fav);
				}
			}
			
			for(Favorite fav : mFavList){
				if(fav instanceof OnlineFavorite){
					int index = newFavList.indexOf(fav);
					if(index >= 0){
						if(fav.isDeletedLocally()){
							delList.add((OnlineFavorite)fav);
							newFavList.set(index, fav);
						}
					} else{
						if(fav.getStatus() == Favorite.STATUS_ADDED){
							addList.add((OnlineFavorite)fav);
							newFavList.add(fav);
							fav.setStatus(Favorite.STATUS_ADDED);
						}
					}
				}
			}
			
			mFavList = newFavList;
			saveFavorites();
			
			addFavoriteToServer(addList);
			delFavoriteFromServer(delList);
		}
	}
	
	//UI task
	private Runnable mFavoriteNotify = new Runnable(){
		@Override
		public void run() {
			notifyFavoriteLoaded();
		}
	};
	
	//data callback
	private Observer mObserver = new Observer() {
		
		@Override
		public void onRequestCompleted(ServiceRequest request,
				ServiceResponse response) {
			DKLog.d(TAG, "sync favorite complete");
			if(response instanceof GetMyFavoriteResponse){
				if(response.isSuccessful()){
					GetMyFavoriteResponse favResponse = (GetMyFavoriteResponse)response;
					favResponse.completeData();
					scheduleBackgroundTask(new MergeFavoriteRunnable(favResponse.data));
				}
			}
		}
		
		@Override
		public void onProgressUpdate(ServiceRequest request, int progress) {
		
		}
	};
	
	//self def class
	public static interface OnFavoriteChangedListener{
		public void onFavoritesChanged(List<Favorite> favList);
	}
	
	//statistic
	private String prepareSetFavoriteStatistic(FavoriteItem favoriteItem) {
		if(favoriteItem != null) {
			MyFavoriteStatisticInfo statisticInfo = new MyFavoriteStatisticInfo();
			if(favoriteItem.mediaInfo != null){
		         statisticInfo.mediaId = favoriteItem.mediaInfo.mediaid;
			}
			statisticInfo.action = 1;
			return statisticInfo.formatToJson();
		}
		return "";
	}
	
	private String prepareDelFavoriteStatistic(List<OnlineFavorite> delList) {
		if(delList != null) {
			MyFavoriteStatisticInfo statisticInfo = new MyFavoriteStatisticInfo();
			statisticInfo.mediaId = -1;
			statisticInfo.action = -1;
			if(delList.size() == 1) {
				OnlineFavorite onlineFavorite = delList.get(0);
				if(onlineFavorite != null && onlineFavorite.getMediaInfo() != null) {
					statisticInfo.mediaId = onlineFavorite.getMediaInfo().mediaid;
				}
			}
			return statisticInfo.formatToJson();
		}
		return "";
	}
}

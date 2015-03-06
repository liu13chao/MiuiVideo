/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   PlayHistoryLoader.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-6
 */

package com.miui.videoplayer.model;

import android.net.Uri;
import android.util.Log;

import com.miui.videoplayer.framework.history.PlayHistoryManager;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;

/**
 * @author tianli
 *
 */
public abstract class PlayHistoryVistor {
	
	protected BaseUri mUriInfo;
	
	public PlayHistoryVistor(BaseUri uri){
		mUriInfo = uri;
	}
	
	public static PlayHistoryVistor create(BaseUri uri){
		if(uri instanceof OnlineUri){
			return new OnlineVistor(uri);
		}else if(uri instanceof LocalUri){
			return new LocalUriVistor(uri); 
		}
		return null;
	}
	
	public int parseInt(String string){
		int value = 0;
		try{
			value = Integer.valueOf(string);
		}catch (Exception e) {
		}
		return value;
	}
	
	public abstract PlayHistoryEntry visit(PlayHistoryManager mgr);
	
	public static class OnlineVistor extends PlayHistoryVistor{
		public static final String TAG = "OnlineVistor";

		public OnlineVistor(BaseUri uri) {
			super(uri);
		}
		@Override
		public PlayHistoryEntry visit(PlayHistoryManager mgr) {
			if(mUriInfo == null || mUriInfo.getUri() == null){
				return null;
			}
			OnlineUri onlineInfo = (OnlineUri)mUriInfo;
			Uri uri = onlineInfo.getUri();
			PlayHistoryEntry entry = mgr.findPlayHistoryByOnlineUri(onlineInfo);
			if (entry != null) {
				int oldCi = parseInt(entry.getMediaCi());
				if (oldCi != onlineInfo.getCi()) {
					entry.setPosition(0);
				}
			Log.i(TAG, "mUri: " + uri + " mHtml5: " + onlineInfo + " pos:" + 
					entry.getPosition() + ", ci: " + onlineInfo.getCi());
			}
			return entry;
		}
	}
	
	public static class LocalUriVistor extends PlayHistoryVistor{
		public static final String TAG = "OnlineVistor";

		public LocalUriVistor(BaseUri uri) {
			super(uri);
		}
		@Override
		public PlayHistoryEntry visit(PlayHistoryManager mgr) {
			if(mUriInfo == null || mUriInfo.getUri() == null){
				return null;
			}
			LocalUri localInfo = (LocalUri)mUriInfo;
			Uri uri = localInfo.getUri();
			PlayHistoryEntry entry = mgr.findPlayHistory(uri);
			if(entry != null){
				Log.i(TAG, "mUri: " + uri +  " pos:" +  entry.getPosition());
			}
			return entry;
		}
	}

	
}

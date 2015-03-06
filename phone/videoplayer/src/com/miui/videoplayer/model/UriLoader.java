/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   SeriesLoader.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-18
 */

package com.miui.videoplayer.model;

import java.util.List;

/**
 * @author tianli
 *
 */
public abstract class UriLoader {
	
	public final static int ERROR_NO_EPISODE = 0;
	public final static int ERROR_NO_URL = 1;
	
	protected BaseUri mPlayingUri;
	
//	protected OnUriLoadedListener mUriListener;
	
	public void setPlayingUri(BaseUri playingUri) {
		this.mPlayingUri = playingUri;
	}
	
	public BaseUri getPlayingUri() {
		return mPlayingUri;
	}
	
	public abstract String getTitle();

	public abstract void loadEpisode(int episode, OnUriLoadedListener uriListener);
	
	public abstract boolean hasNext();
	
	public abstract boolean canSelectCi();
	
	public abstract int next(OnUriLoadedListener uriListener);
	
	public abstract void cancel();
	
	public abstract List<Episode> getEpisodeList();
	
	public abstract String getVideoNameOfCi(int ci);
//	public void setUriListener(OnUriLoadedListener uriListener) {
//		this.mUriListener = uriListener;
//	}

	public static interface OnUriLoadedListener{
		public void onUriLoaded(int episode, BaseUri uri);
		public void onUriLoadError(int errorCode);
	}
	
}

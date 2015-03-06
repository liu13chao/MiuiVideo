/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   UriArrayLoader.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-24
 */

package com.miui.videoplayer.model;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.text.TextUtils;

/**
 * @author tianli
 *
 */
public class UriArrayLoader extends UriLoader {

	private boolean mIsRepeated = true;
	
	private LocalUri[] mUriItems;
	
	public UriArrayLoader(String[] uriList, String[] uriTitle){
		if(uriList != null){
			mUriItems = new LocalUri[uriList.length];
			for(int i = 0; i < uriList.length; i++){
				String uri = uriList[i];
				String title = null;
				if(uriTitle != null && i < uriTitle.length){
					title = uriTitle[i];
				}
				if(TextUtils.isEmpty(title)){
					int pos = uri.lastIndexOf("/");
					if(pos >= 0 && pos < uri.length() - 1){
						title = uri.substring(pos + 1,  uri.length());
					}
				}
				mUriItems[i] = new LocalUri(Uri.parse(uri), title, i);
			}
		}
	}
	
	@Override
	public String getTitle() {
		return mPlayingUri.getTitle();
	}
	
	@Override
	public void loadEpisode(int episode, OnUriLoadedListener uriListener) {
		if(mUriItems != null && episode >= 0 && episode < mUriItems.length){
			if(uriListener != null){
				uriListener.onUriLoaded(episode, mUriItems[episode]);
			}
		}else{
			if(uriListener != null){
				uriListener.onUriLoadError(ERROR_NO_EPISODE);
			}
		}
	}

	@Override
	public int next(OnUriLoadedListener uriListener) {
		if(mPlayingUri != null){
			if(mUriItems != null && mUriItems.length > 0){
				int ci = Math.max(0, mPlayingUri.getCi() + 1) % mUriItems.length;
				loadEpisode(ci, uriListener);
				return ci;
			}
		}
		return -1;
	}
	
	@Override
	public void cancel() {
	}

	@Override
	public boolean hasNext() {
		if(mPlayingUri != null){
			int ci = mPlayingUri.getCi();
			if(mUriItems != null && mUriItems.length > 0){
				if(isRepeated()){
					return true;
				}else if(ci + 1 >= 0 && ci + 1 < mUriItems.length){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canSelectCi() {
		return mUriItems != null && mUriItems.length > 1;
	}

	@Override
	public List<Episode> getEpisodeList() {
		ArrayList<Episode> list = new ArrayList<Episode>();
		for(int i = 0; mUriItems != null && i < mUriItems.length; i++){
			Episode e = new Episode();
			e.setCi(i);
			e.setName(mUriItems[i].getTitle());
			list.add(e);
		}
		return list;
	}

	public boolean isRepeated() {
		return mIsRepeated;
	}

	public void setRepeated(boolean isRepeated) {
		this.mIsRepeated = isRepeated;
	}

	@Override
	public String getVideoNameOfCi(int ci) {
		LocalUri item = mUriItems[ci];
		if(item != null && !TextUtils.isEmpty(item.getTitle())){
			return item.getTitle();
		}
		return "";
	}
}

/**
 *   Copyright(c) 2013 DuoKan TV Group
 *    
 *   DirMediaItem.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2013-1-18
 */

package com.miui.video.storage;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 *@author xuanmingliu
 *
 */

public abstract class DirMediaItem extends MediaItem{
	
    private static final long serialVersionUID = 1L;

    protected List<MediaItem>  mediaItemList;
	
	public DirMediaItem(Context context) {
		super(context);
		mediaItemList = new ArrayList<MediaItem>();
	}

	public DirMediaItem(Context context, String name) {
		super(context, name, true);
		mediaItemList = new ArrayList<MediaItem>();
	}
 	
	public List<MediaItem> getMediaItemList() {
		return mediaItemList;
	}
	
	public int getMediaItemListSize() {
		return mediaItemList.size();
	}
	
	public void addMediaItem(MediaItem mediaItem) {
		mediaItemList.add(mediaItem);
	}
	
	public void addMediaItemList(List<MediaItem>  mediaItemList) {
		if(mediaItemList == null || mediaItemList.size() == 0) {
			return;
		}
		this.mediaItemList.addAll(mediaItemList);
	}

    @Override
    public String getMediaStatus() {
        return "";
    }
	
}



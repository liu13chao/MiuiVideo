package com.miui.video.storage;

import java.util.List;

import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.AudioItem;
import org.teleal.cling.support.model.item.ImageItem;
import org.teleal.cling.support.model.item.Item;
import org.teleal.cling.support.model.item.VideoItem;

import android.content.Context;


public class DLNAMediaItem extends MediaItem {
	
    private static final long serialVersionUID = 1L;

    public static final String TAG = "DLNAMediaItem";
	public List<String> mParendIdList;
	
	protected final DIDLObject mDidlObject;
	
	public DLNAMediaItem(Context context, DIDLObject didlObject) {
		super(context);
		mDidlObject = didlObject;
		mName = mDidlObject.getTitle();
		mPath = mDidlObject.getId();
		
//		MILog.i(TAG, "mDidlObject.getId()!: " + mPath);
		
		mParentPath = mDidlObject.getParentID();
		mIsDirectory = false;
		
		//some dlna server like oShare will pass mp4 file as audio
		mMediaType = getTypeByFilenameExt(getMediaUrl());
		
		if(mMediaType == MediaType.Unknown){
			if (mDidlObject instanceof VideoItem) {
//				MILog.i(TAG, "is vidoe");
				mMediaType = MediaType.Video;
			}else if (mDidlObject instanceof AudioItem) {
//				MILog.i(TAG, "is audio");
				mMediaType = MediaType.Audio;
			}else if (mDidlObject instanceof ImageItem) {
//				MILog.i(TAG, "is image");
				mMediaType = MediaType.Image;
			}else if (mDidlObject instanceof Container) {
				mIsDirectory = true;
			}
		}
		
	}
	
	@Override
	public String getMediaUrl() {
		if (mDidlObject instanceof Item) {
			List<Res> resources = mDidlObject.getResources();
			if(resources.size()>0) {
				return resources.get(0).getValue();
			}
		}
		return null;
	}

	public void addParentList(List<String> parentList) {
		mParendIdList = parentList;
	}
	
	public List<String> getParentList(){
		return mParendIdList;
	}
}

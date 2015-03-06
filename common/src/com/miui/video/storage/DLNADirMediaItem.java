/**
 *   Copyright(c) 2013 DuoKan TV Group
 *    
 *   DLNADirMediaItem.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2013-1-16
 */

package com.miui.video.storage;

import org.teleal.cling.support.model.DIDLObject;

import android.content.Context;

/**
 *@author xuanmingliu
 *
 */

public class DLNADirMediaItem extends DirMediaItem{

    private static final long serialVersionUID = 1L;

    private final DIDLObject mDidlObject;
	private final String mParentId;
	
	public DLNADirMediaItem(Context context, DIDLObject didlObject) {
		super(context);
		mDidlObject = didlObject;
		mName = mDidlObject.getTitle();
		mPath = mDidlObject.getId();
		mIsDirectory = true;
		mParentId = didlObject.getParentID();
	}

	public String getParentId() {
		return mParentId;
	}
}
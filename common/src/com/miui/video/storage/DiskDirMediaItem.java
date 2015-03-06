/**
 *   Copyright(c) 2013 DuoKan TV Group
 *    
 *   DiskDirMediaItem.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2013-1-16
 */

package com.miui.video.storage;

import java.io.File;

import android.content.Context;

/**
 *@author xuanmingliu
 *
 */

public class DiskDirMediaItem extends DirMediaItem{
	
    private static final long serialVersionUID = 1L;

    private final File mFile;
	private final String ignoreFileNameList[] = {
	        "$Recycle.Bin",
	        "System Volume Information",
	        "RECYCLER"
		};
	
	public DiskDirMediaItem(Context context, File file) {
		super(context);
		mFile = file;
		mPath = mFile.getPath();
		mName = mFile.getName();
		mIsDirectory = true;
	}
	
	@Override
	public boolean isApply(){
		for (String ignoreFileName : ignoreFileNameList) {
	        if(mName.equalsIgnoreCase(ignoreFileName)) {
	            return false;
	        }
	    }
		String NO_MEDIA = ".nomedia";
		File nomedia = new File(mPath + "/" + NO_MEDIA);
		if(nomedia.exists()){
			return false;
		}
		return super.isApply();
	}
}
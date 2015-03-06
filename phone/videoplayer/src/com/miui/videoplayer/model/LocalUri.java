/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   LocalUri.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-30
 */

package com.miui.videoplayer.model;

import android.net.Uri;

/**
 * @author tianli
 *
 */
public class LocalUri extends BaseUri {
	
	private Uri mPath;
	private String mTitle;
	private int mCi;

	public LocalUri(Uri uri, String title, int ci){
//		mPath = Uri.parse(path);
		mPath = uri;
		mTitle = title;
		mCi = ci;
	}
	@Override
	public Uri getUri() {
		return mPath;
	}
	
	public String getTitle(){
		return mTitle;
	}
	public int getCi() {
		return mCi;
	}
}

/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OnlineUri.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-1
 */

package com.miui.videoplayer.model;

import android.net.Uri;

/**
 * @author tianli
 *
 */
public class OnlineUri extends BaseUri {

	private int mMediaId;
	private int mCi;
	private int mSource;
	private Uri mUri;
	private int mResolution;
	private String mHtml5;
	private String mTitle;
	private String mSdkInfo;
	private boolean mSdkdisable;
	private int mVideoType;
	private String mPosterUrl;
	private int mPlayType;
	public OnlineUri(int mediaId, int ci, String html5, String title, int source, int resolution, String sdkinfo, boolean sdkdisable, Uri uri, int videoType){
		init(mediaId, ci, html5, title, source, resolution, sdkinfo, sdkdisable, uri, videoType);
	}
	
	
	public OnlineUri(int mediaId, int ci, String html5, String title, int source, int resolution, String sdkinfo, boolean sdkdisable, Uri uri, int videoType, String posterUrl){
		init(mediaId, ci, html5, title, source, resolution, sdkinfo, sdkdisable, uri, videoType);
		mPosterUrl = posterUrl;
	}
	
	public void init(int mediaId, int ci, String html5, String title, int source, int resolution, String sdkinfo, boolean sdkdisable, Uri uri, int videoType){
		mMediaId = mediaId;
		mCi = ci;
		mTitle = title;
		mUri = uri;
		mResolution = resolution;
		mSource = source;
		mSdkInfo = sdkinfo;
		mSdkdisable = sdkdisable;
		mHtml5 = html5;
		mVideoType = videoType;
	}
	
	public OnlineUri(int mediaId, int ci, String html5, String title, int source, int resolution, String sdkinfo, Uri uri){
		mMediaId = mediaId;
		mCi = ci;
		mTitle = title;
		mUri = uri;
		mResolution = resolution;
		mSource = source;
		mSdkInfo = sdkinfo;
		mHtml5 = html5;
	}
	
	@Override
	public Uri getUri() {
		return mUri;
	}

	public boolean getSdkdisable() {
		return mSdkdisable;
	}	
	public int getMediaId() {
		return mMediaId;
	}

	public int getCi() {
		return mCi;
	}

	public int getSource() {
		return mSource;
	}

	public int getResolution() {
		return mResolution;
	}

	@Override
	public String getTitle() {
		return mTitle;
	}

	public String getPosterUrl() {
		return mPosterUrl;
	}

	public String getHtml5() {
		return mHtml5;
	}
	
	public String getSdkInfo() {
		return mSdkInfo;
	}
	public int getVideoType() {
		return mVideoType;
	}
	
	public void setPlayType(int playtype){
		mPlayType = playtype;
	}
	
	public int getPlayType(){
		return mPlayType;
	}
	
	@Override
	public String toString() {
		return "mMediaId = " + mMediaId + 
				", mCi = " + mCi + 
				", mTitle = " + mTitle + 
				", uri = " + mUri + 
				", mResolution = " + mResolution + 
				", source = " + mSource + 
				", posterurl = " + mPosterUrl + 
				", mVideoType = " + mVideoType +
				", mSdkInfo = " + mSdkInfo + 
				", mSdkdisable = " + mSdkdisable + 
				", html5 = " + mHtml5;
	}
	
	
}

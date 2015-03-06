/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   PlayUrlLoader.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-8
 */

package com.miui.videoplayer.model;

import java.net.URLEncoder;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.miui.videoplayer.common.Constants;
import com.miui.videoplayer.model.MediaUrlInfo.UrlInfo;

/**
 * @author tianli
 *
 */
public class PlayUrlLoader extends AsyncTask<Void, Void, String>{
	
	public static final String TAG = "PlayUrlLoader";
//	private String mHtml5;
//	private int mSource;
	private Context mContext;
	private int mMediaId;
	private int mCi;
//	private String mPlayUrl;
//	private int mResolution;
	private UrlInfo mUrlInfo;
	
	private PlayUrlListener mPlayUrlListener = null;
	
	public PlayUrlLoader(Context context, int mediaId, int ci, UrlInfo urlInfo, PlayUrlListener listener){
		mUrlInfo = urlInfo;
//		mHtml5 = html5;
//		mSource = source;
		mContext = context;
		mMediaId = mediaId;
		mCi = ci;
//		mResolution = resolution;
		mPlayUrlListener = listener;
	} 
	
	public void start(){
		executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	protected String doInBackground(Void... arg0) {
		Log.i(TAG, "doInBackground");
		ContentResolver cr = mContext.getContentResolver();
		Cursor cursor = null;
		try {
			String mediaUrl = mUrlInfo.mediaUrl;
			try{
				 mediaUrl = URLEncoder.encode(mediaUrl, "utf-8");
			}catch (Exception e) {
			}
			String contentUriStr = Constants.CONTENT_MEDIA_PLAY_URL.toString() + "?"
								+ Constants.MEDIA_HTML5_URL + "=" + mediaUrl + "&"
								+ Constants.MEDIA_SOURCE + "=" + mUrlInfo.mediaSource;
			Uri contentUri = Uri.parse(contentUriStr);
			Log.d(TAG, "query contentUri: " + contentUri.toString());
			cursor = cr.query(contentUri, null,null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				String playUrl  = cursor.getString(cursor.getColumnIndex(
						Constants.MEDIA_PLAY_URL));
				Log.d(TAG, "media play url " + playUrl);
				return playUrl;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		mUrlInfo.playUrl = result;
		if(mPlayUrlListener != null){
			mPlayUrlListener.onPlayUrl(mMediaId, mCi, mUrlInfo, result);
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		mPlayUrlListener = null;
	}

	@Override
	protected void onCancelled(String result) {
		super.onCancelled(result);
		mPlayUrlListener = null;
	}

	public static interface PlayUrlListener{
		public void onPlayUrl(int mediaId, int ci, UrlInfo urlInfo, String playUrl);
	}

}

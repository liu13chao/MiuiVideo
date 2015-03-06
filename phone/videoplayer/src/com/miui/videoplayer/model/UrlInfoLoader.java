/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   UrlInfoLoader.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-7
 */

package com.miui.videoplayer.model;

import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.miui.videoplayer.common.Constants;

/**
 * @author tianli
 *
 */
public class UrlInfoLoader extends AsyncTask<Void, Void, Void> {
	
	public static final String TAG = "UrlInfoLoader";
	
	private int mCi;
	private int mMediaId;
	private Context mContext;
	private UrlInfoListener mListener;
	
	private MediaUrlInfo mMediaUrlInfo;
	
	public UrlInfoLoader(Context context, int mediaId, int ci, UrlInfoListener listener) {
		mContext = context;
		mCi = ci;
		mMediaId = mediaId;
		mListener = listener;
	}
	
	public void start(){
		executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		Log.i(TAG, "AsyncLoadUriTask doInBackground");
		ContentResolver cr = mContext.getContentResolver();
		Cursor cursor = null;
		try {
			String contentUriStr = Constants.CONTENT_MEDIA_URL_INFO.toString() + "?"
								+ Constants.MEDIA_ID + "=" + mMediaId + "&"
								+ Constants.CURRENT_EPISODE + "=" + mCi;
			Uri contentUri = Uri.parse(contentUriStr);
			Log.d(TAG, "query contentUri: " + contentUri.toString());
			cursor = cr.query(contentUri, null, Constants.MEDIA_ID + "=? and " + Constants.CURRENT_EPISODE + "=?",
					new String[]{String.valueOf(mMediaId), String.valueOf(mCi)}, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				String urlInfo  = cursor.getString(cursor.getColumnIndex(
						Constants.MEDIA_URL_INFO));
				String localPath = cursor.getString(cursor.getColumnIndex(
						Constants.LOCAL_PATH));
				mMediaUrlInfo = new MediaUrlInfo(new JSONObject(urlInfo), localPath);
				Log.d(TAG, "media url info " + urlInfo);
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
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if(mListener != null){
			mListener.onUrlInfoLoaded(mMediaId, mCi, mMediaUrlInfo);
		}
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
		mListener = null;
	}

	@Override
	protected void onCancelled(Void result) {
		super.onCancelled(result);
		mListener = null;
	}

	public void setListener(UrlInfoListener listener) {
		this.mListener = listener;
	}

	public static interface UrlInfoListener{
		public void onUrlInfoLoaded(int mediaId, int ci, MediaUrlInfo urlInfo);
	}

}

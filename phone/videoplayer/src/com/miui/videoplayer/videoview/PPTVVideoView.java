/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   PPTVVideoView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-8-31
 */

package com.miui.videoplayer.videoview;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.miui.videoplayer.ads.AdBean;
import com.miui.videoplayer.ads.AdUtils;
import com.miui.videoplayer.ads.AdsDelegate;
import com.miui.videoplayer.media.DuoKanPlayer;
import com.miui.videoplayer.model.MediaConfig;
import com.miui.videoplayer.model.OnlineUri;
import com.miui.videoplayer.playservice.PlayServiceDelegate;

/**
 * @author tianli
 *
 */
public class PPTVVideoView extends AdsVideoView{
	
	public final static String TAG = "PPTVVideoView";

	public PlayServiceDelegate mPlayService = PlayServiceDelegate.getDefault(getContext());
	
	UrlRequestor mUrlRequestor;
	
	private String mUri = null;
	
	private String mPlayUrl = "";
	private DuoKanPlayer mBackendPlayer;
	
	private OnlineUri mOnlineUri = null;
	
	public PPTVVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PPTVVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PPTVVideoView(Context context) {
		super(context);
	}
		
	private void requestUrl(String uri){
		if(mUrlRequestor != null){
			mUrlRequestor.cancel();
			mUrlRequestor = null;
		}
		mUrlRequestor = new UrlRequestor();
		mUrlRequestor.getUrl(uri);
	}

	@Override
	public void setDataSource(String uri) {
		setDataSource(uri, null);
	}

	@Override
	public void setDataSource(String uri, Map<String, String> headers) {
		Log.d(TAG, "setDataSource : " + uri);
		mBackendPlayer = null;
		mPlayUrl = null;
		mUri = uri;
		startAdsPlay();
		requestUrl(getVid(mUri));
	}
	
	@Override
    protected AdBean requestAd() {
	    int mediaId = 0,  ci = 1;
	    if(mOnlineUri != null){
	        mediaId = mOnlineUri.getMediaId();
	        ci = mOnlineUri.getCi();
	    }
	    return AdsDelegate.getDefault(getContext()).getAdUrl(mediaId, ci,  
	            MediaConfig.MEDIASOURCE_PPTV_TYPE_CODE);
    }

    @Override
    protected String getAdExtraJson() {
        try{
            int mediaId = 0,  ci = 1;
            if(mOnlineUri != null){
                mediaId = mOnlineUri.getMediaId();
                ci = mOnlineUri.getCi();
            }
            return AdUtils.buildOnlineJson(mediaId, ci, MediaConfig.MEDIASOURCE_PPTV_TYPE_CODE).toString();
        }catch(Exception e){
        }
        return null;
    }

    @Override
	public void close() {
		super.close();
		if(mUrlRequestor != null){
			mUrlRequestor.cancel();
			mUrlRequestor = null;
		}
		new Thread(new Runnable() {
            @Override
            public void run() {
                mPlayService.closePlay(MediaConfig.MEDIASOURCE_PPTV_TYPE_CODE, null);
            }
        }).start();
		if(mBackendPlayer != null){
		    Log.e(TAG, "release mBackendPlayer.");
		    mBackendPlayer.reset();
		    mBackendPlayer.release();
		}
	}
	
	private String getVid(String uri){
		try {
			JSONObject json = new JSONObject(uri);
			Log.d(TAG, "vid: " + json.getString("vid"));
			return json.getString("vid");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return uri;
	}
	
	private void onUrlReady(String url){
		Log.d(TAG, "onUrlReady : " + url);
		mPlayUrl = url;
		if(isAdsPlaying()){
		    mBackendPlayer = createMediaPlayer();
		    try{
		          mBackendPlayer.setDataSource(mPlayUrl);
		          mBackendPlayer.prepareAsync();
		    }catch(Exception e){
		        e.printStackTrace();
		    }
		}else{
	        super.setDataSource(url);
		}
	}

	public class UrlRequestor extends AsyncTask<String, Void, String> {

		private boolean mCanceled = false;
		
		public void getUrl(String sdkInfo) {
			executeOnExecutor(THREAD_POOL_EXECUTOR, sdkInfo);
		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "doInBackground : ");
			return PlayServiceDelegate.getDefault(getContext()).
					getPlayUrl(MediaConfig.MEDIASOURCE_PPTV_TYPE_CODE, 
					params[0], null);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d(TAG, "onPostExecute : " + result);
			if(!mCanceled && !TextUtils.isEmpty(result)){
				onUrlReady(result);
			}
		}
		
		public void cancel(){
			mCanceled = true;
		}
	}

	@Override
	public boolean canBuffering() {
		return true;
	}

	@Override
	protected void onAdsPlayEnd() {
	    if(mBackendPlayer != null){
	        attachDuoKanPlayer(mBackendPlayer);
	    }else{
//	        requestUrl(getVid(mUri));
	    }
	    mBackendPlayer = null;
	}

    @Override
    public void setPlayInfo(Object baseUri) {
        super.setPlayInfo(baseUri);
        if(baseUri instanceof OnlineUri){
            mOnlineUri = (OnlineUri)baseUri;
        }else{
            mOnlineUri = null;
        }
    }
	
	

}

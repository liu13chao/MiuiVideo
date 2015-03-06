/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   QiyiVideoView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-19
 */

package com.miui.videoplayer.videoview;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.widget.RelativeLayout;

import com.android.wolesdk.api.WoleSDK;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.media.AdsPlayListener;
import com.miui.videoplayer.videoview.SdkVideoView;
import com.miui.videoplayer.widget.AdView;
import com.miui.videoplayer.widget.ToastBuilder;

/**
 * @author tianli
 *
 */
public class WoleVideoView extends SdkVideoView{
	
	public static final String TAG = "WoleVideoView";
	private AdView mAdView;
	private Activity mContext;
	public WoleVideoView(Activity context){
		super(context);
		mContext = context;
		init();
	}
	
	private void init(){
		WoleSDK.getInstance().setAdListener(mAdsListener);
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
	
	@Override
	public void setDataSource(String uri) {
		Log.d(TAG, "setDataSource : " + uri.toString());
		setDataSource(uri, null);
	}
	
	@Override
	public void setDataSource(String uri, Map<String, String> headers) {
		Log.d(TAG, "setDataSource : " + uri.toString());
		playVideo(uri);
	}
	
	public void playVideo(String uri){
		Log.d(TAG, "play video.");
		try {
			WoleSDK.getInstance().play(mContext, mPlayer,
					getVid(uri), (RelativeLayout)asView(), mAdView.getAdButtonView());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		mPlayer.release();
	}
	
	@Override
	public void onActivityPause() {
	    super.onActivityPause();
		try {
			WoleSDK.getInstance().onPause();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResume() {
	    super.onActivityResume();
		WoleSDK.getInstance().onResume();
	}
	
	private AdsPlayListener mAdsListener = new AdsPlayListener() {
		@Override
		public void onAdsTimeUpdate(int leftSecond) {
			if(mAdsPlayListener != null){
				mAdsPlayListener.onAdsTimeUpdate(leftSecond);
			}
		}
		
		@Override
		public void onAdsPlayStart() {
			Log.d(TAG, "onAdsPlayStart ");
			mIsAdsPlaying = true;
			if(mAdsPlayListener != null){
				mAdsPlayListener.onAdsPlayStart();
			}
		}
		
		@Override
		public void onAdsPlayEnd() {
			Log.d(TAG, "onAdsPlayEnd ");
			mIsAdsPlaying = false;
			if(mOnVideoLoadingListener != null){
				mOnVideoLoadingListener.onVideoLoading(WoleVideoView.this);
			}
			if(mAdsPlayListener != null){
				mAdsPlayListener.onAdsPlayEnd();
			}
		}
		
		@Override
		public void onAdsDuration(int duration) {
			if(mAdsPlayListener != null){
				mAdsPlayListener.onAdsDuration(duration);
			}
		}
	};

	@Override
	public boolean isAirkanEnable() {
		return !mIsAdsPlaying;
	}

	@Override
	public void attachAdView(AdView adView) {
		mAdView = adView;		
	}

	@Override
	public void continuePlay(PlayHistoryEntry history) {
		if(history != null && history.getPosition() > 5000){
			// more than 5 seconds.
			ToastBuilder.buildContinuePlay(mContext, history.getPosition()).show();
			seekTo(history.getPosition());
		}
	}

	@Override
	public void onActivityDestroy() {
	}

	@Override
	public boolean isSupportZoom() {
		return false;
	}
}

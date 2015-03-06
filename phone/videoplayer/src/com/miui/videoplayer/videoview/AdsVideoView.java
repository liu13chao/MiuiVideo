/**
 * 
 */
package com.miui.videoplayer.videoview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.miui.videoplayer.ads.AdBean;
import com.miui.videoplayer.ads.AdsDelegate;
import com.miui.videoplayer.media.AdsPlayListener;
import com.miui.videoplayer.media.DuoKanPlayer;
import com.miui.videoplayer.media.IMediaPlayer;
import com.miui.videoplayer.media.IMediaPlayer.OnCompletionListener;
import com.miui.videoplayer.media.IMediaPlayer.OnErrorListener;
import com.miui.videoplayer.widget.AdView;


/**
 * @author tianli
 *
 */
public abstract class AdsVideoView extends DuoKanVideoView implements OnClickListener {

	public static String TAG = "AdsVideoView";
	
	private AdsPlayListener mAdsPlayListener;
	
	boolean mIsAdPlaying = false;
	
	private Handler mUIHanlder = new Handler(Looper.getMainLooper());
	
	private OnCompletionListener mExOnCompletionListener;
	private OnErrorListener mExOnErrorListener;
	
	private int mAdLeft = 0;
	private int mAdDuration = 0;
	
	private AdBean mAdCell = null;
	private String mAdExtraJson;
	private AdRequestor mAdRequestor;
	
	private AdView mAdView;
	
	public AdsVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public AdsVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AdsVideoView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
	    super.setOnCompletionListener(mInnerOnCompletionListener);
	    super.setOnErrorListener(mInnerOnErrorListener);
	}
	
	@Override
	public void attachAdView(AdView adView) {
		mAdView = adView;
	}

	public void playAd(AdBean cell){
		Log.d(TAG, "playAd : " + cell.toString());
		mIsAdPlaying = true;
		if(mAdsPlayListener != null){
			mAdsPlayListener.onAdsPlayStart();
		}
		if(mAdView != null){
		    if(!TextUtils.isEmpty(cell.getClickUrl())){
	            mAdView.getAdButtonView().setVisibility(View.VISIBLE);
	            mAdView.getAdButtonView().setOnClickListener(this);
	        }else{
	            mAdView.getAdButtonView().setVisibility(View.INVISIBLE);
                mAdView.getAdButtonView().setOnClickListener(null);
	        }
		}
		mAdDuration = toInt(cell.getAdTime());
		mAdLeft = mAdDuration;
		if(mAdDuration > 0){
		    updateAdsLeftTime();
		    if(mAdsPlayListener != null){
		        mAdsPlayListener.onAdsDuration(mAdDuration);
		    }
		}
//        mMediaPlayer = new DuoKanPlayer(new OriginMediaPlayer());
//        initMediaPlayer(mMediaPlayer);
		super.setDataSource(cell.getAdUrl());
		AdsDelegate.getDefault(getContext()).logAdPlay(mAdCell,  mAdExtraJson);
		mUIHanlder.postDelayed(mUpdateAdCountDown, 1000);
	}
	
	private void updateAdsLeftTime(){
		if(mAdsPlayListener != null){
			mAdsPlayListener.onAdsTimeUpdate(mAdLeft);
		}
	}

	private Runnable mUpdateAdCountDown = new Runnable() {
		@Override
		public void run() {
		    DuoKanPlayer player = mMediaPlayer;
			if(mIsAdPlaying && player != null && !player.isReleased()){
				int currentPosition = (int)Math.round(player.getCurrentPosition() / 1000f);
				Log.d(TAG, "current is " + currentPosition);
				if(mAdDuration <= 0){
					mAdDuration = Math.round(player.getDuration() / 1000f);
					Log.d(TAG, "duration is " + mAdDuration);
					if(mAdDuration > 0){
			            if(mAdsPlayListener != null){
			                mAdsPlayListener.onAdsDuration(mAdDuration);
			            }
			        }
				}
				if(mAdDuration > 0){
					mAdLeft  =  mAdDuration - currentPosition;
					mAdLeft = Math.max(mAdLeft, 0);
					updateAdsLeftTime();
				}
				mUIHanlder.postDelayed(mUpdateAdCountDown, 1000);
			}
		}
	};

	@Override
	public void setAdsPlayListener(AdsPlayListener adPlayListener) {
		mAdsPlayListener = adPlayListener;
	}

	@Override
	public boolean isAdsPlaying() {
		return mIsAdPlaying;
	}
	
	@Override
	public void setOnCompletionListener(OnCompletionListener l) {
		mExOnCompletionListener = l;
	}
	
	@Override
    public void setOnErrorListener(OnErrorListener l) {
	    mExOnErrorListener = l;
    }

    protected abstract AdBean requestAd();
    protected abstract String getAdExtraJson();
	
	protected void startAdsPlay(){
	    if(mAdRequestor != null){
	        mAdRequestor.cancel();
	    }
	    mAdRequestor = new AdRequestor();
	    mAdRequestor.send(mAdRequestListener);
	}
	
	AdsRequestListener mAdRequestListener = new AdsRequestListener() {
        @Override
        public void onAdResult(AdBean bean) {
            mAdCell = bean;
            mAdExtraJson = getAdExtraJson();
            if(mAdCell != null && !TextUtils.isEmpty(mAdCell.getAdUrl())){
                playAd(mAdCell);
            }else{
                onAdsPlayEnd();
            }
        }
    };
	
	private int toInt(String value){
		try{
			return Integer.parseInt(value);
		}catch(Exception e){
		}
		return 0;
	}
	
	protected void onAdsPlayEnd(){
	}

	private void handleAdsEnd(){
       mIsAdPlaying = false;
        if(mMediaPlayer != null){
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
	    onAdsPlayEnd();

	    if(mAdsPlayListener != null){
	        mAdsPlayListener.onAdsPlayEnd();
	    }
	    AdsDelegate.getDefault(getContext()).logAdFinished(mAdCell, mAdExtraJson);
	}

	public OnCompletionListener mInnerOnCompletionListener = new OnCompletionListener() {
	    @Override
	    public void onCompletion(IMediaPlayer mp) {
	        Log.d(TAG, "onCompletion");
	        if(mIsAdPlaying){
	            handleAdsEnd();
	        }else{
	            if(mExOnCompletionListener != null){
	                mExOnCompletionListener.onCompletion(mp);
	            }
	        }
	    }
	};
	
	public OnErrorListener mInnerOnErrorListener = new OnErrorListener () {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            if(mIsAdPlaying){
                handleAdsEnd();
            }else{
                if(mExOnErrorListener != null){
                    return mExOnErrorListener.onError(mp, what, extra);
                }
            }
            return false;
        }
    };

	@Override
	public void onClick(View view) {
		if(mAdView != null && mAdView.getAdButtonView() == view){
			try{
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(mAdCell.getClickUrl()));
				getContext().startActivity(intent);
				AdsDelegate.getDefault(getContext()).logAdClick(mAdCell, mAdExtraJson);
			}catch(Exception e){
			}
		}
	}

    @Override
    public void close() {
        super.close();
        if(mIsAdPlaying){
            mUIHanlder.removeCallbacks(mUpdateAdCountDown);
            AdsDelegate.getDefault(getContext()).logAdSkipped(mAdCell, mAdDuration - mAdLeft, mAdExtraJson);
        }
    }
	
	private class AdRequestor  extends  AsyncTask<Void, Void, AdBean>{
	    public AdsRequestListener mListener;
	    
	    public void send(AdsRequestListener listener){
	        mListener = listener;
	        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	    }
	    
	    public void cancel(){
	        mListener = null;
	    }
	    
        @Override
        protected AdBean doInBackground(Void... arg0) {
            AdBean bean = requestAd();
            return bean;
        }

        @Override
        protected void onPostExecute(AdBean result) {
            super.onPostExecute(result);
            if(mListener != null){
                mListener.onAdResult(result);
            }
        }
	};
    
	interface AdsRequestListener{
	    public void onAdResult(AdBean bean);
	}
    
}

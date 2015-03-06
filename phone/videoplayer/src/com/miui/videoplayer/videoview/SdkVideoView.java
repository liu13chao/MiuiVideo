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

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.duokan.MediaPlayer.MediaInfo;
import com.miui.videoplayer.common.DuoKanCodecConstants;
import com.miui.videoplayer.media.AdsPlayListener;
import com.miui.videoplayer.media.DuoKanPlayer;
import com.miui.videoplayer.media.IMediaPlayer.OnBufferingUpdateListener;
import com.miui.videoplayer.media.IMediaPlayer.OnCompletionListener;
import com.miui.videoplayer.media.IMediaPlayer.OnErrorListener;
import com.miui.videoplayer.media.IMediaPlayer.OnInfoListener;
import com.miui.videoplayer.media.IMediaPlayer.OnPreparedListener;
import com.miui.videoplayer.media.IMediaPlayer.OnSeekCompleteListener;
import com.miui.videoplayer.media.IMediaPlayer.OnVideoSizeChangedListener;
import com.miui.videoplayer.media.IMediaPlayerX;
import com.miui.videoplayer.media.OnMediaPlayerListener;
import com.miui.videoplayer.model.DuoKanMediaPlayer;
import com.miui.videoplayer.model.OriginMediaPlayer;

/**
 * @author tianli
 *
 */
public abstract class SdkVideoView implements IVideoView{
	
	public static final String TAG = "SdkVideoView";

	protected DuoKanPlayer mPlayer;
	
	IMediaPlayerX mPlayerEx;
	
	private RelativeLayout mViewGroup;
	
	private Context mContext;
	
	protected boolean mIsAdsPlaying = false;
	
	private OnBufferingUpdateListener mBufferingUpdateListener;
	private OnErrorListener mErrorListener;
	private OnCompletionListener mCompletionListener;
	private OnInfoListener mInfoListener;
	private OnPreparedListener mPreparedListener;
	private OnSeekCompleteListener mSeekCompleteListener;
	private OnVideoSizeChangedListener mVideoSizeChangedListener;
	
	protected OnVideoLoadingListener mOnVideoLoadingListener;
	
	protected AdsPlayListener mAdsPlayListener;
	public SdkVideoView(Context context){
		mContext = context;
		init();
	}
	
	private void init(){
		mViewGroup = new RelativeLayout(mContext);
		if(DuoKanCodecConstants.sUseDuokanCodec){
			DuoKanMediaPlayer player = new DuoKanMediaPlayer();
			mPlayerEx = player;
			mPlayer = new DuoKanPlayer(player);
		}else{
			OriginMediaPlayer player = new OriginMediaPlayer();
			mPlayerEx = player;
			mPlayer = new DuoKanPlayer(player);
		}
		mPlayer.setMediaPlayerListener(mMediaPlayerListener);
	}
	
	public DuoKanPlayer getPlayer(){
	    return mPlayer;
	}
	
	@Override
	public void start() {
		mPlayer.start();
	}

	@Override
	public void pause() {
		mPlayer.pause();
	}

	@Override
	public int getDuration() {
		return mPlayer.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return mPlayer.getCurrentPosition();
	}

	@Override
    public int getRealPlayPosition() {
        return getCurrentPosition();
    }

    @Override
	public void seekTo(int pos) {
    	Log.d(TAG, "seekTo " + pos);
    	mPlayer.seekTo(pos);
	}

	@Override
	public boolean isPlaying() {
		return mPlayer.isPlaying() && mPlayer.isInPlaybackState();
	}

	@Override
    public boolean isInPlaybackState() {
        return mPlayer.isInPlaybackState();
    }

    @Override
	public boolean canPause() {
		return !mIsAdsPlaying;
	}

	@Override
	public boolean canSeekBackward() {
		return !mIsAdsPlaying;
	}

	@Override
	public boolean canSeekForward() {
		return !mIsAdsPlaying;
	}

	@Override
	public View asView() {
		return mViewGroup;
	}

	@Override
	public boolean canBuffering() {
		return true;
	}
	
	@Override
    public boolean isAirkanEnable() {
	    if(isAdsPlaying()){
	        return false;
	    }
        return true;
    }

    @Override
	public boolean isAdsPlaying() {
		return mIsAdsPlaying;
	}

	@Override
	public Uri getUri() {
		return mPlayer.getUri();
	}

	@Override
	public void onActivityPause() {
	    if(mPlayer != null){
	        mPlayer.onActivityPause();
	    }     
	}

    @Override
    public void onActivityResume() {
        if(mPlayer != null){
            mPlayer.onActivityResume();
        } 
    }

    @Override
	public void setAdsPlayListener(AdsPlayListener adPlayListener) {
		mAdsPlayListener = adPlayListener;
	}

	@Override
	public int getBufferPercentage() {
		return mPlayer.getBufferPercentage();
	}

	@Override
	final public void setOnPreparedListener(OnPreparedListener listener) {
		mPreparedListener = listener;
	}

	@Override
	final public void setOnCompletionListener(OnCompletionListener listener) {
		mCompletionListener = listener;
	}

	@Override
	final public void setOnErrorListener(OnErrorListener listener) {
		mErrorListener = listener;
	}

	@Override
	final public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
		mSeekCompleteListener = listener;
	}

	@Override
	final public void setOnInfoListener(OnInfoListener listener) {
		mInfoListener = listener;
	}

	@Override
	final public void setOnBufferingUpdateListener(
			OnBufferingUpdateListener onBufferingUpdateListener) {
		mBufferingUpdateListener = onBufferingUpdateListener;
	}

	@Override
	final public void setOnVideoSizeChangedListener(
			OnVideoSizeChangedListener onVideoSizeChangedListener) {
		mVideoSizeChangedListener = onVideoSizeChangedListener;
	}
	
	@Override
	public void setOnVideoLoadingListener(OnVideoLoadingListener loadingListener) {
		mOnVideoLoadingListener = loadingListener;
	}

	@Override
	public MediaInfo getMediaInfo() {
		if(mPlayerEx != null){
			return mPlayerEx.getMediaInfo();
		}else{
			return null;
		}
	}
	@Override
	public boolean get3dMode() {
		if(mPlayerEx != null){
			return mPlayerEx.get3dMode();
		}else{
			return false;
		}
	}
	@Override
	public void set3dMode(boolean mode) {
		if(mPlayerEx != null){
			mPlayerEx.set3dMode(mode);
		}	
	}
	@Override
	public int getVideoWidth() {
		return mPlayer.getVideoWidth();
	}
	@Override
	public int getVideoHeight() {
		return mPlayer.getVideoHeight();
	}

    @Override
    public void setPlayInfo(Object playInfo) {
    }

    @Override
    public void setForceFullScreen(boolean forceFullScreen) {
    }
	
    @Override
	public void adjustVideoPlayViewSize(int videoSizeStyle) {
	}
    
    @Override
    public void requestVideoLayout() {
    }

    @Override
    public boolean hasLoadingAfterAd() {
        return true;
    }

    private OnMediaPlayerListener mMediaPlayerListener = new OnMediaPlayerListener(){

        @Override
        public com.miui.videoplayer.media.IMediaPlayer.OnErrorListener getOnErrorListener() {
            return mErrorListener;
        }

        @Override
        public com.miui.videoplayer.media.IMediaPlayer.OnCompletionListener getOnCompletionListener() {
            return mCompletionListener;
        }

        @Override
        public com.miui.videoplayer.media.IMediaPlayer.OnPreparedListener getOnPreparedListener() {
            return mPreparedListener;
        }

        @Override
        public com.miui.videoplayer.media.IMediaPlayer.OnSeekCompleteListener getOnSeekCompleteListener() {
            return mSeekCompleteListener;
        }

        @Override
        public com.miui.videoplayer.media.IMediaPlayer.OnInfoListener getOnInfoListener() {
            return mInfoListener;
        }

        @Override
        public com.miui.videoplayer.media.IMediaPlayer.OnBufferingUpdateListener getOnBufferingUpdateListener() {
            return mBufferingUpdateListener;
        }

        @Override
        public com.miui.videoplayer.media.IMediaPlayer.OnVideoSizeChangedListener getOnVideoSizeChangedListener() {
            return mVideoSizeChangedListener;
        }
    };
}

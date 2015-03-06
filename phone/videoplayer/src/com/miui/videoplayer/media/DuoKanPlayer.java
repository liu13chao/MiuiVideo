/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   SdkPlayer.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-21
 */

package com.miui.videoplayer.media;

import java.io.IOException;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.duokan.MediaPlayer.MediaInfo;

/**
 * @author tianli
 *
 */
public class DuoKanPlayer implements IDuoKanPlayer{
    
	private static final String TAG = "DuoKanPlayer";
	
    public static final int STATE_ERROR              = -1;
    public static final int STATE_IDLE               = 0;
    public static final int STATE_PREPARING          = 1;
    public static final int STATE_PREPARED           = 2;
    public static final int STATE_PLAYING            = 3;
    public static final int STATE_PAUSED             = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    
    private boolean mIsActivityPaused = false;
    private long mActivityResumeTime = 0;
    
    protected int mCurrentState = STATE_IDLE;
    protected int mTargetState  = STATE_IDLE;
//    private boolean mIsPrepared = false;
    
//    protected int mSeekWhenPrepared;
	protected int mBufferUpdated = 0;
	
	private int mVideoWidth;
	private int mVideoHeight;
	
	private int mDuration;
	
	private Uri mUri = null;
	
	IDuoKanPlayer mPlayer;
	
	private OnBufferingUpdateListener mExBufferingUpdateListener;
	private OnErrorListener mExErrorListener;
	private OnCompletionListener mExCompletionListener;
	private OnInfoListener mExInfoListener;
	private OnPreparedListener mExPreparedListener;
	private OnSeekCompleteListener mExSeekCompleteListener;
	private OnVideoSizeChangedListener mExVideoSizeChangedListener;
	
	private OnMediaPlayerListener mMediaPlayerListener;
	
	public DuoKanPlayer(IDuoKanPlayer player){
		mPlayer = player;
		mPlayer.setOnInfoListener(mInnerInfoListener);
		mPlayer.setOnErrorListener(mInnerErrorListener);
		mPlayer.setOnCompletionListener(mInnerCompletionListener);
		mPlayer.setOnPreparedListener(mInnerPreparedListener);
		mPlayer.setOnSeekCompleteListener(mInnerSeekCompleteListener);
		mPlayer.setOnVideoSizeChangedListener(mInnerVideoSizeListener);
		mPlayer.setOnBufferingUpdateListener(mInnerBufferingUpdateListener);
	}
	
	@Override
	public void setDataSource(Context context, Uri uri,
			Map<String, String> headers) throws IOException,
			IllegalArgumentException, SecurityException,
			IllegalStateException {
        Log.d(TAG, "setDataSource : ");
	    if(uri == null || TextUtils.isEmpty(uri.toString())){
	        handleError(0, 0);
	        return;
	    }
		if(mPlayer != null && uri != null){
			if(headers != null){
				mPlayer.setDataSource(context, uri, headers);
			}else{
				mPlayer.setDataSource(context, uri);
			}
			Log.d(TAG, "setDataSource : " + uri.toString());
			mUri = uri;
			mBufferUpdated = 0;
			mVideoWidth = mVideoHeight = 0;
			mDuration = 0;
		}
	}

	@Override
	public void setDataSource(Context context, Uri uri) throws IOException,
			IllegalArgumentException, SecurityException,
			IllegalStateException {
		setDataSource(context, uri, null);
	}

	@Override
	public void setDataSource(String path, Map<String, String> headers)
			throws IOException, IllegalArgumentException,
			SecurityException, IllegalStateException {
        Log.d(TAG, "setDataSource : ");
	    if(TextUtils.isEmpty(path)){
            handleError(0, 0);
            return;
        }
		if(mPlayer != null && !TextUtils.isEmpty(path)){
			if(headers != null){
				mPlayer.setDataSource(path, headers);
			}else{
				mPlayer.setDataSource(path);
			}
			Log.d(TAG, "setDataSource : " + path);
			mUri = Uri.parse(path);
			mBufferUpdated = 0;
			mVideoWidth = mVideoHeight = 0;
			mDuration = 0;
//			mIsPrepared = false;
		}
	}

	@Override
	public void setDataSource(String path) throws IOException,
			IllegalArgumentException, SecurityException,
			IllegalStateException {
		setDataSource(path, null);
	}

	@Override
	public void seekTo(int ms) throws IllegalStateException {
		Log.d(TAG, "seekTo " + ms);
		if(mPlayer != null){
			if (isInPlaybackState()) {
				Log.d(TAG, "do seekTo " + ms);
				mPlayer.seekTo(ms);
//				mSeekWhenPrepared = 0;
			} else {
//				mSeekWhenPrepared = ms;
			}
		}
	}

	@Override
	public void setOnBufferingUpdateListener(
			OnBufferingUpdateListener listener) {
		mExBufferingUpdateListener = listener;
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		mExCompletionListener = listener;
	}

	@Override
	public void setOnErrorListener(OnErrorListener listener) {
		mExErrorListener = listener;
	}

	@Override
	public void setOnInfoListener(OnInfoListener listener) {
		mExInfoListener = listener;
	}

	@Override
	public void setOnPreparedListener(OnPreparedListener listener) {
		mExPreparedListener = listener;
	}

	@Override
	public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
		mExSeekCompleteListener = listener;
	}

	@Override
	public void setOnVideoSizeChangedListener(
			OnVideoSizeChangedListener listener) {
		mExVideoSizeChangedListener = listener;
	}

	@Override
	public void prepare() throws IOException, IllegalStateException {
		if(mPlayer != null){
			Log.d(TAG, "prepare");
			mCurrentState = STATE_PREPARING;
			mPlayer.prepare();
			mCurrentState = STATE_PREPARED;
		}
	}

	@Override
	public void prepareAsync() throws IllegalStateException {
		if(mPlayer != null){
			Log.d(TAG, "prepareAsync");
			mPlayer.prepareAsync();
			mCurrentState = STATE_PREPARING;
//			mTargetState = STATE_PLAYING;
		}
	}
	
	@Override
	public void release() {
		if(mPlayer != null){
			Log.d(TAG, "release");
			mPlayer.release();
			mCurrentState = STATE_IDLE;
			mTargetState  = STATE_IDLE;
//			mIsPrepared = false;
			mUri = null;
			mPlayer = null;
		}
	}

	public void onActivityResume(){
	    mIsActivityPaused = false;
	    mActivityResumeTime = System.currentTimeMillis();
	}
	
	public void onActivityPause(){
	    mIsActivityPaused = true;
	}
	
	public Uri getUri() {
		return mUri;
	}
	
	public int getBufferPercentage() {
		return mBufferUpdated;
	}
	
	public boolean isReleased(){
		return mPlayer == null;
	}
	
	private boolean handleError(int what, int extra){
	    mCurrentState = STATE_ERROR;
        mTargetState = STATE_ERROR;
        if(mMediaPlayerListener != null){
            OnErrorListener listener = mMediaPlayerListener.getOnErrorListener();
            if(listener != null){
                listener.onError(this, what, extra);
            }
        }
        if(mExErrorListener != null){
            mExErrorListener.onError(DuoKanPlayer.this, what, extra);
        }
        return true;
	}

	private OnInfoListener mInnerInfoListener = new OnInfoListener() {
		@Override
		public boolean onInfo(IMediaPlayer mp, int what, int extra) {
			Log.d(TAG, "onInfo : what = " + what + ", extra = " + extra);
			if(mMediaPlayerListener != null){
				OnInfoListener listener = mMediaPlayerListener.getOnInfoListener();
				if(listener != null){
					listener.onInfo(DuoKanPlayer.this, what, extra);
				}
			}
			if(mExInfoListener != null){
				mExInfoListener.onInfo(DuoKanPlayer.this, what, extra);
			}
			return false;
		}
	}; 
	
	private OnBufferingUpdateListener mInnerBufferingUpdateListener = 
			new OnBufferingUpdateListener(){
		@Override
		public void onBufferingUpdate(IMediaPlayer mp, int percent) {
			Log.d(TAG, "onBufferingUpdate : " + percent);
			mBufferUpdated = percent;
			if(mMediaPlayerListener != null){
				OnBufferingUpdateListener listener = mMediaPlayerListener.getOnBufferingUpdateListener();
				if(listener != null){
					listener.onBufferingUpdate(DuoKanPlayer.this, percent);
				}
			}
			if(mExBufferingUpdateListener != null){
				mExBufferingUpdateListener.onBufferingUpdate(DuoKanPlayer.this, percent);
			}
		}
	};

	private OnCompletionListener mInnerCompletionListener = 
			new OnCompletionListener(){
		@Override
		public void onCompletion(IMediaPlayer mp) {
			Log.d(TAG, "onCompletion.");
			mCurrentState = STATE_PLAYBACK_COMPLETED;
			mTargetState = STATE_PLAYBACK_COMPLETED;
			if(mMediaPlayerListener != null){
				OnCompletionListener listener = mMediaPlayerListener.getOnCompletionListener();
				if(listener != null){
					listener.onCompletion(DuoKanPlayer.this);
				}
			}
			if(mExCompletionListener != null){
				mExCompletionListener.onCompletion(DuoKanPlayer.this);
			}
		}
	};
	
	private OnErrorListener mInnerErrorListener = 
			new OnErrorListener(){
		@Override
		public boolean onError(IMediaPlayer mp, int what, int extra) {
			Log.d(TAG, "onError : what = " + what + ", extra = " + extra);
			return handleError(what, extra);
//			mCurrentState = STATE_ERROR;
//			mTargetState = STATE_ERROR;
//			if(mMediaPlayerListener != null){
//				OnErrorListener listener = mMediaPlayerListener.getOnErrorListener();
//				if(listener != null){
//					listener.onError(DuoKanPlayer.this, what, extra);
//				}
//			}
//			if(mExErrorListener != null){
//				return mExErrorListener.onError(DuoKanPlayer.this, what, extra);
//			}
//			return false;
		}
	};

	private OnPreparedListener mInnerPreparedListener = 
			new OnPreparedListener(){
		@Override
		public void onPrepared(IMediaPlayer mp){
			Log.d(TAG, "onPrepared.");
			mCurrentState = STATE_PREPARED;
			if(mTargetState == STATE_PLAYING){
				start();
			}
//			if(mSeekWhenPrepared > 0){
//				seekTo(mSeekWhenPrepared);
//			}
//			mSeekWhenPrepared = 0;
			mDuration = 0;
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			if(mMediaPlayerListener != null){
				OnPreparedListener listener = mMediaPlayerListener.getOnPreparedListener();
				if(listener != null){
					listener.onPrepared(DuoKanPlayer.this);
				}
			}
			if(mExPreparedListener != null){
				mExPreparedListener.onPrepared(DuoKanPlayer.this);
			}
		}
	};

	private OnSeekCompleteListener mInnerSeekCompleteListener = 
			new OnSeekCompleteListener(){
		@Override
		public void onSeekComplete(IMediaPlayer mp) {
			Log.d(TAG, "seekComplete.");
			if(mMediaPlayerListener != null){
				OnSeekCompleteListener listener = mMediaPlayerListener.getOnSeekCompleteListener();
				if(listener != null){
					listener.onSeekComplete(DuoKanPlayer.this);
				}
			}
			if(mExSeekCompleteListener != null){
				mExSeekCompleteListener.onSeekComplete(DuoKanPlayer.this);
			}
		}
	};

	private OnVideoSizeChangedListener mInnerVideoSizeListener = 
			new OnVideoSizeChangedListener(){
		@Override
		public void onVideoSizeChanged(IMediaPlayer mp, int width,
				int height) {
			Log.d(TAG, "videoSize: width = " + width + ", height = " + height);
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			if(mMediaPlayerListener != null){
				OnVideoSizeChangedListener listener = mMediaPlayerListener.getOnVideoSizeChangedListener();
				if(listener != null){
					listener.onVideoSizeChanged(DuoKanPlayer.this, 
							width, height);
				}
			}
			if(mExVideoSizeChangedListener != null){
				mExVideoSizeChangedListener.onVideoSizeChanged(DuoKanPlayer.this, 
						width, height);
			}
		}
	};

	@Override
	public int getCurrentPosition() {
		if(mPlayer != null){
			if(isInPlaybackState()){
				return mPlayer.getCurrentPosition();
			}
		}
		return 0;
	}

	@Override
	public int getDuration() {
		if(mPlayer != null){
			if(isInPlaybackState()){
				if(mDuration <= 0){
					return mPlayer.getDuration();
				}else{
					mDuration = 0;
				}
			}
		}
		return 0;
	}

	@Override
	public int getVideoHeight() {
//		if(mPlayer != null){
//			if(isInPlaybackState()){
//				return mPlayer.getVideoHeight();
//			}
//		}
		return mVideoHeight;
	}

	@Override
	public int getVideoWidth() {
//		if(mPlayer != null){
//			if(isInPlaybackState()){
//				return mPlayer.getVideoWidth();
//			}
//		}
		return mVideoWidth;
	}
	
	public int getCurrentState(){
		return mCurrentState;
	}
	
	public boolean isIdle(){
		return mCurrentState == STATE_IDLE;
	}
	
	public boolean isPlayingState(){
        return mTargetState == STATE_PLAYING || mCurrentState == STATE_PLAYING;
    }
	
	public boolean isPrepared(){
	    return isInPlaybackState();
	}

	@Override
	public boolean isPlaying() {
		if(mPlayer != null){
			return isInPlaybackState() && mPlayer.isPlaying();
		}else{
			return false;
		}
	}

	@Override
	public void pause() throws IllegalStateException {
		if(mPlayer != null){
			if(isInPlaybackState()){
				mPlayer.pause();
				mCurrentState = STATE_PAUSED;
			}
			mTargetState = STATE_PAUSED;
		}
	}

	@Override
	public void reset() {
		if(mPlayer != null){
			mTargetState = STATE_IDLE;
			mCurrentState = STATE_IDLE;
//			mIsPrepared = false;
			mPlayer.reset();
		}
	}

	@Override
	public void setScreenOnWhilePlaying(boolean screenOn) {
		if(mPlayer != null){
			mPlayer.setScreenOnWhilePlaying(screenOn);
		}
	}

	@Override
	public void setDisplay(SurfaceHolder holder) {
		if(mPlayer != null){
			mPlayer.setDisplay(holder);
		}
	}

	@Override
	public void setSurface(Surface surface) {
		if(mPlayer != null){
			mPlayer.setSurface(surface);
		}
	}

	@Override
	public void setVolume(float leftVolume, float rightVolume) {
		if(mPlayer != null){
			mPlayer.setVolume(leftVolume, rightVolume);	
		}
	}

	@Override
	public void start() throws IllegalStateException {
		Log.d(TAG, "start ");
		if(mPlayer != null){
			if(isInPlaybackState() && !mIsActivityPaused){
				if(System.currentTimeMillis() - mActivityResumeTime < 500){
				    // TODO: workaround to avoid start before surfaceOnCreated.
				    Handler handler = new Handler(Looper.getMainLooper());
				    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mTargetState == STATE_PLAYING){
                                Log.d(TAG, "do start ");
                                mPlayer.start();
                                mCurrentState = STATE_PLAYING;
                            }
                        }
                    }, 500);
				}else{
	                Log.d(TAG, "do start ");
				    mPlayer.start();
				    mCurrentState = STATE_PLAYING;
				}
			}
			mTargetState = STATE_PLAYING;
		}
	}

	@Override
	public void stop() throws IllegalStateException {
		if(mPlayer != null){
			mTargetState = STATE_IDLE;
			mCurrentState = STATE_IDLE;
			mPlayer.stop();
		}
	}
	
    public boolean isInPlaybackState() {
        return mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING;
    }

	public OnMediaPlayerListener getMediaPlayerListener() {
		return mMediaPlayerListener;
	}

	public void setMediaPlayerListener(OnMediaPlayerListener mediaPlayerListener) {
		this.mMediaPlayerListener = mediaPlayerListener;
	}

	@Override
	public MediaInfo getMediaInfo() {
		if(mPlayer != null){
			return mPlayer.getMediaInfo();
		}else{
			return null;
		}
	}
	@Override
	public boolean get3dMode() {
		if(mPlayer != null){
			return mPlayer.get3dMode();
		}else{
			return false;
		}
	}
	@Override
	public void set3dMode(boolean mode) {
		if(mPlayer != null){
			mPlayer.set3dMode(mode);
		}
	}
}

package com.miui.videoplayer.videoview;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.duokan.MediaPlayer.MediaInfo;
import com.miui.videoplayer.common.Constants;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.framework.ui.LocalVideoPlaySizeAdjustable;
import com.miui.videoplayer.media.AdsPlayListener;
import com.miui.videoplayer.media.IMediaPlayer;
import com.miui.videoplayer.media.IMediaPlayer.OnBufferingUpdateListener;
import com.miui.videoplayer.media.IMediaPlayer.OnCompletionListener;
import com.miui.videoplayer.media.IMediaPlayer.OnErrorListener;
import com.miui.videoplayer.media.IMediaPlayer.OnInfoListener;
import com.miui.videoplayer.media.IMediaPlayer.OnPreparedListener;
import com.miui.videoplayer.media.IMediaPlayer.OnSeekCompleteListener;
import com.miui.videoplayer.media.IMediaPlayer.OnVideoSizeChangedListener;
import com.miui.videoplayer.widget.AdView;
import com.miui.videoplayer.widget.ToastBuilder;
import com.sohuvideo.api.SohuPlayerDefinition;
import com.sohuvideo.api.SohuPlayerError;
import com.sohuvideo.api.SohuPlayerItemBuilder;
import com.sohuvideo.api.SohuPlayerLibManager;
import com.sohuvideo.api.SohuPlayerLoadFailure;
import com.sohuvideo.api.SohuPlayerMonitor;
import com.sohuvideo.api.SohuPlayerSDK;
import com.sohuvideo.api.SohuPlayerSetting;
import com.sohuvideo.api.SohuPlayerStatCallback;
import com.sohuvideo.api.SohuScreenView;
import com.sohuvideo.api.SohuVideoPlayer;


public class SohuVideoView implements IVideoView, LocalVideoPlaySizeAdjustable {

    public static final String TAG = "SohuVideoView";

    static final int STATE_IDLE = 0;
    static final int STATE_PREPARING = 1;
    static final int STATE_PREPARED = 2;
    static final int STATE_PLAYBACK = 3;

    //  static final int BUFFER_START = 3;
    //  static final int BUFFER_END = 4;
    private static final String SOHULIB_PATH = "sohulib";
    static boolean mIsBuffering;

    private SohuVideoPlayer mSohuVideoPlayer;
    private SohuScreenView mSohuScreenView;
    private Context mContext;
    protected String mUri;

    private OnBufferingUpdateListener mBufferingUpdateListener;
    private OnErrorListener mErrorListener;
    private OnCompletionListener mCompletionListener;
    private OnInfoListener mInfoListener;
    private OnPreparedListener mPreparedListener;
    private OnSeekCompleteListener mSeekCompleteListener;
    private OnVideoSizeChangedListener mVideoSizeChangedListener;
    private OnVideoLoadingListener mOnVideoLoadingListener;
    private AdsPlayListener mAdsPlayListener;

    private int mState;

    private long sid = 0;
    private long vid = 0;
    private int site = 1;
    private int resolution = SohuPlayerDefinition.PE_DEFINITION_HIGH;

    private int mRetryTimes = 0;

    private SohuLayout mSohuLayout;

    public SohuVideoView(Context context) {
        mContext = context.getApplicationContext();
        initSohuSdk();
    }

    private void initSohuSdk(){
        SohuPlayerSDK.init(mContext);
		setSohuLibPath();
		SohuPlayerSetting.setArgs(Constants.SOHU_APPKEY);
		mSohuLayout = new SohuLayout(mContext);
		mSohuVideoPlayer = new SohuVideoPlayer();
		mSohuScreenView = new SohuScreenView(mContext);
		SohuPlayerSetting.setNeedAutoNext(false);
		mSohuVideoPlayer.setSohuScreenView(mSohuScreenView);
		mSohuVideoPlayer.setSohuPlayerMonitor(mSohuPlayerMonitor);
		mSohuVideoPlayer.setSohuPlayerStatCallback(mSohuPlayerStatCallback);		
		mState = STATE_IDLE;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		mSohuScreenView.setLayoutParams(params);
		mSohuLayout.addView(mSohuScreenView);
    }
    
	private void setSohuLibPath(){
		String path = mContext.getDir("lib", Context.MODE_PRIVATE).getAbsolutePath();
		if (path.endsWith(File.separator)) {
			path = path + SOHULIB_PATH + File.separator;
		}else{
			path = path + File.separator + SOHULIB_PATH + File.separator;
		}
		SohuPlayerLibManager.setMylibPath(path);
	}
	
	private void initArgs(String uri){
		JSONObject json = null;
		try {
			json = new JSONObject(uri);
			vid = json.getLong("vid");
			sid = json.getLong("sid");
			site = json.getInt("site");
			resolution = json.optInt("resolutionmap", SohuPlayerDefinition.PE_DEFINITION_HIGH);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setDataSource(String uri) {
		setDataSource(uri, null);
	}

	@Override
	public void setDataSource(String uri, Map<String, String> headers) {
		Log.d(TAG, "setDataSource");
		if(TextUtils.isEmpty(uri)){
			return;
		}
		mIsBuffering = false;
		mUri = uri;
		initArgs(uri);
		Log.d(TAG, "sid:" + sid + "," + "vid:" + vid + "," + "site:" + site);
		if(sid == 0 || vid == 0){
			Log.e(TAG, "sid or vid is null");
			return;
		}
		SohuPlayerItemBuilder sohuPlayitemBuilder = new SohuPlayerItemBuilder("111", sid, vid, site);
		mSohuVideoPlayer.setDataSource(sohuPlayitemBuilder);
		SohuPlayerSetting.setPreferDefinition(resolution);
	}

	@Override
	public void start() {
	    try{
	        if(!mSohuVideoPlayer.isPlaybackState()){
	            mSohuVideoPlayer.play();
	        }
	    }catch(Throwable e){
	    }
	}

	@Override
	public void pause() {
		Log.d(TAG, "pause()");
	    try{
	        if(mSohuVideoPlayer.isPlaybackState()){
	            mSohuVideoPlayer.pause();
	        }
	    }catch(Throwable e){
        }
	}

	@Override
	public int getDuration() {
	    try{
	        return mSohuVideoPlayer.getDuration();
	    }catch(Throwable e){
	    }
	    return 0;
	}

	@Override
	public int getCurrentPosition() {
	    try{
	        return mSohuVideoPlayer.getCurrentPosition();
	    }catch(Throwable e){
	    }
	    return 0;
	}

	@Override
	public void seekTo(int pos) {
	    try{
	        mSohuVideoPlayer.seekTo(pos);
	    }catch(Throwable e){
	    }
	}

	@Override
	public boolean isPlaying() {
	    try{
	        return mSohuVideoPlayer.isPlaybackState();
	    }catch(Throwable e){
	    }
	    return false;
	}

	@Override
    public boolean isInPlaybackState() {
        return  mState != STATE_IDLE  && mState != STATE_PREPARING 
                && mState != STATE_PREPARED;
    }

    @Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public boolean canBuffering() {
		return true;
	}

	@Override
	public boolean isAirkanEnable() {
		return false;
	}

	@Override
	public void close() {
		try{
			mSohuVideoPlayer.stop(false);
			mSohuVideoPlayer.release();
		}catch (Throwable t) {
		}
	}

	@Override
	public Uri getUri() {
		if(TextUtils.isEmpty(mUri)){
			return null;
		}else{
			return Uri.parse(mUri);
		}
	}

	@Override
	public boolean isAdsPlaying() {
	    try{
	        return mSohuVideoPlayer.isAdvertInPlayback();
	    }catch(Throwable e){
	    }
	    return false;
	}

	@Override
	public MediaInfo getMediaInfo() {
		MediaInfo mediaInfo = new MediaInfo();
		try{
		    mediaInfo.duration = mSohuVideoPlayer.getDuration();
	        if(mSohuVideoPlayer.getVideoWidthAndHeight() != null){
	            mediaInfo.videoWidth = mSohuVideoPlayer.getVideoWidthAndHeight()[0];
	            mediaInfo.videoHeight = mSohuVideoPlayer.getVideoWidthAndHeight()[1];
	        }
		}catch(Throwable e){
		}
		return mediaInfo;
	}

	@Override
	public boolean get3dMode() {
		return false;
	}

	@Override
	public void set3dMode(boolean mode) {
	}

	@Override
	public void setOnPreparedListener(OnPreparedListener listener) {
		mPreparedListener = listener;
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		mCompletionListener = listener;
	}

	@Override
	public void setOnErrorListener(OnErrorListener listener) {
		mErrorListener = listener;
	}

	@Override
	public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
		mSeekCompleteListener = listener;
	}

	@Override
	public void setOnInfoListener(OnInfoListener listener) {
		mInfoListener = listener;
	}

	@Override
	public void setOnBufferingUpdateListener(
			OnBufferingUpdateListener onBufferingUpdateListener) {
		mBufferingUpdateListener = onBufferingUpdateListener;
	}

	@Override
	public void setOnVideoSizeChangedListener(
			OnVideoSizeChangedListener onVideoSizeChangedListener) {
		mVideoSizeChangedListener = onVideoSizeChangedListener;
	}

	@Override

	public int getVideoWidth() {
	    try{
	        if(mSohuVideoPlayer.getVideoWidthAndHeight() != null){
	            return mSohuVideoPlayer.getVideoWidthAndHeight()[0];
	        }
	    }catch(Throwable t){
	    }
		return 0;
	}

	@Override
	public int getVideoHeight() {
	    try{
	        if(mSohuVideoPlayer.getVideoWidthAndHeight() != null){
	            return mSohuVideoPlayer.getVideoWidthAndHeight()[1];
	        }
	    }catch(Throwable t){
	    }
	    return 0;
	}

	@Override
	public View asView() {
		return mSohuLayout;
	}

	@Override
	public void onActivityPause() {
		Log.d(TAG, "onPause stop(true)");
		try{
		    mSohuVideoPlayer.pause();
		    mSohuVideoPlayer.stop(true);
		}catch(Throwable t){
		}
	}

	@Override
	public void onActivityResume() {
		Log.d(TAG, "onResume()");
		try{
			SohuPlayerSetting.setArgs(Constants.SOHU_APPKEY);
			SohuPlayerSetting.setNeedAutoNext(false);
		    mSohuVideoPlayer.play();
		}catch(Throwable t){
		}
//		if(mOnVideoLoadingListener != null){
//			mOnVideoLoadingListener.onVideoLoading(null);
//		}
	}

	@Override
	public void setAdsPlayListener(AdsPlayListener adPlayListener) {
		mAdsPlayListener = adPlayListener;
	}

	private IMediaPlayer mediaPlayer = new IMediaPlayer() {

		@Override
		public void stop() throws IllegalStateException {
		    try{
		          mSohuVideoPlayer.stop(true);
		    }catch(Throwable t){
		    }
		}

		@Override
		public void start() throws IllegalStateException {
		    try{
		        mSohuVideoPlayer.play();            
		    }catch(Throwable t){
		    }
		}

		@Override
		public void setVolume(float leftVolume, float rightVolume) {
		}

		@Override
		public void setSurface(Surface surface) {			
		}

		@Override
		public void setScreenOnWhilePlaying(boolean screenOn) {
		}

		@Override
		public void setOnVideoSizeChangedListener(
				OnVideoSizeChangedListener listener) {			
		}

		@Override
		public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {

		}

		@Override
		public void setOnPreparedListener(OnPreparedListener listener) {

		}

		@Override
		public void setOnInfoListener(OnInfoListener listener) {
		}

		@Override
		public void setOnErrorListener(OnErrorListener listener) {			
		}

		@Override
		public void setOnCompletionListener(OnCompletionListener listener) {

		}

		@Override
		public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {

		}

		@Override
		public void setDisplay(SurfaceHolder arg0) {

		}

		@Override
		public void setDataSource(String path) throws IOException,
		IllegalArgumentException, SecurityException, IllegalStateException {			
		}

		@Override
		public void setDataSource(String path, Map<String, String> headers)
				throws IOException, IllegalArgumentException, SecurityException,
				IllegalStateException {			
		}

		@Override
		public void setDataSource(Context context, Uri uri) throws IOException,
		IllegalArgumentException, SecurityException, IllegalStateException {			
		}

		@Override
		public void setDataSource(Context context, Uri arg1,
				Map<String, String> headers) throws IOException,
				IllegalArgumentException, SecurityException, IllegalStateException {			
		}

		@Override
		public void seekTo(int ms) throws IllegalStateException {			
		}

		@Override
		public void reset() {			
		}

		@Override
		public void release() {			
		}

		@Override
		public void prepareAsync() throws IllegalStateException {			
		}

		@Override
		public void prepare() throws IOException, IllegalStateException {			
		}

		@Override
		public void pause() throws IllegalStateException {			
		}

		@Override
		public boolean isPlaying() {
		    return SohuVideoView.this.isPlaying();
		}

		@Override
		public int getVideoWidth() {
			return SohuVideoView.this.getVideoWidth();
		}

		@Override
		public int getVideoHeight() {
			return SohuVideoView.this.getVideoHeight();
		}

		@Override
		public int getDuration() {
		    return SohuVideoView.this.getDuration();
		}

		@Override
		public int getCurrentPosition() {
	          return SohuVideoView.this.getCurrentPosition();
		}
	};

	@Override
	public void attachAdView(AdView adView) {
		adView.setDisableView(true);
	}

	@Override
	public void setOnVideoLoadingListener(OnVideoLoadingListener loadingListener) {
		mOnVideoLoadingListener = loadingListener;
	}

	@Override
	public void continuePlay(PlayHistoryEntry history) {
		if(history != null && history.getPosition() > 5000){
			ToastBuilder.buildContinuePlay(mContext, history.getPosition()).show();
			seekTo(history.getPosition());
		}
	}

	public class SohuLayout extends RelativeLayout{

		public SohuLayout(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		public SohuLayout(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public SohuLayout(Context context) {
			super(context);
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent motion) {
			return super.dispatchTouchEvent(motion);
		}
	}

	@Override
	public void onActivityDestroy() {
	}

    @Override
    public void setForceFullScreen(boolean forceFullScreen) {
        if(mSohuScreenView != null){
            if(forceFullScreen){
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                mSohuScreenView.setLayoutParams(params);
            }else{
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                mSohuScreenView.setLayoutParams(params);
            }
        }
    }
    
    @Override
    public void requestVideoLayout() {
        if(mSohuScreenView != null){
            mSohuScreenView.requestLayout();
        }
    }

    @Override
    public void setPlayInfo(Object playInfo) {
    	
    }
        
	@Override
	public void adjustVideoPlayViewSize(int videoSizeStyle) {
    }

	@Override
	public boolean isSupportZoom() {
		return true;
	}
	
	private final SohuPlayerMonitor mSohuPlayerMonitor = new SohuPlayerMonitor() {
		@Override
		public void onPreparing() {
			Log.d(TAG, "onPreparing");
			super.onPreparing();
			mState = STATE_PREPARING;
			mIsBuffering = true;
			if(mOnVideoLoadingListener != null){
				mOnVideoLoadingListener.onVideoLoading(null);
			}
		}

		@Override
		public void onPrepared() {
			super.onPrepared();
			Log.d(TAG, "onPrepared: state " + mState);
			if(mState < STATE_PREPARED){
				if(mPreparedListener != null){
					mPreparedListener.onPrepared(mediaPlayer);
				}
			}
			mState = STATE_PREPARED;
		}

		@Override
		public void onStop() {
			Log.d(TAG, "onStop");
			super.onStop();
			mState = STATE_IDLE;
		}

		@Override
		public void onComplete() {
			Log.d(TAG, "onComplete");
			super.onComplete();
			if(mCompletionListener != null){
				mCompletionListener.onCompletion(mediaPlayer);
			}
			mState = STATE_IDLE;
		}

		@Override
		public void onBuffering(int progress) {
			super.onBuffering(progress);
			Log.d(TAG, "onBuffering: " + progress);
			if(mInfoListener != null){
				mInfoListener.onInfo(mediaPlayer, com.duokan.MediaPlayer.
						MEDIA_INFO_BUFFERING_UPDATE, progress);
			}
			if(!mIsBuffering && mInfoListener != null){
				mInfoListener.onInfo(mediaPlayer, MediaPlayer.MEDIA_INFO_BUFFERING_START, 0);
			}
			mIsBuffering = true;
		}

		@Override
		public void onPlay() {
			Log.d(TAG, "onPlay");
			super.onPlay();
			if(mIsBuffering && mInfoListener != null){
				mInfoListener.onInfo(mediaPlayer, MediaPlayer.MEDIA_INFO_BUFFERING_END, 0);
			}
			
			if(mOnVideoLoadingListener != null){
				mOnVideoLoadingListener.onVideoHideLoading(null);
			}
			mIsBuffering = false;
			mState = STATE_PLAYBACK;
		}

		@Override
		public void onVideoClick() {
			super.onVideoClick();
		}

		@Override
		public void onStartLoading() {
			Log.d(TAG, "onStartLoading");
			super.onStartLoading();
			mState = STATE_PREPARING;
			if(mOnVideoLoadingListener != null){
				mOnVideoLoadingListener.onVideoLoading(null);
			}
		}

		@Override
		public void onLoadSuccess() {
			Log.d(TAG, "onLoadSuccess");
			super.onLoadSuccess();
		}

		@Override
		public void onLoadFail(SohuPlayerLoadFailure failure) {
			Log.d(TAG, "onLoadFail");
			if(failure != null) {
			    Log.d(TAG, "onLoadFail: " +failure.toString());
			}
			super.onLoadFail(failure);
		}

		@Override
		public void onError(SohuPlayerError error) {
			Log.d(TAG, "onError: " + error);
			super.onError(error);
			if(mRetryTimes < 3){
				mRetryTimes++;
				mSohuVideoPlayer.pause();
				mSohuVideoPlayer.stop(true);
				mSohuVideoPlayer.play();
			}else{
				if(mErrorListener != null){
					mErrorListener.onError(mediaPlayer, 0, 0);
				}
				mState = STATE_IDLE;
			}
		}

		@Override
		public void onPause() {
			Log.d(TAG, "onPause");
			super.onPause();
		}

		@Override
		public void onPausedAdvertShown() {
			super.onPausedAdvertShown();
			Log.d(TAG, "onPausedAdvertShown():" + mSohuVideoPlayer.isAdvertInPlayback());
		}

		@Override
		public void onPlayItemChanged(SohuPlayerItemBuilder builder, int index) {
//			currentBuilder = builder;
//			if (mController == null) {
//				if (currentBuilder != null) {
//					mController = ControllerFactory.createController(
//							PlayerActivity.this, builder.getType());
//					mController.setVisibleChangeListener(PlayerActivity.this);
//				}
//				mController.setAnchorView(mScreenContainer);
//				mController.setPlayControlProxy(playerProxy);
//			}
//			mController.setTitle(builder.getTitle());
//			mController.setSid(builder.getAid());
//			mController.setVid(builder.getVid());
//			mController.setSite(builder.getSite());
//			mController.setCurrentPlayingIndex(currentBuilder, index);
		}

		@Override
		public void onProgressUpdated(int currentPosition, int duration) {
			Log.d(TAG, "currentPostion " + currentPosition + ", duration " + duration);
			super.onProgressUpdated(currentPosition, duration);
		}

		@Override
		public void onDefinitionChanged() {
//			if (mController != null) {
//				mController.updateDefinitionButton();
//			}
			super.onDefinitionChanged();
		}

		@Override
		public void onPreviousNextStateChange(boolean previous, boolean next) {
//			if (mController != null) {
//				mController.setPreviousNextState(previous, next);
//			}
			super.onPreviousNextStateChange(previous, next);
		}

		@Override
		public void onSkipHeader() {
			super.onSkipHeader();
		};

		@Override
		public void onSkipTail() {
			super.onSkipTail();
		}
		
		@Override
		public void onAppPlayOver() {
//			finish();
		};
	};
	
	private final SohuPlayerStatCallback mSohuPlayerStatCallback = new SohuPlayerStatCallback() {
		@Override
		public void onVV(SohuPlayerItemBuilder sohuPlayitemBuilder) {
			Log.d(TAG, "onVV");
		}

		@Override
		public void onRealVV(SohuPlayerItemBuilder sohuPlayitemBuilder,
				int loadingTime) {
			Log.d(TAG, "onRealVV, loadingTime:" + loadingTime);
		}

		@Override
		public void onHeartBeat(SohuPlayerItemBuilder sohuPlayitemBuilder,
				int currentTime) {
			Log.d(TAG, "onHeartBeat, currentTime:" + currentTime);
		}

		@Override
		public void onEnd(SohuPlayerItemBuilder sohuPlayitemBuilder,
				int timePlayed, boolean fromUser) {
			Log.d(TAG, "onEnd, Have played time:" + timePlayed + ",fromUser:"
					+ fromUser);
		}
	};

	@Override
	public boolean hasLoadingAfterAd() {
		return false;
	}

    @Override
    public int getRealPlayPosition() {
        return getCurrentPosition();
    }
}

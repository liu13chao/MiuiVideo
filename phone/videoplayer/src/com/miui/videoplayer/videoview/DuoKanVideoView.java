package com.miui.videoplayer.videoview;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.duokan.MediaPlayer.MediaInfo;
import com.miui.video.DKApp;
import com.miui.video.controller.MediaConfig;
import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.common.DisplayInformationFetcher;
import com.miui.videoplayer.common.DuoKanCodecConstants;
import com.miui.videoplayer.fragment.UIConfig;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.framework.ui.LocalVideoPlaySizeAdjustable;
import com.miui.videoplayer.media.AdsPlayListener;
import com.miui.videoplayer.media.DuoKanPlayer;
import com.miui.videoplayer.media.IMediaPlayer;
import com.miui.videoplayer.media.IMediaPlayer.OnBufferingUpdateListener;
import com.miui.videoplayer.media.IMediaPlayer.OnCompletionListener;
import com.miui.videoplayer.media.IMediaPlayer.OnErrorListener;
import com.miui.videoplayer.media.IMediaPlayer.OnInfoListener;
import com.miui.videoplayer.media.IMediaPlayer.OnPreparedListener;
import com.miui.videoplayer.media.IMediaPlayer.OnSeekCompleteListener;
import com.miui.videoplayer.media.IMediaPlayer.OnVideoSizeChangedListener;
import com.miui.videoplayer.media.OnMediaPlayerListener;
import com.miui.videoplayer.model.DuoKanMediaPlayer;
import com.miui.videoplayer.model.OriginMediaPlayer;
import com.miui.videoplayer.model.PlayerSettings;
import com.miui.videoplayer.model.SoundEffect;
import com.miui.videoplayer.widget.AdView;
import com.miui.videoplayer.widget.ToastBuilder;


public class DuoKanVideoView extends FrameLayout implements IVideoView, LocalVideoPlaySizeAdjustable{

    private String TAG = "DuoKanVideoView";
//    private Uri         mUri;
    private Map<String, String> mHeaders = new HashMap<String, String>();

    protected SurfaceView mSurfaceView;

    // All the stuff we need for playing and showing a video
    protected SurfaceHolder mSurfaceHolder = null;
    protected DuoKanPlayer mMediaPlayer = null;
    //	protected IDuoKanPlayer mPlayerImpl = null;
    //	protected IMediaPlayerX mPlayerEx = null;

    private int         mSeekWhenPrepared;  // recording the seek position while preparing
    private boolean     mCanPause = true;
    private boolean     mCanSeekBack = true;
    private boolean     mCanSeekForward = true;
    
    private String mUserAgent = "MiuiVideo/1.0";

    protected Context mContext;
    protected OnCompletionListener mOnCompletionListener;
    protected OnPreparedListener mOnPreparedListener;
    protected OnSeekCompleteListener mOnSeekCompleteListener;
    protected OnInfoListener mOnInfoListener;
    protected OnBufferingUpdateListener mOnBufferingUpdateListener;
    protected OnVideoSizeChangedListener mOnVideoSizeChangedListener;
    protected OnVideoLoadingListener mOnVideoLoadingListener;
    protected OnErrorListener mOnErrorListener;

    //	private long mTotalPausedTime = 0L;
    //	private long mLastPausedTimeStamp = 0L;

    private int mCurrentState = DuoKanPlayer.STATE_IDLE;
    private boolean mDelayOnPrepared = false;
    private boolean mIsActivityPaused = false;
    
    private Uri mUri;

    //for adjust the size of videoview
    private int mAdjustWidth;
    private int mAdjustHeight;
    private boolean mUserAdjustSize = false;
    private boolean mForceFullScreen = false;

    //end custom
    public DuoKanVideoView(Context context) {
        super(context);
        mContext = context;
        initVideoView();
    }

    public DuoKanVideoView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
        initVideoView();
    }

    public DuoKanVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initVideoView();
    }

    private void initVideoView() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        WebView wv = new WebView(getContext());
        mUserAgent = wv.getSettings().getUserAgentString();
        mUserAgent += " MiuiVideo/1.0";
        if(isSurfaceView()){
            mSurfaceView = new InnerSurfaceView(mContext);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            mSurfaceView.setLayoutParams(params);
            addView(mSurfaceView);
        }
    }

    private void setVideoUri(Uri uri) {
        setVideoUri(uri, null);
    }

    private Uri getRealUri(Uri uri){
        if(uri != null){
            String url = uri.toString();
            if(url != null && url.startsWith("file:///content")){
                url = url.substring(15, url.length());
                try{
                    url = URLDecoder.decode(url, "utf-8");
                    int pos = url.indexOf("/");
                    if(pos >= 0){
                        return Uri.parse("content://" + url.substring(pos + 1, url.length()));
                    }
                }catch(Exception e){
                }
            }
        }
        return uri;
    }

    /**
     * @hide
     */
    private void setVideoUri(Uri uri, Map<String, String> headers) {
        mUri = getRealUri(uri);
        if(headers != null){
            mHeaders = headers;
        }
        mHeaders.put("user-agent", mUserAgent);
        mSeekWhenPrepared = 0;
        if(uri == null){
            throw new IllegalArgumentException("uri can not be empty.");
        }
        if(mMediaPlayer != null){
            mMediaPlayer.reset();
        }else{
            mMediaPlayer = createMediaPlayer();
            initMediaPlayer(mMediaPlayer);
        }
        Log.d(TAG, "create mediaplayer");
        if(mSurfaceHolder != null){
            prepareMediaPlayer(mMediaPlayer, uri);
        }
        requestLayout();
        invalidate();
    }

    public void stop() {
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
        }
    }

    protected boolean isSurfaceView(){
        return true;
    }

    private boolean isSurfaceCreated() {
        return mSurfaceHolder != null;
    }

    protected DuoKanPlayer createMediaPlayer(){
        DuoKanPlayer mediaPlayer = null;
        if(DuoKanCodecConstants.sUseDuokanCodec && !AndroidUtils.isRtspVideo(mUri)){
            mediaPlayer = new DuoKanPlayer(new DuoKanMediaPlayer());
        }else{
            mediaPlayer = new DuoKanPlayer(new OriginMediaPlayer());
        }
        return mediaPlayer;
    }

    protected void initMediaPlayer(DuoKanPlayer mediaPlayer){
        mediaPlayer.setMediaPlayerListener(mMediaPlayerListener);
        if(mSurfaceHolder != null){
            mediaPlayer.setDisplay(mSurfaceHolder);
        }
    }

    protected void prepareMediaPlayer(DuoKanPlayer player, Uri uri){
        try{
            Log.i(TAG, "setDataSource: " + uri);
            if(mHeaders != null){
                player.setDataSource(mContext, uri, mHeaders);
            }else{
                player.setDataSource(mContext, uri);
            }
            Log.i(TAG, "setDataSource: end " + uri);
            mDelayOnPrepared = false;
            player.prepareAsync();
            if(mOnVideoLoadingListener != null){
                mOnVideoLoadingListener.onVideoLoading(this);
            }
        }catch (Exception e) {
            Log.w(TAG, "Unable to open content: " + uri, e);
            if(mInnerErrorListener != null){
                mInnerErrorListener.onError(player, IMediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            }
        }
    }

    public void handleOnPrepared(){
        if(mMediaPlayer == null){
            // no media player, just return.
            return;
        }
        Log.d(TAG, "onPrepared");
        mDelayOnPrepared = false;
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(mMediaPlayer);
        }
        if(isPlayingState()){
            start();
        }
        if (mSeekWhenPrepared != 0) {
            seekTo(mSeekWhenPrepared); 
        }
        mSeekWhenPrepared = 0;
        if(mSurfaceView != null){
            mSurfaceView.requestLayout();
            mSurfaceView.invalidate();
        }
    }

    IMediaPlayer.OnVideoSizeChangedListener mInnerSizeChangedListener =
            new IMediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height) {
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();
            if (mSurfaceView != null && videoWidth != 0 && videoHeight != 0) {
                mSurfaceView.getHolder().setFixedSize(videoWidth, 
                        ((videoHeight == 1088) ? 1080 : videoHeight));
            }
            if (mOnVideoSizeChangedListener != null) {
                mOnVideoSizeChangedListener.onVideoSizeChanged(mp, width, height);
            }
        }
    };

    IMediaPlayer.OnPreparedListener mInnerPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            if(mSurfaceHolder == null){
                mDelayOnPrepared = true;
                return;
            }
            handleOnPrepared();
        }
    };

    private IMediaPlayer.OnCompletionListener mInnerCompletionListener =
            new IMediaPlayer.OnCompletionListener() {
        public void onCompletion(IMediaPlayer mp) {
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
        }
    };

    private IMediaPlayer.OnInfoListener mInnerInfoListener =
            new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(mMediaPlayer, what, extra);
            }
            return false;
        }
    };

    private IMediaPlayer.OnSeekCompleteListener mInnerSeekCompleteListener =
            new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            if(mOnSeekCompleteListener != null){
                mOnSeekCompleteListener.onSeekComplete(mMediaPlayer);
            }
        }
    };

    private IMediaPlayer.OnErrorListener mInnerErrorListener =
            new IMediaPlayer.OnErrorListener() {
        public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
            Log.e(TAG, "Error: " + framework_err + "," + impl_err);
            /* If an error handler has been supplied, use it and finish. */
            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                    return true;
                }
            }
            return true;
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener mInnerBufferingUpdateListener =
            new IMediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            Log.e("buffered percent: ", percent + "");
            if (mOnBufferingUpdateListener != null) {
                mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
            }
        }
    };

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, VideoView will inform
     * the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void setOnInfoListener(OnInfoListener listener) {
        mOnInfoListener = listener;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        mOnSeekCompleteListener = listener;
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener) {
        this.mOnBufferingUpdateListener = onBufferingUpdateListener;
    }

    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {
        this.mOnVideoSizeChangedListener = onVideoSizeChangedListener;
    }

    /*
     * release the media player in any state
     */
    protected void release() {
        if(mMediaPlayer != null){
            Log.d(TAG, "release enter.");
            mMediaPlayer.reset();
            mMediaPlayer.release();
            Log.d(TAG, "release exit.");
            SoundEffect.turnOnMusicMode();
        }
        mMediaPlayer = null;
    }

    public void start() {
        mCurrentState = DuoKanPlayer.STATE_PLAYING;
        if(mMediaPlayer != null){
            if (!isSurfaceView() || isSurfaceCreated()) {
                setKeepScreenOn(true);
                mMediaPlayer.start();
            }
            if(mMediaPlayer.isInPlaybackState() && DKApp.getSingleton(PlayerSettings.class).isAudioEffectOn()){
                SoundEffect.turnOnMovieMode(true);
            }
        }
    }

    public void pause() {
        mCurrentState = DuoKanPlayer.STATE_PAUSED;
        if(mMediaPlayer != null){
            if (!isSurfaceView() || isSurfaceCreated()) {
                Log.i(TAG, "media player handlePause.");
                mMediaPlayer.pause();
            }
        }
    }

    // cache duration as mDuration for faster access
    public int getDuration() {
        if(mMediaPlayer != null){
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentPosition() {
        if(mMediaPlayer != null){
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getVideoWidth() {
        if(mMediaPlayer != null){
            return mMediaPlayer.getVideoWidth();
        }else{
            return 0;
        }
    }

    public int getVideoHeight() {
        if(mMediaPlayer != null){
            return mMediaPlayer.getVideoHeight();
        }else{
            return 0;
        }
    }

    public void seekTo(int msec) {
        if(mMediaPlayer != null){
            if(mMediaPlayer.isInPlaybackState()){
                Log.d(TAG, "seekTo " + msec);
                mMediaPlayer.seekTo(msec);
                mSeekWhenPrepared = 0;
            } else {
                mSeekWhenPrepared = msec;
            }
        }
    }

    public boolean isPlaying() {
        if(mMediaPlayer != null){
            return mMediaPlayer.isPlaying();
        }
        return false;
    }
    
    

    @Override
    public boolean isInPlaybackState() {
        if(mMediaPlayer != null){
            return mMediaPlayer.isInPlaybackState();
        }
        return false;
    }

    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getBufferPercentage();
        }
        return 0;
    }

    public boolean canPause() {
        Uri uri = getUri();
        if(uri != null && "rtsp".equals(uri.getScheme()) && getDuration() <= 0){
            return false;
        }
        return mCanPause;
    }

    public boolean canSeekBackward() {
        Uri uri = getUri();
        if(uri != null && "rtsp".equals(uri.getScheme()) && getDuration() <= 0){
            return false;
        }
        return mCanSeekBack;
    }

    public boolean canSeekForward() {
        Uri uri = getUri();
        if(uri != null && "rtsp".equals(uri.getScheme()) && getDuration() <= 0){
            return false;
        }
        return mCanSeekForward;
    }

    public boolean isPlayingState(){
        return mCurrentState == DuoKanPlayer.STATE_PLAYING  || (
                mMediaPlayer != null && mMediaPlayer.isPlayingState());
    }


    public int getAdjustWidth() {
        return mAdjustWidth;
    }

    public int getAdjustHeight() {
        return mAdjustHeight;
    }
    //		@Override
    //		public boolean enableMultiSpeedPlayback(int speed, boolean forward) {
    //			return false;
    //		}
    //		@Override
    //		public boolean disableMultiSpeedPlayback() {
    //			return false;
    //		}
    //		@Override
    //		public void setOnTimedTextListener(OnTimedTextListener listener) {
    //		}
    //		@Override
    //		public boolean setOutOfBandTextSource(String sourceUri) {
    //			return false;
    //		}
    //		@Override
    //		public long getPausedTotalTime() {
    //			return this.mTotalPausedTime;
    //		}

    public void set3dMode(boolean mode) {
    }

    public boolean get3dMode() {
        return false;
    }

    @Override
    public View asView() {
        return this;
    }

    @Override
    public boolean canBuffering() {
        Uri uri = getUri();
        if(uri != null){
            String scheme = uri.getScheme();
            Log.i(TAG, "check network");
            Log.i(TAG, "scheme: " + scheme);
            if (AndroidUtils.isOnlineVideo(uri) || AndroidUtils.isSmbVideo(uri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setDataSource(String uri) {
        setVideoUri(Uri.parse(uri));
    }

    @Override
    public void setDataSource(String uri, Map<String, String> headers) {
        setVideoUri(Uri.parse(uri), headers);
    }

    @Override
    public void close() {
        if(mMediaPlayer != null && !mMediaPlayer.isReleased()){
            resetState();
            mOnCompletionListener = null;
            mOnErrorListener = null;
            mOnPreparedListener = null;
            mOnSeekCompleteListener = null;
            mOnInfoListener = null;
            Log.i(TAG, "close VideoView");
            release();
        }
    }

    private void resetState(){
        mSeekWhenPrepared = 0;
        mCurrentState = DuoKanPlayer.STATE_IDLE;
        mDelayOnPrepared = false;
    }

    @Override
    public boolean isAdsPlaying() {
        return false;
    }

    @Override
    public Uri getUri() {
        if(mMediaPlayer != null && mMediaPlayer.getUri() != null){
            return mMediaPlayer.getUri();
        }
        return mUri;
    }

    @Override
    public boolean isAirkanEnable() {
        Uri uri = getUri();
        return !MediaConfig.isOfflineUri(uri);
    }

    @Override
    public void onActivityPause() {
        if(mMediaPlayer != null){
            mMediaPlayer.onActivityPause();
        }
        mIsActivityPaused = true;
    }

    @Override
    public void onActivityResume() {
        if(mMediaPlayer != null){
            mMediaPlayer.onActivityResume();
        }
        mIsActivityPaused = false;
    }

    @Override
    public void setAdsPlayListener(AdsPlayListener adPlayListener) {
    }

    @Override
    public MediaInfo getMediaInfo() {
        if(mMediaPlayer != null ){
            return mMediaPlayer.getMediaInfo();
        }else{
            return null;
        }
    }
    @Override
    public void attachAdView(AdView adView) {
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

    @Override
    public void onActivityDestroy() {
    }

    protected void onSurfaceCreated(SurfaceHolder holder){
    }

    protected void onSurfaceChanged(SurfaceHolder holder, int format, int w, int h){
    }

    protected void onSurfaceDestroyed(SurfaceHolder holder){
    }

    protected void attachDuoKanPlayer(DuoKanPlayer player){
        if(player == null){
            return;
        }
        Log.d(TAG, "attachDuoKanPlayer.");
        resetState();
        release();
        mMediaPlayer = player;
        initMediaPlayer(mMediaPlayer);
        start();
        if(mMediaPlayer.isPrepared()){
            handleOnPrepared();
        }
    }

    protected class InnerSurfaceView extends SurfaceView{

        public InnerSurfaceView(Context context, AttributeSet attrs,
                int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        public InnerSurfaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public InnerSurfaceView(Context context) {
            super(context);
            init();
        }

        private void init(){
            getHolder().addCallback(mSHCallback);
        }

        private SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
            public void surfaceChanged(SurfaceHolder holder, int format,
                    int w, int h) {
                Log.i(TAG, "surfaceChanged!!!");
                if(mMediaPlayer != null){
                    if(isPlayingState())
                        start();
                }
                onSurfaceChanged(holder, format,  w, h);
            }

            public void surfaceCreated(SurfaceHolder holder) {
                Log.i(TAG, "surfaceCreated");
                if(mIsActivityPaused){
                    return;
                }
                mSurfaceHolder = holder;
                if(mMediaPlayer == null){
                    mMediaPlayer = createMediaPlayer();
                    initMediaPlayer(mMediaPlayer);
                }
                mMediaPlayer.setDisplay(mSurfaceHolder);
                mMediaPlayer.setScreenOnWhilePlaying(true);
                if(mMediaPlayer.isIdle() && mUri != null){
                    prepareMediaPlayer(mMediaPlayer, mUri);
                }else if(mDelayOnPrepared){
                    handleOnPrepared();
                }
                if(isPlayingState()){
                    start();
                }
                onSurfaceCreated(holder);
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "surface destroyed!!!");
                if(mSurfaceHolder != null){
                    // after we return from this we can't use the surface any more
                    mSurfaceHolder = null;
                    if(mMediaPlayer != null){
                        mUri = mMediaPlayer.getUri();
                    }
                    release();
                    Log.i(TAG, "surfaceDestroyed done" );
                    onSurfaceDestroyed(holder);
                }
            }
        };

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//            Log.d(TAG, "onMeasure...... ");
            if(!mForceFullScreen){
                int videoWidth = getVideoWidth();
                int videoHeight = getVideoHeight();
                int width = getDefaultSize(videoWidth, widthMeasureSpec);
                int height = getDefaultSize(videoHeight, heightMeasureSpec);
                if (videoWidth > 0 && videoHeight > 0) {
                    if ( videoWidth * height  > width * videoHeight ) {
                        //Log.i("@@@", "image too tall, correcting");
                        height = width * videoHeight / videoWidth;
                    } else if ( videoWidth * height  < width * videoHeight ) {
                        //Log.i("@@@", "image too wide, correcting");
                        width = height * videoWidth / videoHeight;
                    } else {
                        //Log.i("@@@", "aspect ratio is correct: " +
                        //width+"/"+height+"="+
                        //mVideoWidth+"/"+mVideoHeight);
                    }
                }
                Log.d(TAG, "setting size: " + width + 'x' + height);
                if (mUserAdjustSize) {
                    width = mAdjustWidth;
                    height = mAdjustHeight;
                }
                setMeasuredDimension(width, height);
            }else{
                super.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
            }
            if(mSurfaceView != null && mSurfaceView.getHolder() != null){
                mSurfaceView.getHolder().setFixedSize(-1, -1);
            }
        }
    }

    @Override
    public void setForceFullScreen(boolean forceFullScreen) {
        mForceFullScreen = forceFullScreen;
        if(mSurfaceView != null){
            mSurfaceView.requestLayout();
        }
    }

    @Override
    public void setPlayInfo(Object playInfo) {
    }
    public void getVideoPlayViewSize(int videoSizeStyle) {
        int screenWidth = DisplayInformationFetcher.getInstance(mContext).getScreenWidth();
        int screenHeight = DisplayInformationFetcher.getInstance(mContext).getScreenHeight();
        int mVideoWidth = getVideoWidth();
        int mVideoHeight = getVideoHeight();
        if(mVideoWidth == 0 || mVideoHeight == 0){
            return;
        }

        switch (videoSizeStyle) {
        case UIConfig.VIDEO_SIZE_STYLE_AUTO:
            mAdjustWidth = -1;
            mAdjustHeight = -1;
            mUserAdjustSize = false;
            break;
        case UIConfig.VIDEO_SIZE_STYLE_FULL_SCREEN:
            mAdjustWidth = screenWidth;
            mAdjustHeight = screenHeight;
            mUserAdjustSize = true;
            break;
        case UIConfig.VIDEO_SIZE_STYLE_ADAPT_WIDTH:
            mAdjustWidth = screenWidth;
            mAdjustHeight = mAdjustWidth * mVideoHeight / mVideoWidth;
            if(mAdjustHeight > screenHeight){
                mAdjustHeight = screenHeight;
            }
            mUserAdjustSize = true;
            break;
        case UIConfig.VIDEO_SIZE_STYLE_ADAPT_HEIGHT:
            mAdjustHeight = screenHeight;
            mAdjustWidth = mAdjustHeight *  mVideoWidth / mVideoHeight;
            if(mAdjustWidth > screenWidth){
                mAdjustWidth = screenWidth;
            }
            mUserAdjustSize = true;
            break;
        case UIConfig.VIDEO_SIZE_STYLE_16_9:
            if (9 * screenWidth >= 16 * screenHeight) {
                mAdjustWidth = screenHeight * 16 / 9;
                mAdjustHeight = screenHeight;
            } else {
                mAdjustWidth = screenWidth;
                mAdjustHeight = screenWidth * 9 / 16;
            }
            mUserAdjustSize = true;
            break;
        case UIConfig.VIDEO_SIZE_STYLE_4_3:
            if (3 * screenWidth >= 4 * screenHeight) {
                mAdjustWidth = screenHeight * 4 / 3;
                mAdjustHeight = screenHeight;
            } else {
                mAdjustWidth = screenWidth;
                mAdjustHeight = screenWidth * 3 / 4;
            }
            mUserAdjustSize = true;
            break;			
        default:
            break;
        }
    }

    @Override
    public void adjustVideoPlayViewSize(int videoSizeStyle) {
        getVideoPlayViewSize(videoSizeStyle);
        if(mUserAdjustSize){
            LayoutParams lp = new android.widget.FrameLayout.LayoutParams(mAdjustWidth,  mAdjustHeight);
            lp.gravity = Gravity.CENTER;
            this.setLayoutParams(lp);
            if(mSurfaceView != null){
                mSurfaceView.getHolder().setFixedSize(mAdjustWidth, mAdjustHeight);
            }
        }else{
            if(mSurfaceView != null){
                mSurfaceView.getHolder().setFixedSize(-1, -1);
            }
        }
    }

    private OnMediaPlayerListener mMediaPlayerListener = new OnMediaPlayerListener(){

        @Override
        public IMediaPlayer.OnErrorListener getOnErrorListener() {
            return mInnerErrorListener;
        }

        @Override
        public IMediaPlayer.OnCompletionListener getOnCompletionListener() {
            return mInnerCompletionListener;
        }

        @Override
        public IMediaPlayer.OnPreparedListener getOnPreparedListener() {
            return mInnerPreparedListener;
        }

        @Override
        public OnSeekCompleteListener getOnSeekCompleteListener() {
            return mInnerSeekCompleteListener;
        }

        @Override
        public IMediaPlayer.OnInfoListener getOnInfoListener() {
            return mInnerInfoListener;
        }

        @Override
        public IMediaPlayer.OnBufferingUpdateListener getOnBufferingUpdateListener() {
            return mInnerBufferingUpdateListener;
        }

        @Override
        public IMediaPlayer.OnVideoSizeChangedListener getOnVideoSizeChangedListener() {
            return mInnerSizeChangedListener;
        }
    };

    @Override
    public boolean isSupportZoom() {
        return true;
    }

    @Override
    public boolean hasLoadingAfterAd() {
        return true;
    }

    @Override
    public void requestVideoLayout() {
        if(mSurfaceView != null){
            mSurfaceView.requestLayout();
        }
    }

    @Override
    public int getRealPlayPosition() {
        return getCurrentPosition();
    }

}

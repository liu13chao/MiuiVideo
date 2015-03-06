/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   JiashiVideoView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014年9月19日
 */
package com.miui.video.live;

import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.miui.videoplayer.ads.AdBean;
import com.miui.videoplayer.ads.AdUtils;
import com.miui.videoplayer.ads.AdsDelegate;
import com.miui.videoplayer.videoview.AdsVideoView;
import com.miui.videoplayer.media.DuoKanPlayer;
import com.tvplayer.play.IMediaPlayer;
import com.tvplayer.play.IMediaPlayer.OnBufferingUpdateListener;
import com.tvplayer.play.IMediaPlayer.OnCompletionListener;
import com.tvplayer.play.IMediaPlayer.OnErrorListener;
import com.tvplayer.play.IMediaPlayer.OnInfoListener;
import com.tvplayer.play.IMediaPlayer.OnPreparedListener;
import com.tvplayer.play.IMediaPlayer.OnSeekCompleteListener;
import com.tvplayer.play.IMediaPlayer.OnVideoSizeChangedListener;
import com.tvplayer.play.TVPlayer;

/**
 * @author tianli
 *
 */
public class JiashiVideoView extends AdsVideoView{

    public static final String TAG = "JiashiVideoView";
    
    TVPlayer mTvPlayer;
    
    private OnVideoSizeChangedListener mExOnVideoSizeChangedListener;
    private OnBufferingUpdateListener mExOnBufferingUpdateListener;
    private OnCompletionListener mExOnCompletionListener;
    private OnErrorListener mExOnErrorListener;
    private OnInfoListener mExOnInfoListener;
    private OnPreparedListener  mExOnPreparedListener;
    private OnSeekCompleteListener mExOnSeekCompleteListener;
    
    private boolean mPlayDelayed = false;
    private String mEpgId;
    
    private LivePlayInfo mTvPlayInfo;
    
    private boolean mPause = false;
    
    public JiashiVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public JiashiVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JiashiVideoView(Context context) {
        super(context);
    }
    
    private void playTv(){
        Log.d(TAG, "play tvid : " + mEpgId);
        mMediaPlayer = createMediaPlayer();
        initMediaPlayer(mMediaPlayer);
        mTvPlayer = new TVPlayer(getContext(), mSuperTVPlayer, mSurfaceHolder); 
        mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
        mMediaPlayer.setOnCompletionListener(mCompletionListener);
        mMediaPlayer.setOnErrorListener(mErrorListener);
        mMediaPlayer.setOnInfoListener(mInfoListener);
        mMediaPlayer.setOnPreparedListener(mPreparedListener);
        mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
        mTvPlayer.play(mEpgId);
        mPlayDelayed = false;
        if(mOnVideoLoadingListener != null){
            mOnVideoLoadingListener.onVideoLoading(this);
        }
    }

    @Override
    public void setPlayInfo(Object playInfo) {
        super.setPlayInfo(playInfo);
        if(playInfo instanceof LivePlayInfo){
            mTvPlayInfo = (LivePlayInfo)playInfo;
        }
    }

    @Override
    public void setDataSource(String uri) {
        setDataSource(uri, null);
    }

    @Override
    public void setDataSource(String uri, Map<String, String> headers) {
        mEpgId = uri;
        Log.d(TAG, "setDataSource : " + mEpgId);
        startAdsPlay();
        if(mOnVideoLoadingListener != null){
            mOnVideoLoadingListener.onVideoLoading(this);
        }
    }
    
    @Override
    public void close() {
        super.close();
        Log.d(TAG, "release TvPlayer");
        if(mTvPlayer != null){
            mTvPlayer.release();
        }
    }

    @Override
    protected AdBean requestAd() {
        int tvId = 0, source = 0;
        String programe = "";
        if(mTvPlayInfo != null){
            tvId = mTvPlayInfo.getTvId();
            source = mTvPlayInfo.getSource();
            programe = mTvPlayInfo.getTvPrograme();
        }
        Log.d(TAG, "tvId = " + tvId + ", programe = " + programe);
        return AdsDelegate.getDefault(getContext()).getAdUrl(tvId, source, programe);
    }
    
    @Override
    protected String getAdExtraJson() {
        try{
            int tvId = 0, source = 0;
            String programe = "";
            if(mTvPlayInfo != null){
                tvId = mTvPlayInfo.getTvId();
                source = mTvPlayInfo.getSource();
                programe = mTvPlayInfo.getTvPrograme();
            }
            return AdUtils.buildLiveJson(tvId, source, programe).toString();
        }catch(Exception e){
        }
        return null;
        
    }

    @Override
    protected void onAdsPlayEnd() {
        super.onAdsPlayEnd();
        prepare2PlayTv();
    }
    
    public void prepare2PlayTv(){
        if(mSurfaceHolder != null){
            if(!TextUtils.isEmpty(mEpgId)){
                playTv();
            }
        }else{
            mPlayDelayed = true;
        }
        start();
    }

    @Override
    public void onActivityPause() {
        super.onActivityPause();
        mPause = true;
        release();
        if(mTvPlayer != null){
            mTvPlayer.release();
        }
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        if(mPause){
            mPause = false;
            if(!TextUtils.isEmpty(mEpgId)){
                if(!isAdsPlaying()){
                    prepare2PlayTv();  
                }
            }
        }
    }

    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }
    
    @Override
    protected void onSurfaceCreated(SurfaceHolder holder) {
        super.onSurfaceCreated(holder);
        if(!isAdsPlaying()){
            if(!TextUtils.isEmpty(mEpgId) && mPlayDelayed){
                playTv();
            }
        }
    }

    @Override
    protected void onSurfaceChanged(SurfaceHolder holder, int format, int w,
            int h) {
        super.onSurfaceChanged(holder, format, w, h);
    }

    @Override
    protected void onSurfaceDestroyed(SurfaceHolder holder) {
        super.onSurfaceDestroyed(holder);
    }


    private IMediaPlayer mSuperTVPlayer = new IMediaPlayer(){
        @Override
        public int getCurrentPosition() {
            if(mMediaPlayer == null){
                return 0;
            }
            return mMediaPlayer.getCurrentPosition();
        }

        @Override
        public int getCurrentState() {
            int state = mMediaPlayer.getCurrentState();
            if(state == DuoKanPlayer.STATE_IDLE){
                return IMediaPlayer.STATE_IDLE;
            }else if(state == DuoKanPlayer.STATE_PAUSED){
                return IMediaPlayer.STATE_PAUSED;
            }else if(state == DuoKanPlayer.STATE_PLAYING){
                return IMediaPlayer.STATE_STARTED;
            }else if(state == DuoKanPlayer.STATE_PREPARING){
                return IMediaPlayer.STATE_PREPARING;
            }else if(state == DuoKanPlayer.STATE_PREPARED){
                return IMediaPlayer.STATE_PREPARED;
            }else if(state == DuoKanPlayer.STATE_PLAYBACK_COMPLETED){
                return IMediaPlayer.STATE_PLAYBACK_COMPLETE;
            }else if(state == DuoKanPlayer.STATE_ERROR){
                return IMediaPlayer.STATE_ERROR;
            }
            return IMediaPlayer.STATE_IDLE;
        }

        @Override
        public int getDuration() {
            if(mMediaPlayer == null){
                return 0;
            }
            return mMediaPlayer.getDuration();
        }

        @Override
        public int getVideoHeight() {
            if(mMediaPlayer == null){
                return 0;
            }
            return mMediaPlayer.getVideoHeight();
        }

        @Override
        public int getVideoWidth() {
            if(mMediaPlayer == null){
                return 0;
            }
            return mMediaPlayer.getVideoWidth();
        }

        @Override
        public boolean isPlaying() {
            if(mMediaPlayer == null){
                return false;
            }
            return mMediaPlayer.isPlaying();
        }

        @Override
        public void pause() {
            if(mMediaPlayer == null){
                return;
            }
            mMediaPlayer.pause();
        }

        @Override
        public void prepare() {
            if(mMediaPlayer == null){
                return;
            }
            try{
                if(mMediaPlayer.isIdle()){
                    mMediaPlayer.prepare();
                }
            }catch(Exception e){
            }
        }

        @Override
        public void prepareAsync() {
            if(mMediaPlayer == null){
                return;
            }
            try{
                if(mMediaPlayer.isIdle()){
                    mMediaPlayer.prepareAsync();
                }
            }catch(Exception e){
            }
        }

        @Override
        public void release() {
            if(mMediaPlayer == null){
                return;
            }
            mMediaPlayer.release();
        }

        @Override
        public void reset() {
            if(mMediaPlayer == null){
                return;
            }
            mMediaPlayer.reset();
        }

        @Override
        public void seekTo(int ms) {
            if(mMediaPlayer == null){
                return;
            }
            mMediaPlayer.seekTo(ms);
        }

        @Override
        public void setAudioStreamType(int arg0) {
        }

        @Override
        public void setDataSource(String path) {
            if(mMediaPlayer == null){
                return;
            }
            if(mMediaPlayer.isIdle()){
                try{
                    mMediaPlayer.setDataSource(path);
                }catch(Exception e){
                }
            }
        }

        @Override
        public void setDataSource(Context context, Uri uri) {
            if(mMediaPlayer == null){
                return;
            }
            if(mMediaPlayer.isIdle()){
                try{
                    mMediaPlayer.setDataSource(context, uri);
                }catch(Exception e){
                }
            }
        }

        @Override
        public void setDisplay(SurfaceHolder arg0) {
        }

        @Override
        public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
            mExOnBufferingUpdateListener = listener;
        }

        @Override
        public void setOnCompletionListener(OnCompletionListener listener) {
            mExOnCompletionListener = listener;
        }

        @Override
        public void setOnErrorListener(OnErrorListener listener) {
            mExOnErrorListener = listener;
        }

        @Override
        public void setOnInfoListener(OnInfoListener listener) {
            mExOnInfoListener = listener;
        }

        @Override
        public void setOnPreparedListener(OnPreparedListener listener) {
            mExOnPreparedListener = listener;
        }

        @Override
        public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
            mExOnSeekCompleteListener = listener;
        }

        @Override
        public void setOnVideoSizeChangedListener(
                OnVideoSizeChangedListener listener) {
            mExOnVideoSizeChangedListener = listener;
        }

        @Override
        public void setScreenOnWhilePlaying(boolean arg0) {
        }

        @Override
        public void setSurfaceType(SurfaceView arg0) {
        }

        @Override
        public void start() {
            if(mMediaPlayer == null){
                return;
            }
            mMediaPlayer.start();
        }

        @Override
        public void stop() {
            if(mMediaPlayer == null){
                return;
            }
            mMediaPlayer.stop();
        }
    };
    
    private  com.miui.videoplayer.media.IMediaPlayer.OnErrorListener 
    mErrorListener = new  com.miui.videoplayer.media.IMediaPlayer.OnErrorListener(){
    	@Override
    	public boolean onError(com.miui.videoplayer.media.IMediaPlayer mp, int what,
    			int extra) {
    		if(mExOnErrorListener != null){
    			return mExOnErrorListener.onError(mSuperTVPlayer, what, extra);
    		}
    		return false;
    	}
    };

    private  com.miui.videoplayer.media.IMediaPlayer.OnCompletionListener 
    mCompletionListener = new  com.miui.videoplayer.media.IMediaPlayer.OnCompletionListener() {
    	@Override
    	public void onCompletion(com.miui.videoplayer.media.IMediaPlayer mp) {
    		if(mExOnCompletionListener != null){
    			mExOnCompletionListener.onCompletion(mSuperTVPlayer);
    		}
    	}
    };

    private  com.miui.videoplayer.media.IMediaPlayer.OnPreparedListener 
    mPreparedListener = new  com.miui.videoplayer.media.IMediaPlayer.OnPreparedListener() {
    	@Override
    	public void onPrepared(com.miui.videoplayer.media.IMediaPlayer mp) {
    		if(mExOnPreparedListener != null){
    			mExOnPreparedListener.onPrepared(mSuperTVPlayer);
    		}
    	}
    };

    private com.miui.videoplayer.media.IMediaPlayer.OnSeekCompleteListener
    mSeekCompleteListener = new com.miui.videoplayer.media.IMediaPlayer.OnSeekCompleteListener(){
    	@Override
    	public void onSeekComplete(
    			com.miui.videoplayer.media.IMediaPlayer mp) {
    		if(mExOnSeekCompleteListener != null){
    			mExOnSeekCompleteListener.onSeekComplete(mSuperTVPlayer);
    		}
    	}
    };

    private com.miui.videoplayer.media.IMediaPlayer.OnInfoListener 
    mInfoListener = new com.miui.videoplayer.media.IMediaPlayer.OnInfoListener(){
    	@Override
    	public boolean onInfo(
    			com.miui.videoplayer.media.IMediaPlayer mp, int what, int extra) {
    		if(mExOnInfoListener != null){
    			return mExOnInfoListener.onInfo(mSuperTVPlayer, what, extra);
    		}
    		return false;
    	}
    };

    private com.miui.videoplayer.media.IMediaPlayer.OnBufferingUpdateListener
    mBufferingUpdateListener = new  com.miui.videoplayer.media.IMediaPlayer.OnBufferingUpdateListener(){
    	@Override
    	public void onBufferingUpdate(
    			com.miui.videoplayer.media.IMediaPlayer mp, int percent) {
    		if(mExOnBufferingUpdateListener != null){
    			mExOnBufferingUpdateListener.onBufferingUpdate(mSuperTVPlayer, percent);
    		}
    	}
    };

    private com.miui.videoplayer.media.IMediaPlayer.OnVideoSizeChangedListener 
    mVideoSizeChangedListener = new com.miui.videoplayer.media.IMediaPlayer.OnVideoSizeChangedListener(){
    	@Override
    	public void onVideoSizeChanged(
    			com.miui.videoplayer.media.IMediaPlayer mp, int width, int height) {
    		if(mExOnVideoSizeChangedListener != null){
    			mExOnVideoSizeChangedListener.onVideoSizeChanged(mSuperTVPlayer, width, height);
    		}
    	}
    };
}

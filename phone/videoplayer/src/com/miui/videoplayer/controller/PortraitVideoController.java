/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   PortraitVideoController.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-26
 */
package com.miui.videoplayer.controller;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.miui.video.R;
import com.miui.videoplayer.controller.ControllerView.OnControlEventListener;
import com.miui.videoplayer.media.MediaPlayerControl;
import com.miui.videoplayer.videoview.IVideoView;
import com.miui.videoplayer.widget.VideoProgressView;

/**
 * @author tianli
 *
 */
public class PortraitVideoController extends RelativeLayout implements 
OnControlEventListener, IVideoLifeCycle{

    private VideoProgressView mProgressView;
    private ImageView mPlayIcon;
    private ImageView mFullscreen;
    
    private MediaPlayerControl mPlayer;
    private OrientationUpdater mOrientationUpdater;
    
    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    
    public PortraitVideoController(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public PortraitVideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitVideoController(Context context) {
        super(context);
    }
    
    public void attachMediaPlayer(MediaPlayerControl player){
        mPlayer = player;
    }
    
    public void attachActivity(Activity activity, FrameLayout anchor, 
            OrientationUpdater orientationUpdater){
        mOrientationUpdater = orientationUpdater;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mProgressView = (VideoProgressView)findViewById(R.id.video_portrait_progress);
        mPlayIcon = (ImageView)findViewById(R.id.video_portrait_play);
        mFullscreen = (ImageView)findViewById(R.id.video_portrait_fullscreen);
        setOnClickListener(mOnClickListener);
        mFullscreen.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onTouchMove(int region, float movementX, float movementY) {
    }

    @Override
    public void onTouchUp(int region) {
    }

    @Override
    public void onTap(int region) {
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(changedView == this){
            mUIHandler.removeCallbacks(mQueryPositionRunner);
            if(visibility == View.VISIBLE){
                mUIHandler.post(mQueryPositionRunner);
                refreshViews();
            }
        }
    }
    
    private void togglePlayer(){
        if(mPlayer != null){
            if(mPlayer.isPlaying()){
                mPlayer.pause();
            }else{
                mPlayer.start();
            }
            refreshViews();
        }
    }
    
    private void refreshViews(){
        if(mPlayer != null){
            if(mPlayer.isPlaying() || !mPlayer.isInPlaybackState()  ||
                    mPlayer.isAdsPlaying()){
                mPlayIcon.setVisibility(View.GONE);
            }else{
                mPlayIcon.setVisibility(View.VISIBLE);
            }
        }
    }
    
    private Runnable mQueryPositionRunner = new Runnable() {
        @Override
        public void run() {
            if(mPlayer != null){
                int position = mPlayer.getCurrentPosition();
                int duration = mPlayer.getDuration();
                if(duration != 0){
                    mProgressView.setProgress(position / (float)duration);
                }
            }
            mUIHandler.postDelayed(mQueryPositionRunner, 1000);
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mUIHandler.post(mQueryPositionRunner);
        refreshViews();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mUIHandler.removeCallbacks(mQueryPositionRunner);
    }
    
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == PortraitVideoController.this){
                togglePlayer();
            }else if(view == mFullscreen){
                if(mOrientationUpdater != null){
                    mOrientationUpdater.requestLandscape();
                }
            }
        }
    };

    @Override
    public void onEpLoadingStart() {
        mPlayIcon.setVisibility(View.GONE);
    }

    @Override
    public void onVideoLoadingStart(IVideoView videoView) {
        mPlayIcon.setVisibility(View.GONE);
    }

    @Override
    public void onCompletion(IVideoView videoView) {
    }

    @Override
    public void onPrepared(IVideoView videoView) {
        refreshViews();
    }

    @Override
    public void onBufferingStart(IVideoView videoView) {
        mPlayIcon.setVisibility(View.GONE);
    }

    @Override
    public void onBufferingEnd(IVideoView videoView) {
        refreshViews();
    }

    @Override
    public void onBufferingPercent(IVideoView videoView, int percent) {
    }

}

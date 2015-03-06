/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   LoadingCycle.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-27
 */
package com.miui.videoplayer.controller;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.videoplayer.videoview.IVideoView;

/**
 * @author tianli
 *
 */
public class LoadingCycle implements IVideoLifeCycle {

    private View mLoadingView;
    private FrameLayout mParent;
    private TextView mLoadingText;

    public LoadingCycle(FrameLayout parent){
        mParent = parent;
    }

    public View getLoadingView(){
        if(mLoadingView == null){
            mLoadingView = View.inflate(mParent.getContext(), R.layout.vp_loading_view, null);
            LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, 
                    LayoutParams.MATCH_PARENT);
            p.gravity = Gravity.CENTER;
            mParent.addView(mLoadingView);
            mLoadingView.setBackgroundColor(0x80000000);
            mLoadingText = (TextView)mParent.findViewById(R.id.loading_textview);
        }
        mLoadingView.bringToFront();
        return mLoadingView;
    }

    @Override
    public void onCompletion(IVideoView videoView) {
    }

    @Override
    public void onPrepared(IVideoView videoView) {
        hideLoading();
    }

    @Override
    public void onBufferingStart(IVideoView videoView) {
        if(videoView.canBuffering()){
            showBuffering();
        }
    }

    @Override
    public void onBufferingEnd(IVideoView videoView) {
        hideLoading();
    }

    @Override
    public void onVideoLoadingStart(IVideoView videoView) {
        if(videoView == null || videoView.canBuffering()){
            showLoading();
        }
    }

    @Override
    public void onEpLoadingStart() {
        showLoading();
    }

    @Override
    public void onBufferingPercent(IVideoView videoView, int percent) {
        if(mLoadingText != null){
            String text = mLoadingText.getResources().getString(
                    R.string.buffering_video_netplay_label_v5);
            text += "(" + percent +"%)";
            mLoadingText.setText(text);
        }
    }

    private void showLoading(){
        getLoadingView().setVisibility(View.VISIBLE);
        mLoadingText.setText(R.string.vp_video_loading);
    }

    private void showBuffering(){
        getLoadingView().setVisibility(View.VISIBLE);
        mLoadingText.setText(R.string.buffering_video_netplay_label_v5);
    }

    private void hideLoading(){
        if(mLoadingView != null){
            getLoadingView().setVisibility(View.GONE);  
        }
    }

}

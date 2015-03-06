package com.miui.videoplayer.controller;

import java.util.ArrayList;
import java.util.List;

import com.miui.videoplayer.videoview.IVideoView;

public class VideoCycleManager implements IVideoLifeCycle{
    
    List<IVideoLifeCycle> mQueue = new ArrayList<IVideoLifeCycle>();

    public void add(IVideoLifeCycle cycle){
        if(cycle != null && !mQueue.contains(cycle)){
            mQueue.add(cycle);
        }
    }
    
    public void remove(IVideoLifeCycle cycle){
        if(cycle != null && mQueue.contains(cycle)){
            mQueue.remove(cycle);
        }
    }
    
    @Override
    public void onCompletion(IVideoView videoView) {
        for(IVideoLifeCycle cycle : mQueue){
            cycle.onCompletion(videoView);
        }
    }

    @Override
    public void onPrepared(IVideoView videoView) {
        for(IVideoLifeCycle cycle : mQueue){
            cycle.onPrepared(videoView);
        }
    }

    @Override
    public void onBufferingStart(IVideoView videoView) {
        for(IVideoLifeCycle cycle : mQueue){
            cycle.onBufferingStart(videoView);
        }
    }

    @Override
    public void onBufferingEnd(IVideoView videoView) {
        for(IVideoLifeCycle cycle : mQueue){
            cycle.onBufferingEnd(videoView);
        }
    }

    @Override
    public void onVideoLoadingStart(IVideoView videoView) {
        for(IVideoLifeCycle cycle : mQueue){
            cycle.onVideoLoadingStart(videoView);
        }
    }

    @Override
    public void onBufferingPercent(IVideoView videoView, int percent) {
        for(IVideoLifeCycle cycle : mQueue){
            cycle.onBufferingPercent(videoView, percent);
        }
    }

    @Override
    public void onEpLoadingStart() {
        for(IVideoLifeCycle cycle : mQueue){
            cycle.onEpLoadingStart();
        }
    }

}

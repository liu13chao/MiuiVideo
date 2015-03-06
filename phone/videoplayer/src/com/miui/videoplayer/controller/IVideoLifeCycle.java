/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   IVideoLifeCycle.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-26
 */
package com.miui.videoplayer.controller;

import com.miui.videoplayer.videoview.IVideoView;

/**
 * @author tianli
 *
 */
public interface IVideoLifeCycle {

    public void onEpLoadingStart();
    
    public void onVideoLoadingStart(IVideoView videoView);
    
    public void onCompletion(IVideoView videoView);
    
    public void onPrepared(IVideoView videoView);
    
    public void onBufferingStart(IVideoView videoView);
    
    public void onBufferingEnd(IVideoView videoView);
    
    public void onBufferingPercent(IVideoView videoView, int percent);
    
    
}

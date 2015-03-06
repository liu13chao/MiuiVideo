/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   VideoHandler.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-12-1
 */
package com.miui.videoplayer.fragment;

import android.os.Looper;

import com.miui.videoplayer.model.SafeHandler;

/**
 * @author tianli
 *
 */
public class VideoProxy extends SafeHandler<VideoFragment>{

    public VideoProxy(VideoFragment ref) {
        super(ref, Looper.getMainLooper());
    }
    
    public void playNext(){
        final VideoFragment ref = getReference();
        if(ref != null){
            post(new Runnable() {
                @Override
                public void run() {
                    ref.playNext();
                }
            });
        }
    }
    
    public void playCi(final int ci){
        final VideoFragment ref = getReference();
        if(ref != null){
            post(new Runnable() {
                @Override
                public void run() {
                    ref.playCi(ci);
                }
            });
        }
    }
    
    public void playSource(final int source, final int resolution){
        final VideoFragment ref = getReference();
        if(ref != null){
            post(new Runnable() {
                @Override
                public void run() {
                    ref.playSource(source, resolution);
                }
            });
        }
    }
    
    public void exitPlayer(){
        final VideoFragment ref = getReference();
        if(ref != null){
            post(new Runnable() {
                @Override
                public void run() {
                    ref.finish();
                }
            });
        }
    }
    
    public void hideController(){
        final VideoFragment ref = getReference();
        if(ref != null){
            post(new Runnable() {
                @Override
                public void run() {
                    ref.hideController();
                }
            });
        }
    }
    
    
}

package com.miui.videoplayer.framework.ui;

import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;

import com.duokan.MediaPlayer.MediaInfo;

public interface LocalMediaPlayerControl extends MediaPlayerControl{
//	//Media player
//    void    start();
//    void    pause();
//    int     getDuration();
//    int     getCurrentPosition();
//    void    seekTo(int pos);
//    boolean isPlaying();
//    int     getBufferPercentage();
//    boolean canPause();
//    boolean canSeekBackward();
//    boolean canSeekForward();
    
    //for switch media
    void startLocalPlayForMediaSwitch(Uri videoUri);
    void stopLocalPlayForMediaSwitch();
    
    //for playTo/tackBackToPhone 
    void startLocalPlayForAirkan(Uri videoUri);
    void stopLocalPlayForAirkan();
 
    //origin codec interface
    void setOnPreparedListener(OnPreparedListener listener);
    void setOnCompletionListener(OnCompletionListener listener);
    void setOnErrorListener(OnErrorListener listener);
    void setOnSeekCompleteListener(OnSeekCompleteListener listener);
    void setOnInfoListener(OnInfoListener listener);
    void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener);
    void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener);
    
    //duokan codec interface
    void setOnPreparedListener(com.duokan.MediaPlayer.OnPreparedListener listener);
    void setOnCompletionListener(com.duokan.MediaPlayer.OnCompletionListener listener);
    void setOnErrorListener(com.duokan.MediaPlayer.OnErrorListener listener);
    void setOnSeekCompleteListener(com.duokan.MediaPlayer.OnSeekCompleteListener listener);
    void setOnInfoListener(com.duokan.MediaPlayer.OnInfoListener listener);
    void setOnBufferingUpdateListener(com.duokan.MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener);
    void setOnVideoSizeChangedListener(com.duokan.MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener);
    void setOnTimedTextListener(com.duokan.MediaPlayer.OnTimedTextListener listener);
    boolean setOutOfBandTextSource(String sourceUri);
    
    //duokan codec method
    boolean enableMultiSpeedPlayback(int speed, boolean forward);
    boolean disableMultiSpeedPlayback();
    MediaInfo getMediaInfo();
    void set3dMode(boolean mode);
    boolean get3dMode();
    
    //extra info
    long getPausedTotalTime();
}

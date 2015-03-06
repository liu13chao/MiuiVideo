/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   IVideoPlayer.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-8-17
 */

package com.miui.videoplayer.media;

import android.view.SurfaceHolder;
import android.widget.Button;

/**
 * @author tianli
 *
 */
public interface IVideoPlayer {
	
	public void onActivityResume();
	
	public void onActivityPause();
	
	public void onActivityDestroy();
	
	public void play(String video, int resolution, Button button, IMediaPlayer adPlayer, IMediaPlayer videoPlayer);
	
	public void play(String video, int resolution, IMediaPlayer videoPlayer);
	
	public void offlineDownload(String video, String path);
	
	public void setAdsPlayListener(AdsPlayListener listener);
	
	public void onSurfaceChanged(SurfaceHolder holder, int format, int w,int h);
	
	public void onSurfaceCreated(SurfaceHolder holder);
	
	public void onSurfaceDestroyed(SurfaceHolder holder);
	
}

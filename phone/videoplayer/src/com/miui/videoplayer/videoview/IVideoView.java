/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   IVideoView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-19
 */

package com.miui.videoplayer.videoview;

import android.view.View;

import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.framework.ui.LocalVideoPlaySizeAdjustable;
import com.miui.videoplayer.media.AdsPlayListener;
import com.miui.videoplayer.media.MediaPlayerControl;
import com.miui.videoplayer.media.IMediaPlayer.OnBufferingUpdateListener;
import com.miui.videoplayer.media.IMediaPlayer.OnCompletionListener;
import com.miui.videoplayer.media.IMediaPlayer.OnErrorListener;
import com.miui.videoplayer.media.IMediaPlayer.OnInfoListener;
import com.miui.videoplayer.media.IMediaPlayer.OnPreparedListener;
import com.miui.videoplayer.media.IMediaPlayer.OnSeekCompleteListener;
import com.miui.videoplayer.media.IMediaPlayer.OnVideoSizeChangedListener;
import com.miui.videoplayer.widget.AdView;

/**
 * @author tianli
 *
 */
public interface IVideoView extends MediaPlayerControl, LocalVideoPlaySizeAdjustable{

	public View asView();
	public void requestVideoLayout();
	
	public void continuePlay(PlayHistoryEntry history);
	
	public boolean hasLoadingAfterAd();
	
	public void setPlayInfo(Object playInfo);
	
	public void onActivityPause();
	public void onActivityResume();
	public void onActivityDestroy();
	
	public boolean isSupportZoom();
	public void setForceFullScreen(boolean forceFullScreen);
	
	public void setAdsPlayListener(AdsPlayListener adPlayListener);
	public void attachAdView(AdView adView);
	
	public void setOnVideoLoadingListener(OnVideoLoadingListener loadingListener);
    public void setOnPreparedListener(OnPreparedListener listener);
    public void setOnCompletionListener(OnCompletionListener listener);
	public void setOnErrorListener(OnErrorListener listener);
	public void setOnSeekCompleteListener(OnSeekCompleteListener listener);
	public void setOnInfoListener(OnInfoListener listener);
	public void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener);
	public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener);
	
	public static interface OnVideoLoadingListener{
		public void onVideoLoading(IVideoView videoView);
		public void onVideoHideLoading(IVideoView videoView);
	}
	
}

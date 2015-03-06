/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaPlayerListener.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-21
 */

package com.miui.videoplayer.media;

import com.miui.videoplayer.media.IMediaPlayer.OnBufferingUpdateListener;
import com.miui.videoplayer.media.IMediaPlayer.OnCompletionListener;
import com.miui.videoplayer.media.IMediaPlayer.OnErrorListener;
import com.miui.videoplayer.media.IMediaPlayer.OnInfoListener;
import com.miui.videoplayer.media.IMediaPlayer.OnPreparedListener;
import com.miui.videoplayer.media.IMediaPlayer.OnSeekCompleteListener;
import com.miui.videoplayer.media.IMediaPlayer.OnVideoSizeChangedListener;


/**
 * @author tianli
 *
 */
public interface OnMediaPlayerListener {
	
	public OnErrorListener getOnErrorListener();
	public OnCompletionListener getOnCompletionListener();
	public OnPreparedListener getOnPreparedListener();
	public OnSeekCompleteListener getOnSeekCompleteListener();
	public OnInfoListener getOnInfoListener();
	public OnBufferingUpdateListener getOnBufferingUpdateListener();
	public OnVideoSizeChangedListener getOnVideoSizeChangedListener();

}

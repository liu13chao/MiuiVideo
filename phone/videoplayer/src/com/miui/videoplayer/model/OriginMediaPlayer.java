/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OriginMediaPlayer.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-18
 */

package com.miui.videoplayer.model;

import java.io.IOException;
import java.util.Map;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.duokan.MediaPlayer.MediaInfo;
import com.miui.videoplayer.media.IDuoKanPlayer;

/**
 * @author tianli
 *
 */
public class OriginMediaPlayer implements IDuoKanPlayer {
	
	private MediaPlayer mMediaPlayer;

	private OnBufferingUpdateListener mBufferingUpdateListener;
	private OnErrorListener mErrorListener;
	private OnCompletionListener mCompletionListener;
	private OnInfoListener mInfoListener;
	private OnPreparedListener mPreparedListener;
	private OnSeekCompleteListener mSeekCompleteListener;
	private OnVideoSizeChangedListener mVideoSizeChangedListener;

	public OriginMediaPlayer(){
		mMediaPlayer = new MediaPlayer();
	}

	@Override
	public int getCurrentPosition() {
		return mMediaPlayer.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	@Override
	public int getVideoHeight() {
		return mMediaPlayer.getVideoHeight();
	}

	@Override
	public int getVideoWidth() {
		return mMediaPlayer.getVideoWidth();
	}

	@Override
	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}

	@Override
	public void pause() throws IllegalStateException {
		mMediaPlayer.pause();
	}

	@Override
	public void prepare() throws IOException, IllegalStateException {
		mMediaPlayer.prepare();
	}

	@Override
	public void prepareAsync() throws IllegalStateException {
		mMediaPlayer.prepareAsync();
	}

	@Override
	public void release() {
		mMediaPlayer.release();
	}

	@Override
	public void reset() {
		mMediaPlayer.reset();
	}

	@Override
	public void seekTo(int ms) throws IllegalStateException {
		mMediaPlayer.seekTo(ms);
	}

	@Override
	public void setDataSource(Context context, Uri uri,
			Map<String, String> headers) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException {
		mMediaPlayer.setDataSource(context, uri, headers);
	}

	@Override
	public void setDataSource(Context context, Uri uri) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException {
		mMediaPlayer.setDataSource(context, uri);
	}

	@Override
	public void setDataSource(String path, Map<String, String> headers)
			throws IOException, IllegalArgumentException, SecurityException,
			IllegalStateException {
		mMediaPlayer.setDataSource(path, headers);
	}

	@Override
	public void setDataSource(String path) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException {
		mMediaPlayer.setDataSource(path);
	}

	@Override
	public void setDisplay(SurfaceHolder sh) {
		mMediaPlayer.setDisplay(sh);
	}
	
	@Override
	public void setSurface(Surface surface) {
		mMediaPlayer.setSurface(surface);
	}

	@Override
	public void setVolume(float leftVolume, float rightVolume) {
		mMediaPlayer.setVolume(leftVolume, rightVolume);
	}

	@Override
	public void start() throws IllegalStateException {
		mMediaPlayer.start();
	}

	@Override
	public void stop() throws IllegalStateException {
		mMediaPlayer.stop();
	}

	@Override
	public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
		mBufferingUpdateListener = listener;
		if(mBufferingUpdateListener != null){
			mMediaPlayer.setOnBufferingUpdateListener(mInnerBufferingUpdateListener);
		}else{
			mMediaPlayer.setOnBufferingUpdateListener(null);
		}
	}
	
	private MediaPlayer.OnBufferingUpdateListener mInnerBufferingUpdateListener = 
			new MediaPlayer.OnBufferingUpdateListener(){
		@Override
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			if(mBufferingUpdateListener != null){
				mBufferingUpdateListener.onBufferingUpdate(OriginMediaPlayer.this, percent);
			}
		}
	};

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		mCompletionListener = listener;
		if(mCompletionListener != null){
			mMediaPlayer.setOnCompletionListener(mInnerCompletionListener);
		}else{
			mMediaPlayer.setOnCompletionListener(null);
		}
	}
	
	private MediaPlayer.OnCompletionListener mInnerCompletionListener = 
			new MediaPlayer.OnCompletionListener(){
		@Override
		public void onCompletion(MediaPlayer mp) {
			if(mCompletionListener != null){
				mCompletionListener.onCompletion(OriginMediaPlayer.this);
			}
		}
	};

	@Override
	public void setOnErrorListener(OnErrorListener listener) {
		mErrorListener = listener;
		if(mErrorListener != null){
			mMediaPlayer.setOnErrorListener(mInnerErrorListener);
		}else{
			mMediaPlayer.setOnErrorListener(null);
		}
	}
	
	private MediaPlayer.OnErrorListener mInnerErrorListener = 
			new MediaPlayer.OnErrorListener(){
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			if(mErrorListener != null){
				return mErrorListener.onError(OriginMediaPlayer.this, what, extra);
			}
			return false;
		}
	};

	@Override
	public void setOnInfoListener(OnInfoListener listener) {
		mInfoListener = listener;
		if(mInfoListener != null){
			mMediaPlayer.setOnInfoListener(mInnerInfoListener);
		}else{
			mMediaPlayer.setOnInfoListener(null);
		}
	}
	
	private MediaPlayer.OnInfoListener mInnerInfoListener = 
			new MediaPlayer.OnInfoListener(){
		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			if(mInfoListener != null){
				return mInfoListener.onInfo(OriginMediaPlayer.this, what, extra);
			}
			return false;
		}
	};

	@Override
	public void setOnPreparedListener(OnPreparedListener listener) {
		mPreparedListener = listener;
		if(mPreparedListener != null){
			mMediaPlayer.setOnPreparedListener(mInnerPreparedListener);
		}else{
			mMediaPlayer.setOnPreparedListener(null);
		}
	}
	
	private MediaPlayer.OnPreparedListener mInnerPreparedListener = 
			new MediaPlayer.OnPreparedListener(){
		@Override
		public void onPrepared(MediaPlayer mp) {
			if(mPreparedListener != null){
				mPreparedListener.onPrepared(OriginMediaPlayer.this);
			}
		}
	};

	@Override
	public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
		mSeekCompleteListener = listener;
		if(mSeekCompleteListener != null){
			mMediaPlayer.setOnSeekCompleteListener(mInnerSeekCompleteListener);
		}else{
			mMediaPlayer.setOnSeekCompleteListener(null);
		}
	}
	
	private MediaPlayer.OnSeekCompleteListener mInnerSeekCompleteListener = 
			new MediaPlayer.OnSeekCompleteListener(){
		@Override
		public void onSeekComplete(MediaPlayer mp) {
			if(mSeekCompleteListener != null){
				mSeekCompleteListener.onSeekComplete(OriginMediaPlayer.this);
			}
		}
	};

	@Override
	public void setOnVideoSizeChangedListener(
			OnVideoSizeChangedListener listener) {
		mVideoSizeChangedListener = listener;
		if(mVideoSizeChangedListener != null){
			mMediaPlayer.setOnVideoSizeChangedListener(mInnerVideoSizeListener);
		}else{
			mMediaPlayer.setOnVideoSizeChangedListener(null);
		}
	}
	
	private MediaPlayer.OnVideoSizeChangedListener mInnerVideoSizeListener = 
			new MediaPlayer.OnVideoSizeChangedListener(){
		@Override
		public void onVideoSizeChanged(MediaPlayer mp, int width,
				int height) {
			if(mVideoSizeChangedListener != null){
				mVideoSizeChangedListener.onVideoSizeChanged(OriginMediaPlayer.this, 
						width, height);
			}
		}
	};

	@Override
	public void setScreenOnWhilePlaying(boolean screenOn) {
		mMediaPlayer.setScreenOnWhilePlaying(screenOn);
	}

	@Override
	public MediaInfo getMediaInfo() {
		return null;
	}
	@Override
	public boolean get3dMode() {
		return false;
	}
	@Override
	public void set3dMode(boolean mode) {
	}
}

package com.miui.videoplayer.media;

import java.util.Map;

import com.duokan.MediaPlayer.MediaInfo;
import android.net.Uri;

public interface MediaPlayerControl {
	void setDataSource(String uri);
	void setDataSource(String uri, Map<String, String> headers);
	void start();
	void pause();
	int getDuration();
	int getCurrentPosition();
	int getRealPlayPosition();
	void seekTo(int pos);
	boolean isPlaying();
	boolean isInPlaybackState();
	int getBufferPercentage();
	boolean canPause();
	boolean canSeekBackward();
	boolean canSeekForward();
	boolean canBuffering();
	boolean isAirkanEnable();
	void close();
	
	Uri getUri();
	
	public boolean isAdsPlaying();
	MediaInfo getMediaInfo();
	public boolean get3dMode();
	public void set3dMode(boolean mode);
}

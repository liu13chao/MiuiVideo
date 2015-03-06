package com.miui.videoplayer.framework.airkan;

import com.miui.videoplayer.media.MediaPlayerControl;

public interface RemoteMediaPlayerControlNew extends MediaPlayerControl {
	
	public float getVolume();
	
	public void setVolume(float volume);
	
	public void playTo(String deviceName);
	
	public void takeBack();
	
	public void stop();
	
	public void startPlay();
	
	public void setVideoUri(String url, String title, int position);
	
	public void setSpName(String spName);

}

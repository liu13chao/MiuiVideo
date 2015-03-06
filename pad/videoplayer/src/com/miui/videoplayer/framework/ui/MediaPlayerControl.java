package com.miui.videoplayer.framework.ui;

public interface MediaPlayerControl {
	void start();
	void pause();
	int getDuration();
	int getCurrentPosition();
	void seekTo(int pos);
	boolean isPlaying();
	int getBufferPercentage();
	boolean canPause();
	boolean canSeekBackward();
	boolean canSeekForward();
}

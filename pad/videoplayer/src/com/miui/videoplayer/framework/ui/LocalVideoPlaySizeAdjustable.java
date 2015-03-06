package com.miui.videoplayer.framework.ui;

public interface LocalVideoPlaySizeAdjustable {
	void adjustVideoPlayViewSize(int width, int height, boolean auto);
	int getVideoWidth();
	int getVideoHeight();
}

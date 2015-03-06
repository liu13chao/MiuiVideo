package com.miui.videoplayer.model;

import com.miui.video.api.def.MediaConstantsDef;


public class OnlineEpisodeSource {

	private int mSource;

	private int mResolution;

	public OnlineEpisodeSource() {
		mSource = MediaConfig.MEDIASOURCE_UNKNOWN_TYPE_CODE;
		mResolution = MediaConstantsDef.CLARITY_NORMAL;
	}

	public OnlineEpisodeSource(int source, int resolution) {
		mSource = source;
		mResolution = resolution;
	}

	public int getSource() {
		return mSource;
	}

	public void setSource(int source) {
		mSource = source;
	}

	public int getResolution() {
		return mResolution;
	}

	public void setResolution(int resolution) {
		mResolution = resolution;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof OnlineEpisodeSource)) {
			return false;
		}
		OnlineEpisodeSource that = (OnlineEpisodeSource) o;
		return this.mSource == that.mSource
				&& this.mResolution == that.mResolution;
	}
}

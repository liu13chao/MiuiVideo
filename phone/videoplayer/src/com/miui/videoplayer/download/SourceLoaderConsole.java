package com.miui.videoplayer.download;

import com.miui.video.type.SourceInfo;

import android.content.Context;


public abstract class SourceLoaderConsole extends LoaderConsole {

	protected SourceInfo mInfo;
	public SourceLoaderConsole(Context context, String remoteUrl,
			String localPath) {
		super(context, remoteUrl, localPath);
	}

	public void setSourceInfo(SourceInfo info){
		mInfo = info;
	}
	
	@Override
	protected void downloadStart() {
	}

	@Override
	protected void downloadProgress(int completed, int total) {

	}

	@Override
	protected void downloadComplete(final String path) {
		onLoadComplete(path);
	}

	@Override
	protected void downloadError(int error) {
	}
	
	protected abstract void onLoadComplete(String path);
	
	protected abstract String getStartMessage();
}

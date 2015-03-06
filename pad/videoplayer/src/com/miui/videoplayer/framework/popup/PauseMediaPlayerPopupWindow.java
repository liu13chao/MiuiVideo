package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.view.View;

import com.miui.videoplayer.framework.airkan.RemoteTVMediaPlayerControl;
import com.miui.videoplayer.framework.ui.MediaPlayerControl;

public class PauseMediaPlayerPopupWindow extends ManagedPopupWindow {
	private VpCtrlFullScreenPopupWindow mFullScreenPopupWindow;
	private MediaPlayerControl mMediaPlayerControl;
	private Context mContext;
	private boolean mAirkanPaused = false;
	private boolean mVideoIsPlaying = false;
	
	public PauseMediaPlayerPopupWindow(Context context, View contentView, int width, int height) {
		super(contentView, width, height);
		
		this.mContext = context;
	}

	public PauseMediaPlayerPopupWindow(Context context, View contentView) {
		super(contentView);
		
		this.mContext = context;
	}
	
	public void show(View anchor, MediaPlayerControl mediaPlayerControl) {
		mVideoIsPlaying = false;
		mMediaPlayerControl = mediaPlayerControl;
		mAirkanPaused = false;
		if (mMediaPlayerControl != null) {
			if (mMediaPlayerControl instanceof RemoteTVMediaPlayerControl) {
				RemoteTVMediaPlayerControl remotePlayerControl = (RemoteTVMediaPlayerControl) mMediaPlayerControl;
				mAirkanPaused = !remotePlayerControl.isPlaying();
			}
			if (mMediaPlayerControl.isPlaying()) {
				mVideoIsPlaying = true;
				mMediaPlayerControl.pause();
			}
			updateAirkanPlayingState(false);
		}
		if (mFullScreenPopupWindow == null) {
			mFullScreenPopupWindow = new VpCtrlFullScreenPopupWindow(mContext);
		}
		mFullScreenPopupWindow.show(anchor);
		show(anchor);
	}
	
	@Override
	public void show(View anchor) {
	}
	
	@Override
	public void dismiss() {
		if (mMediaPlayerControl != null && !mMediaPlayerControl.isPlaying()) {
			if ((!(mMediaPlayerControl instanceof RemoteTVMediaPlayerControl) && mVideoIsPlaying) 
					|| ((mMediaPlayerControl instanceof RemoteTVMediaPlayerControl) && !mAirkanPaused)) {
				mMediaPlayerControl.start();
			}
		}
		updateAirkanPlayingState(true);
		if (mFullScreenPopupWindow != null && mFullScreenPopupWindow.isShowing()) {
			mFullScreenPopupWindow.dismiss();
		}
		mAirkanPaused = false;
		super.dismiss();
	}

	@Override
	public void dismiss(boolean remove) {
		if (mFullScreenPopupWindow != null && mFullScreenPopupWindow.isShowing()) {
			mFullScreenPopupWindow.dismiss();
		}
		super.dismiss(remove);
	}
	
	protected void updateAirkanPlayingState(boolean playing) {
		if (mMediaPlayerControl instanceof RemoteTVMediaPlayerControl) {
			RemoteTVMediaPlayerControl remoteMediaPlayerControl = (RemoteTVMediaPlayerControl) mMediaPlayerControl;
			remoteMediaPlayerControl.updatePlayingState(playing);
			remoteMediaPlayerControl.showMediaController();
		}
	}
}

package com.miui.videoplayer.framework.popup;

import com.miui.videoplayer.framework.ui.MediaPlayerControlNew;

import android.net.Uri;
import android.view.View;

public interface IAirKanDevicesPopupWindow {
	void setVideoUri(Uri uri);
	boolean isShowing();
    void show(View anchor, MediaPlayerControlNew mediaPlayerControl);
    void dismiss();
}

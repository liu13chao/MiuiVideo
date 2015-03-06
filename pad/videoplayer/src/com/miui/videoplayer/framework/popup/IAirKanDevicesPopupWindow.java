package com.miui.videoplayer.framework.popup;

import com.miui.videoplayer.framework.ui.MediaPlayerControl;

import android.net.Uri;
import android.view.View;

public interface IAirKanDevicesPopupWindow {
	void setVideoUri(Uri uri);
	boolean isShowing();
    void show(View anchor, MediaPlayerControl mediaPlayerControl);
    void dismiss();
}

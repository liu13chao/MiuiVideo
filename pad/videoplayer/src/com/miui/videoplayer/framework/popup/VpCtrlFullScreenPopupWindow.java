package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;

import com.miui.video.R;

public class VpCtrlFullScreenPopupWindow extends ManagedPopupWindow {

	public VpCtrlFullScreenPopupWindow(Context context) {
		super(LayoutInflater.from(context).inflate(R.layout.vp_popup_ctrl_fullscreen, null), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}
	
	public void show(View anchor) {
		showAtLocation(anchor, Gravity.TOP, 0, 0);
	}
	
}

package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.miui.video.R;

public class BufferFullScreenPopupWindow extends PopupWindow {
	
	private TextView mBufferedPercentTextView;
	
	public BufferFullScreenPopupWindow(Context context) {
		super(LayoutInflater.from(context).inflate(R.layout.vp_popup_buffer_fullscreen, null), 
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.setAnimationStyle(R.style.full_screen_pause_popup_anim_style);
		init();
	}
	
	private void init() {
		mBufferedPercentTextView = (TextView) getContentView().findViewById(R.id.buffer_percent_textview);
	}

	public void show(View anchor, String mediaName) {
		mBufferedPercentTextView.setText("");
		showAtLocation(anchor, Gravity.TOP, 0, 0);
	}

	public void setBufferedPercent(int percent) {
		mBufferedPercentTextView.setText("(" + percent + "%)");
	}
}

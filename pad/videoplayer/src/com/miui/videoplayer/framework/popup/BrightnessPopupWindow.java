package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.miui.video.R;
import com.miui.videoplayer.framework.DuoKanConstants;

public class BrightnessPopupWindow extends AbstractVerticalSeekbarPopupWindows {


	public BrightnessPopupWindow(Context context) {
		super(LayoutInflater.from(context).inflate(R.layout.vp_popup_left_vertical_seebar_group, null), android.widget.FrameLayout.LayoutParams.WRAP_CONTENT, context);
		
		this.setMaxSeekbarValue(DuoKanConstants.BRIGHTNESS_MAX_VALUE);
	}

	public BrightnessPopupWindow(Context context, int layoutId) {
		super(LayoutInflater.from(context).inflate(layoutId, null), android.widget.FrameLayout.LayoutParams.WRAP_CONTENT, context);
		
		this.setMaxSeekbarValue(DuoKanConstants.BRIGHTNESS_MAX_VALUE);
	}
	
	@Override
	protected void layoutSeekbarGroup() {
		SeekBar mSeekbar = getSeekbar();
		mSeekbar.setRotation(-90f);
	}

	@Override
	public void show(View anchor) {
	
	}
	
	public void show(View anchor, Context context) {
		this.showAtLocation(anchor, Gravity.LEFT| Gravity.CENTER_VERTICAL, 0, 0);		
	}

}

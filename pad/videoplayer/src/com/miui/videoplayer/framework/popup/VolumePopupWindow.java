package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import com.miui.video.R;

public class VolumePopupWindow extends AbstractVerticalSeekbarPopupWindows {
	
	public VolumePopupWindow(Context context) {
		super(LayoutInflater.from(context).inflate(R.layout.vp_popup_right_vertical_seekbar_group, null), ViewGroup.LayoutParams.WRAP_CONTENT, context);
	}
	
	public VolumePopupWindow(Context context, int layoutId) {
		super(LayoutInflater.from(context).inflate(layoutId, null), ViewGroup.LayoutParams.WRAP_CONTENT, context);
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
		this.showAtLocation(anchor, Gravity.RIGHT| Gravity.CENTER_VERTICAL, 0, 0);
	}
	
}

package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.videoplayer.common.DKTimeFormatter;

public class ProgressBarOnlyPopupWindow extends PopupWindow {
	private SeekBar mSeekbar;
	private TextView mPositionTextView;
	private TextView mDurationTextView;
	private int mDuration;
	
	public ProgressBarOnlyPopupWindow(Context context) {
		super(LayoutInflater.from(context).inflate(R.layout.vp_popup_bottom_progress_bar_only, null));
		
		this.setWidth(LayoutParams.MATCH_PARENT);
		int height = (int) context.getResources().getDimension(R.dimen.popup_bottom_progress_bar_only_height);
		this.setHeight(height);
		
		ColorDrawable backgroudColorDrawable = new ColorDrawable(context.getResources().getColor(R.color.vp_black));
		backgroudColorDrawable.setAlpha(179);
		this.setBackgroundDrawable(backgroudColorDrawable);
		
		setupWindows();
	}

	private void setupWindows() {
		
		
		View contentView = getContentView();
//		contentView.setAlpha(0.7f);
		mSeekbar = (SeekBar) contentView.findViewById(R.id.mediacontroller_progress);
		mSeekbar.setMax(1000);
		mSeekbar.setPressed(true);
		mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			private int originalProgress; 
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				  originalProgress = seekBar.getProgress(); 
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) { 
	                seekBar.setProgress(originalProgress); 
	            }
			}
		});
		
		mPositionTextView = (TextView) contentView.findViewById(R.id.time_current);
		mDurationTextView = (TextView) contentView.findViewById(R.id.time);
	}
	
	public void show(View anchor) {
		showAtLocation(anchor, Gravity.BOTTOM, 0, 0);
	}
	
	public void setDuration(int duration) {
		mDurationTextView.setText(DKTimeFormatter.getInstance().stringForTime(duration));
		mDuration = duration;
	}
	
	public void updatePosition(int position) {
		mPositionTextView.setText(DKTimeFormatter.getInstance().stringForTime(position));
		if (mDuration != 0) {
			mSeekbar.setProgress((int) ((1000L * position) / mDuration ));
		}
	}
	
}

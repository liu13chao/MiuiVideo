package com.miui.videoplayer.framework.popup;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.miui.video.R;
import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.common.DuoKanConstants;

public class BrightnessPopupWindow extends AbstractVerticalSeekbarPopupWindows {

	private static final int BRIGHTNESS_VOLUME_DISMISS_TIME = 3000;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	
	public BrightnessPopupWindow(Context context) {
		super(LayoutInflater.from(context).inflate(R.layout.vp_popup_left_vertical_seebar_group, null), android.widget.FrameLayout.LayoutParams.WRAP_CONTENT, context);
		
		this.setMaxSeekbarValue(DuoKanConstants.BRIGHTNESS_MAX_VALUE);
	}

	public BrightnessPopupWindow(Context context, int layoutId) {
		super(LayoutInflater.from(context).inflate(layoutId, null), android.widget.FrameLayout.LayoutParams.WRAP_CONTENT, context);
		
		this.setMaxSeekbarValue(DuoKanConstants.BRIGHTNESS_MAX_VALUE);
	}
	
	public BrightnessPopupWindow adjustBrightness(Activity activity, float distanceY) {
		int currentValue = (int) (AndroidUtils.getActivityBrightness(activity) * 255);
		if (currentValue < 0) {
			currentValue = AndroidUtils.getSystemBrightness(activity);
		}
		int newValue = getNewBrightnessValue(distanceY, currentValue);
		AndroidUtils.setActivityBrightness(activity, newValue);
		updateSeekbarValue(newValue / DuoKanConstants.BRIGHTNESS_STEP);
		mHandler.removeCallbacks(mDissmissRunnable);
		mHandler.postDelayed(mDissmissRunnable, BRIGHTNESS_VOLUME_DISMISS_TIME);
		return this;
	}
	
	private int getNewBrightnessValue(float distanceY, int currentValue) {
		int newValue = 0;
		if (distanceY > 0) {
			newValue = currentValue - DuoKanConstants.BRIGHTNESS_STEP;
		} else {
			newValue = currentValue + DuoKanConstants.BRIGHTNESS_STEP;
		}
		if (newValue > 255) {
			newValue = 255;
		}
		if (newValue < 2) {
			newValue = 2;
		}
		return newValue;
	}
	
	@Override
	protected void layoutSeekbarGroup() {
		SeekBar mSeekbar = getSeekbar();
		mSeekbar.setRotation(-90f);
	}
	
	public void show(View anchor, Context context) {
		this.showAtLocation(anchor, Gravity.LEFT| Gravity.CENTER_VERTICAL, 0, 0);		
		mHandler.removeCallbacks(mDissmissRunnable);
		mHandler.postDelayed(mDissmissRunnable, BRIGHTNESS_VOLUME_DISMISS_TIME);
	}

	private Runnable mDissmissRunnable = new Runnable() {
		@Override
		public void run() {
			try{
				if(isShowing()){
					dismiss();
				}
			}catch (Exception e) {
			}
		}
	};
}

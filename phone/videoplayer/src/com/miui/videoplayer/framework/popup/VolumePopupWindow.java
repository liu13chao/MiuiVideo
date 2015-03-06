package com.miui.videoplayer.framework.popup;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.miui.video.R;

public class VolumePopupWindow extends AbstractVerticalSeekbarPopupWindows {
	private static final int BRIGHTNESS_VOLUME_DISMISS_TIME = 3000;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	
	public VolumePopupWindow(Context context) {
		super(LayoutInflater.from(context).inflate(R.layout.vp_popup_right_vertical_seekbar_group, null), ViewGroup.LayoutParams.WRAP_CONTENT, context);
	}
	
	public VolumePopupWindow(Context context, int layoutId) {
		super(LayoutInflater.from(context).inflate(layoutId, null), ViewGroup.LayoutParams.WRAP_CONTENT, context);
	}
	
	public VolumePopupWindow adjustVolume(Activity activity, float distanceY) {
		AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
		final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		final int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		int newValue = getNewVolumeValue(distanceY, maxVolume, currentVolume);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newValue, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		updateSeekbarValue(newValue);
		mHandler.removeCallbacks(mDissmissRunnable);
		mHandler.postDelayed(mDissmissRunnable, BRIGHTNESS_VOLUME_DISMISS_TIME);
		return this;
	}

	private int getNewVolumeValue(float distanceY, int maxVolume, int currentVolume) {
		int newValue = 0;
		if (distanceY > 0) {
			newValue = currentVolume - 1;
		} else {
			newValue = currentVolume + 1;
		}
		if (newValue > maxVolume) {
			newValue = maxVolume;
		}
		if (newValue < 0) {
			newValue = 0;
		}
		return newValue;
	}
	
	@Override
	protected void layoutSeekbarGroup() {
		SeekBar mSeekbar = getSeekbar();
		mSeekbar.setRotation(-90f);		
	}
	
	public void show(View anchor, Context context) {
		this.showAtLocation(anchor, Gravity.RIGHT| Gravity.CENTER_VERTICAL, 0, 0);
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

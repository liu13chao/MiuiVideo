/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   WindowFactory.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-13
 */

package com.miui.videoplayer.framework.popup;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;

import com.miui.video.R;
import com.miui.videoplayer.common.DuoKanConstants;
import com.miui.videoplayer.common.AndroidUtils;

/**
 * @author tianli
 *
 */
public class WindowFactory {
	
	public static BrightnessPopupWindow createBrightnessWindow(Activity activity){
		BrightnessPopupWindow window = new BrightnessPopupWindow(activity, 
				R.layout.vp_popup_left_vertical_seebar_group);
		int currentValue = (int) (AndroidUtils.getActivityBrightness(activity) * 255);
		if (currentValue < 0) {
			currentValue = AndroidUtils.getSystemBrightness(activity);
		}
		window.updateSeekbarValue(currentValue / DuoKanConstants.BRIGHTNESS_STEP);
		return window;
	}
	
	public static VolumePopupWindow createVolumeWindow(Activity activity){
		 VolumePopupWindow window = new VolumePopupWindow(activity, 
				 R.layout.vp_popup_right_vertical_seekbar_group);
		AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
		final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		final int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		window.setMaxSeekbarValue(maxVolume);
		window.updateSeekbarValue(currentVolume);
		window.getSeekbar().setPressed(false);
		return window;
	}
}

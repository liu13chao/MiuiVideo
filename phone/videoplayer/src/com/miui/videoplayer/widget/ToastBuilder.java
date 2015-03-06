/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   ToastBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-6
 */

package com.miui.videoplayer.widget;

import com.miui.video.R;
import com.miui.videoplayer.common.DKTimeFormatter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * @author tianli
 *
 */
public class ToastBuilder {

	@SuppressLint("ShowToast")
	public static Toast buildContinuePlay(Context context, int position){
		String fromString = context.getResources().getString(R.string.toast_message_continue_play_from);
		String hourString = context.getResources().getString(R.string.toast_message_continue_play_hour);
		String minuteString = context.getResources().getString(R.string.toast_message_continue_play_minute);
		String secondString = context.getResources().getString(R.string.toast_message_continue_play_second_and_play);
		DKTimeFormatter timeFormatter = DKTimeFormatter.getInstance();
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(fromString);
		if (timeFormatter.getHoursForTime(position) != 0) {
			sBuilder.append(timeFormatter.getHoursForTime(position)).append(hourString);
		}
		sBuilder.append(timeFormatter.getMinutesForTime(position)).append(minuteString);
		sBuilder.append(timeFormatter.getSecondsForTime(position)).append(secondString);

		Toast toast = Toast.makeText(context, sBuilder.toString(), Toast.LENGTH_SHORT);
		return toast;
	}
}

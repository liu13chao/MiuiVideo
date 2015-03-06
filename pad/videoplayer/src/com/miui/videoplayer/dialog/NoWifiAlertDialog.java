package com.miui.videoplayer.dialog;

import android.app.AlertDialog;
import android.content.Context;

import com.miui.video.R;

public class NoWifiAlertDialog extends AlertDialog {

	public NoWifiAlertDialog(Context context, int theme) {
		super(context, theme);
		String title = context.getResources().getString(R.string.nowifi_alert_dialog_title);
		String message = context.getResources().getString(R.string.nowifi_alert_dialog_message);
		this.setMessage(message);
		this.setTitle(title);
	}

	public NoWifiAlertDialog(Context context) {
		super(context);
	}
	
}

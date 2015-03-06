package com.miui.videoplayer.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;

import com.miui.video.R;
import com.miui.videoplayer.common.Constants;

public class AlertDialogApkFactory {
	
	public static AlertDialog createStartDialogWithAsk(Context context, String name){
		String message = context.getResources().getString(R.string.vp_client_message, name);
		AlertDialog mAlertDialog = new AlertDialog.Builder(context, miui.R.style.Theme_Light_Dialog_Alert)
		.setCancelable(false)
		.setTitle(R.string.vp_install_title)
		.setMessage(message)
		.create();
		return mAlertDialog;
	}
	
	
	public static  AlertDialog createLoadingDialog(Context context){
		View view = View.inflate(context, R.layout.vp_horizontal_load_view, null);
		AlertDialog mAlertDialog = new AlertDialog.Builder(context, miui.R.style.Theme_Light_Dialog_Alert)
		.setCancelable(false)
		.setView(view).create();
		return mAlertDialog;
	}
	
	public static AlertDialog createErrorDialog(Context context, int error){
		final Resources res = context.getResources();
		final String message;
		if (error == Constants.OFFLINE_STATE_FILE_ERROR) {
			message = res.getString(R.string.vp_download_file_fail);
		} else if (error == Constants.OFFLINE_STATE_CONNECT_ERROR
				|| error == Constants.OFFLINE_STATE_SOURCE_ERROR) {
			message = res.getString(R.string.vp_download_connect_fail);
		} else {
			message = "";
		}
		
		AlertDialog mAlertDialog = new AlertDialog.Builder(context, miui.R.style.Theme_Light_Dialog_Alert)
		.setCancelable(false)
		.setTitle(R.string.vp_install_title)
		.setMessage(message)
		.setPositiveButton(R.string.vp_retry,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
					}
				})
		.setNegativeButton(R.string.vp_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.dismiss();
					}
				}).create();
		return mAlertDialog;
	}
}

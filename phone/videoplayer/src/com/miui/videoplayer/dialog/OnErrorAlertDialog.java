package com.miui.videoplayer.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;

import com.miui.video.R;
import com.miui.videoplayer.common.AndroidUtils;

public class OnErrorAlertDialog extends AlertDialog {

	public OnErrorAlertDialog(Context context, int theme) {
		super(context, theme);
	 	setTitle(R.string.vp_VideoView_error_title);
        setCancelable(false);
	}
	
	public OnErrorAlertDialog(Context context) {
//		this(context, miui.R.style.V5_Theme_Light_Dialog_Alert);
		super(context);
	}
	
	public static OnErrorAlertDialog build(final Activity context, Uri uri, int what){
		int messageId = R.string.vp_VideoView_error_text_unknown;
		if (what == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
			messageId = R.string.vp_VideoView_error_text_invalid_progressive_playback;
		}
		if (AndroidUtils.isOnlineVideo(uri) && !AndroidUtils.isNetworkConncected(context)) {
			messageId = R.string.vp_VideoView_error_network_not_available;
		}
		final OnErrorAlertDialog dialog = new OnErrorAlertDialog(context, miui.R.style.Theme_Light_Dialog_Alert);
		dialog.setMessage(context.getString(messageId));
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(
				R.string.vp_VideoView_error_button), new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int id) {
		 		dialog.dismiss();
		 		context.finish();
			}
			
		});
		return dialog;
	}
}

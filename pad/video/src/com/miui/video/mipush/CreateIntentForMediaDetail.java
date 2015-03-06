package com.miui.video.mipush;

import com.miui.video.dialog.MediaDetailDialogFragment;
import com.miui.video.util.DKLog;
import com.xiaomi.miui.pushads.sdk.NotifyAdsCell;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class CreateIntentForMediaDetail implements IntentCreator {

	private final String TAG = CreateIntentForMediaDetail.class.getName();
	
	private Context context;
	private NotifyAdsCell cell;
	public CreateIntentForMediaDetail(Context context, NotifyAdsCell cell) {
		this.context = context;
		this.cell = cell;
	}

	@Override
	public Intent creatIntent() {
		Uri uri = Uri.parse(cell.actionUrl);
		String mediaidStr = uri.getQueryParameter("mediaid");
		int mediaid = -1;
		try {
			mediaid = Integer.parseInt(mediaidStr);
		} catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		
		Intent clickIntent = new Intent(context, MediaDetailDialogFragment.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(MediaDetailDialogFragment.KEY_MEDIA_ID, mediaid);
		clickIntent.putExtras(bundle);
		return clickIntent;
	}
}

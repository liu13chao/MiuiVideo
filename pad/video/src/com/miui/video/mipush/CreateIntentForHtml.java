package com.miui.video.mipush;

import com.miui.video.OpenHtml5ForAdsActivity;
import com.xiaomi.miui.pushads.sdk.NotifyAdsCell;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class CreateIntentForHtml implements IntentCreator {
	
	private Context context;
	private NotifyAdsCell cell;
	public CreateIntentForHtml(Context context, NotifyAdsCell cell) {
		this.context = context;
		this.cell = cell;
	}

	@Override
	public Intent creatIntent() {
		Uri uri = Uri.parse(cell.actionUrl);
		String url = uri.getQueryParameter("url");
		Intent clickIntent = new Intent(context, OpenHtml5ForAdsActivity.class);
		clickIntent.putExtra("url", url);
		return clickIntent;
	}

}

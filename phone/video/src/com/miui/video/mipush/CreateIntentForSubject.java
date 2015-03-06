package com.miui.video.mipush;

import android.content.Context;
import android.content.Intent;

import com.miui.video.MainActivity;
import com.xiaomi.miui.pushads.sdk.NotifyAdsCell;

public class CreateIntentForSubject implements IntentCreator {

	private Context context;
	public CreateIntentForSubject(Context context, NotifyAdsCell cell) {
		this.context = context;
	}

	@Override
	public Intent creatIntent() {
		Intent clickIntent = new Intent(context, MainActivity.class);
		return clickIntent;
	}

}

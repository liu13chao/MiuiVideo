package com.miui.video.mipush;

import com.miui.video.FeatureListActivity;
import com.xiaomi.miui.pushads.sdk.NotifyAdsCell;

import android.content.Context;
import android.content.Intent;

public class CreateIntentForSubject implements IntentCreator {

	private Context context;
	public CreateIntentForSubject(Context context, NotifyAdsCell cell) {
		this.context = context;
	}

	@Override
	public Intent creatIntent() {
		Intent clickIntent = new Intent(context, FeatureListActivity.class);
		return clickIntent;
	}

}
